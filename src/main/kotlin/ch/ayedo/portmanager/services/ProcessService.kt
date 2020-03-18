package ch.ayedo.portmanager.services

class ProcessService(
    private val networkUtility: NetworkUtility,
    private val processUtility: ProcessUtility,
    private val processTerminator: ProcessTerminator
) {

    fun processPortBindings(): Iterable<PortBinding> {

        val pidsToPorts = networkUtility.processIdPortMappings()

        val pidsToNames = processUtility.processNamesById()

        return pidsToPorts.map({ (processId, port) ->

            val processName = pidsToNames.getOrDefault(processId, ProcessName.notFound)

            val process = Process(processId, processName)

            PortBinding(process, port)

        })

    }

    fun terminate(processId: ProcessId) {
        processTerminator.terminate(processId)
    }

    companion object {

        fun forCurrentOperationSystem(): ProcessService {
            val os = OperationSystem.current()
            return forOperationSystem(os)
        }

        private fun forOperationSystem(os: OperationSystem): ProcessService {

            val runner = CommandLineRunner()

            val toolFinder = ToolFinder.forOperationSystem(os, runner)

            val processTerminator = ProcessTerminator.forOperationSystem(os, runner, toolFinder)

            val processUtility = ProcessUtility.forOperationSystem(os, runner, toolFinder)

            val networkUtility = NetworkUtility.forOperationSystem(os, runner, toolFinder)

            return ProcessService(
                networkUtility,
                processUtility,
                processTerminator
            )
        }
    }
}
