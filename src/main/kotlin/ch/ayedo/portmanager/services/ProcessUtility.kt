package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*
import ch.ayedo.portmanager.whitespaceRegex
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

interface ProcessUtility {

    fun processNamesById(): Map<ProcessId, ProcessName>

    companion object {

        fun forOperationSystem(
            os: OperationSystem,
            runner: CommandLineRunner,
            toolFinder: ToolFinder
        ): ProcessUtility {

            val processUtility =
                when (os) {
                    WINDOWS -> TasklistProcessUtility(runner, toolFinder)
                    MAC, LINUX -> PsProcessUtility(runner, toolFinder)
                }

            val jpsExists = toolFinder.toolExists("jps")

            if (!jpsExists) {
                return processUtility
            }

            return JpsProcessUtility.wrap(os, processUtility, runner, toolFinder)
        }

    }

}

class PsProcessUtility(
    private val runner: CommandLineRunner,
    toolFinder: ToolFinder
) : ProcessUtility {

    init {
        toolFinder.requireTool("ps")
    }

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val psResult = runner.run("ps -Ao pid,command -c")

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

class TasklistProcessUtility(
    private val runner: CommandLineRunner,
    toolFinder: ToolFinder
) : ProcessUtility {

    init {
        toolFinder.requireTool("tasklist")
    }

    private val csvReader = csvReader()

    override fun processNamesById(): Map<ProcessId, ProcessName> {
        val tasklistResult = runner.run("tasklist /svc /fo csv")

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
    private val runner: CommandLineRunner,
    private val javaProcessName: String,
    toolFinder: ToolFinder
) : ProcessUtility {

    init {
        toolFinder.requireTool("jps")
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
            toolFinder: ToolFinder
        ): JpsProcessUtility = when (os) {
            WINDOWS -> JpsProcessUtility(processUtility, runner, "java.exe", toolFinder)
            MAC, LINUX -> JpsProcessUtility(processUtility, runner, "java", toolFinder)
        }
    }

}
