package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*
import ch.ayedo.portmanager.whitespaceRegex
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

interface ProcessUtility {
    fun processNamesById(): Map<ProcessId, ProcessName>
}

class PsProcessUtility(private val cmd: CommandLineRunner) : ProcessUtility {

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val psResult = cmd.run("ps -Ao pid,command -c")

        val rows = psResult.lines()
            .drop(1)
            .dropLast(1)
            .map(String::trim)

        return rows.map({ row ->
            val columns = row.split(whitespaceRegex)
            val pid = columns[0].toInt()
            val name = columns[1]
            ProcessId(pid) to ProcessName(
                name
            )
        }).toMap()
    }
}

class TasklistProcessUtility(private val cmd: CommandLineRunner) : ProcessUtility {

    private val csvReader = csvReader()

    override fun processNamesById(): Map<ProcessId, ProcessName> {
        val tasklistResult = cmd.run("tasklist /svc /fo csv")

        val rows = csvReader.readAllWithHeader(tasklistResult)

        return rows.map({ row ->
            val pid = row.getValue("PID").toInt()
            val name = row.getValue("Image Name")

            ProcessId(pid) to ProcessName(
                name
            )
        }).toMap()
    }
}

class JpsProcessUtility(
    private val processUtility: ProcessUtility,
    private val cmd: CommandLineRunner,
    private val javaProcessName: String
) : ProcessUtility {

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val processNamesById = processUtility.processNamesById()

        return processNamesById.entries.associateBy(
            { it.key },
            { if (it.value.value == javaProcessName) (jpsLookup(it.key) ?: it.value) else it.value })
    }

    private fun jpsLookup(processId: ProcessId): ProcessName? {

        val jpsResult = cmd.run("jps")

        val rows = jpsResult.lines().dropLast(1)

        val processIdsToNames =
            rows.mapNotNull({ row ->

                val columns = row.split(" ")
                val pid = columns[0].trim().toInt()
                val name = columns[1]

                if (name.isNotBlank()) {
                    ProcessId(pid) to ProcessName(
                        "java ($name)"
                    )
                } else {
                    null
                }
            }).toMap()

        return processIdsToNames[processId]
    }

    companion object {

        fun create(
            os: OperationSystem,
            processUtility: ProcessUtility,
            cmd: CommandLineRunner
        ): JpsProcessUtility = when (os) {
            WINDOWS -> JpsProcessUtility(processUtility, cmd, "java.exe")
            MAC, LINUX -> JpsProcessUtility(processUtility, cmd, "java")
        }
    }

}
