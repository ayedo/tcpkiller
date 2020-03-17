package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

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

            val cmd = CommandLineRunner()

            return when (os) {
                WINDOWS -> {
                    requireTools("taskkill", "netstat")

                    val tasklist = TasklistProcessUtility(cmd)

                    val processUtility =
                        if (jpsExists) {
                            JpsProcessUtility.create(os, tasklist, cmd)
                        } else tasklist

                    ProcessService(
                        WindowsNetstatNetworkUtility(cmd),
                        processUtility
                    )
                }
                MAC -> {
                    requireTools("kill", "lsof")

                    val ps = PsProcessUtility(cmd)

                    val processUtility =
                        if (jpsExists) {
                            JpsProcessUtility.create(os, ps, cmd)
                        } else ps

                    ProcessService(
                        LsofNetworkUtility(cmd),
                        processUtility
                    )
                }
                LINUX -> TODO()
            }
        }
    }
}
