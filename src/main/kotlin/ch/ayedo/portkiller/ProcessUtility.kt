package ch.ayedo.portkiller

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
            ProcessId(pid) to ProcessName(name)
        }).toMap()
    }
}
