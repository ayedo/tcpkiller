package ch.ayedo.portmanager.services

class ProcessService(
    private val networkUtility: NetworkUtility,
    private val processUtility: ProcessUtility
) {

    fun processPortBindings(): Iterable<PortBinding> {

        val pidsToPorts = networkUtility.processIdPortMappings()

        val pidsToNames = processUtility.processNamesById()

        return pidsToPorts.map({ (processId, port) ->

            val processName = pidsToNames.getOrDefault(processId, ProcessName.notFound)

            val process = Process(
                processId = processId,
                processName = processName
            )

            PortBinding(process = process, port = port)

        })

    }

    companion object {

        fun forOperationSystem(os: OperationSystem, runner: CommandLineRunner): ProcessService {

            val toolFinder = ToolFinder.forOperationSystem(os, runner)

            val processUtility = ProcessUtility.forOperationSystem(os, runner, toolFinder)

            val networkUtility = NetworkUtility.forOperationSystem(os, runner)

            return ProcessService(
                networkUtility,
                processUtility
            )
        }
    }
}
