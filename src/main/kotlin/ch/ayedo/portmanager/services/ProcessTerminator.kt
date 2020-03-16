package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

interface ProcessTerminator {

    fun terminate(processId: ProcessId)

    companion object {
        fun forOperationSystem(os: OperationSystem): ProcessTerminator {
            val cmd = CommandLineRunner()
            return when (os) {
                WINDOWS -> WindowsTaskKillProcessTerminator(cmd)
                LINUX, MAC -> UnixKillProcessTerminator(cmd)
            }
        }
    }
}

class UnixKillProcessTerminator(private val cmd: CommandLineRunner) : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        cmd.run("kill ${processId.value}")
    }
}

class WindowsTaskKillProcessTerminator(private val cmd: CommandLineRunner) : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        cmd.run("taskkill /t /f /PID ${processId.value}")
    }
}
