package ch.ayedo.portkiller

import java.nio.file.Paths

interface ProcessTerminator {
    fun terminate(processId: ProcessId)
}


class UnixKillProcessTerminator : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        Paths.get(".").toFile() exec "kill ${processId.value}"
    }
}
