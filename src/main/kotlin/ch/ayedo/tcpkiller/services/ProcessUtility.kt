package ch.ayedo.tcpkiller.services

import ch.ayedo.tcpkiller.services.OperationSystem.*
import ch.ayedo.tcpkiller.whitespaceRegex
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

interface ProcessUtility {

    fun processNamesById(): Map<ProcessId, ProcessName>

    companion object {

        fun forOperationSystem(
            os: OperationSystem,
            runner: CommandLineRunner,
            toolUtility: ToolUtility
        ): ProcessUtility {

            val processUtility =
                when (os) {
                    WINDOWS -> TasklistProcessUtility(runner, toolUtility)
                    MAC, LINUX -> PsProcessUtility(runner, toolUtility)
                }

            val jpsExists = toolUtility.toolExists("jps")

            if (!jpsExists) {
                return processUtility
            }

            return JpsProcessUtility.wrap(os, processUtility, runner, toolUtility)
        }

    }

}

class PsProcessUtility(
    private val runner: CommandLineRunner,
    toolUtility: ToolUtility
) : ProcessUtility {

    init {
        toolUtility.requireTool("ps")
    }

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val psResult = runner.run("ps -Ao pid,command -c")

        val rows = psResult.lines()
            .drop(1)
            .dropLast(1)
            .map(String::trim)

        return rows.map({ row ->

            val columns = row.split(whitespaceRegex, 2)
            val pid = columns[0].toInt()
            val name = columns[1]

            ProcessId(pid) to ProcessName(name)

        }).toMap()
    }
}

class TasklistProcessUtility(
    private val runner: CommandLineRunner,
    toolUtility: ToolUtility
) : ProcessUtility {

    init {
        toolUtility.requireTool("tasklist")
    }

    private val csvReader = csvReader()

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val tasklistResult = runner.run("tasklist /svc /fo csv")

        val rows = csvReader.readAllWithHeader(tasklistResult)

        return rows.map({ row ->
            val pid = row.getValue("PID").toInt()
            val imageName = row.getValue("Image Name")
            val services = row.getValue("Services")

            val name = imageName + if (services == "N/A") "" else " ($services)"

            ProcessId(pid) to ProcessName(name)

        }).toMap()
    }
}

class JpsProcessUtility(
    private val processUtility: ProcessUtility,
    private val runner: CommandLineRunner,
    private val javaProcessName: String,
    toolUtility: ToolUtility
) : ProcessUtility {

    init {
        toolUtility.requireTool("jps")
    }

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val processNamesById = processUtility.processNamesById()

        return processNamesById.entries.associateBy(
            { it.key },
            { if (it.value.value == javaProcessName) (jpsLookup(it.key) ?: it.value) else it.value })
    }

    private fun jpsLookup(processId: ProcessId): ProcessName? {

        val jpsResult = runner.run("jps")

        val rows = jpsResult.lines().dropLast(1)

        val processIdsToNames =
            rows.mapNotNull({ row ->

                val columns = row.split(" ")
                val pid = columns[0].trim().toInt()
                val name = columns[1]

                if (name.isNotBlank()) {
                    ProcessId(pid) to ProcessName(
                        "$javaProcessName ($name)"
                    )
                } else {
                    null
                }
            }).toMap()

        return processIdsToNames[processId]
    }

    companion object {

        fun wrap(
            os: OperationSystem,
            processUtility: ProcessUtility,
            runner: CommandLineRunner,
            toolUtility: ToolUtility
        ): JpsProcessUtility = when (os) {
            WINDOWS -> JpsProcessUtility(processUtility, runner, "java.exe", toolUtility)
            MAC, LINUX -> JpsProcessUtility(processUtility, runner, "java", toolUtility)
        }
    }

}
