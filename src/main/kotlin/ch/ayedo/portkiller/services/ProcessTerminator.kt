package ch.ayedo.portkiller.services

import ch.ayedo.portkiller.exec
import ch.ayedo.portkiller.services.OperationSystem.*
import java.nio.file.Paths

interface ProcessTerminator {
    fun terminate(processId: ProcessId)

    companion object {
        fun forOperationSystem(os: OperationSystem): ProcessTerminator {
            return when (os) {
                WINDOWS -> WindowsTaskKillProcessTerminator()
                LINUX, MAC -> UnixKillProcessTerminator()
            }
        }
    }
}

class UnixKillProcessTerminator : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        Paths.get(".").toFile() exec "kill ${processId.value}"
    }
}

class WindowsTaskKillProcessTerminator : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        Paths.get(".").toFile() exec "taskkill /PID ${processId.value}"
    }
}
