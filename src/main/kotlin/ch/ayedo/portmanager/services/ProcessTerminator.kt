package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

interface ProcessTerminator {

    fun terminate(processId: ProcessId)

    companion object {
        fun forOperationSystem(os: OperationSystem, runner: CommandLineRunner): ProcessTerminator =
            when (os) {
                WINDOWS -> WindowsTaskKillProcessTerminator(runner)
                LINUX, MAC -> UnixKillProcessTerminator(runner)
            }
    }
}

class UnixKillProcessTerminator(private val runner: CommandLineRunner) : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        runner.run("kill ${processId.value}")
    }
}

class WindowsTaskKillProcessTerminator(private val runner: CommandLineRunner) : ProcessTerminator {
    override fun terminate(processId: ProcessId) {
        runner.run("taskkill /t /f /PID ${processId.value}")
    }
}
