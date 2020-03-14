package ch.ayedo.portkiller.services

import ch.ayedo.portkiller.exec
import java.nio.file.Paths

interface ProcessUtility {
    fun processNamesById(): Map<ProcessId, ProcessName>
}


class PsProcessUtility : ProcessUtility {

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val psResult = Paths.get(".").toFile() exec "ps -Ao pid,command -c"

        val rows = psResult.split("\n")
            .drop(1)
            .dropLast(1)
            .map({ row -> row.trim() })

        return rows.map({ row ->
            val columns = row.split(" ")
            val pid = columns[0].toInt()
            val name = columns[1]
            ProcessId(pid) to ProcessName(
                name
            )
        }).toMap()
    }
}

class JpsProcessUtility(private val processUtility: ProcessUtility) :
    ProcessUtility {

    override fun processNamesById(): Map<ProcessId, ProcessName> {

        val processNamesById = processUtility.processNamesById()

        return processNamesById.entries.associateBy(
            { it.key },
            { if (it.value.value == "java") (jpsLookup(it.key) ?: it.value) else it.value })
    }

    private fun jpsLookup(processId: ProcessId): ProcessName? {

        val jpsResult = Paths.get(".").toFile() exec "jps"

        val rows = jpsResult.split("\n").dropLast(1)

        val pidsToNames =
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

        return pidsToNames[processId]
    }

}
