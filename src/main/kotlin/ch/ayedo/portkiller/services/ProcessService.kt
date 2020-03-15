package ch.ayedo.portkiller.services

import ch.ayedo.portkiller.services.OperationSystem.*

class ProcessService(
    private val networkUtility: NetworkUtility,
    private val processUtility: ProcessUtility
) {

    fun processPortBindings(): Iterable<PortBinding> {

        val pidsToPorts = networkUtility.processIdPortMappings()

        val pidsToNames = processUtility.processNamesById()

        return pidsToPorts.map({ (processId, port) ->

            val processName = pidsToNames.getValue(processId)

            val process = Process(
                processId = processId,
                processName = processName
            )

            PortBinding(process = process, port = port)

        })

    }

    companion object {

        fun forOperationSystem(os: OperationSystem): ProcessService {

            val toolFinder = ToolFinder.forOperationSystem(os)

            fun requireTools(vararg names: String) {
                for (name in names) {
                    if (!toolFinder.toolExists(name)) {
                        throw IllegalStateException("Cannot run required commandline tool: $name")
                    }
                }
            }

            val jpsExists = toolFinder.toolExists("jps")

            return when (os) {
                WINDOWS -> {
                    requireTools("taskkill", "netstat")
                    val processUtility =
                        if (jpsExists) JpsProcessUtility(TasklistProcessUtility()) else TasklistProcessUtility()
                    ProcessService(
                        WindowsNetstatNetworkUtility(),
                        processUtility
                    )
                }
                MAC -> {
                    requireTools("kill", "lsof")
                    val processUtility = if (jpsExists) JpsProcessUtility(PsProcessUtility()) else PsProcessUtility()
                    ProcessService(
                        LsofNetworkUtility(),
                        processUtility
                    )
                }
                LINUX -> TODO()
            }
        }
    }
}
