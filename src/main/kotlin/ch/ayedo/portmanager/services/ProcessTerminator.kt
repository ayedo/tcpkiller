package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

interface ProcessTerminator {

    fun terminate(processId: ProcessId)

    companion object {
        fun forOperationSystem(
            os: OperationSystem,
            runner: CommandLineRunner,
            toolFinder: ToolFinder
        ): ProcessTerminator =
            when (os) {
                WINDOWS -> WindowsTaskKillProcessTerminator(runner, toolFinder)
                LINUX, MAC -> UnixKillProcessTerminator(runner, toolFinder)
            }
    }
}

class UnixKillProcessTerminator(
    private val runner: CommandLineRunner,
    toolFinder: ToolFinder
) : ProcessTerminator {

    init {
        toolFinder.requireTool("kill")
    }

    override fun terminate(processId: ProcessId) {
        runner.run("kill ${processId.value}")
    }
}

class WindowsTaskKillProcessTerminator(
    private val runner: CommandLineRunner,
    toolFinder: ToolFinder
) : ProcessTerminator {

    init {
        toolFinder.requireTool("taskkill")
    }

    override fun terminate(processId: ProcessId) {
        runner.run("taskkill /t /f /PID ${processId.value}")
    }
}
