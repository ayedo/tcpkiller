package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

interface ProcessTerminator {

    fun terminate(processId: ProcessId)

    companion object {
        fun forOperationSystem(
            os: OperationSystem,
            runner: CommandLineRunner,
            toolUtility: ToolUtility
        ): ProcessTerminator =
            when (os) {
                WINDOWS -> WindowsTaskKillProcessTerminator(runner, toolUtility)
                LINUX, MAC -> UnixKillProcessTerminator(runner, toolUtility)
            }
    }
}

class UnixKillProcessTerminator(
    private val runner: CommandLineRunner,
    toolUtility: ToolUtility
) : ProcessTerminator {

    init {
        toolUtility.requireTool("kill")
    }

    override fun terminate(processId: ProcessId) {
        runner.run("kill ${processId.value}")
    }
}

class WindowsTaskKillProcessTerminator(
    private val runner: CommandLineRunner,
    toolUtility: ToolUtility
) : ProcessTerminator {

    init {
        toolUtility.requireTool("taskkill")
    }

    override fun terminate(processId: ProcessId) {
        runner.run("taskkill /t /f /PID ${processId.value}")
    }
}
