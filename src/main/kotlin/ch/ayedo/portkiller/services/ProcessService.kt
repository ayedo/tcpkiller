package ch.ayedo.portkiller.services

class ProcessService(private val networkUtility: NetworkUtility, private val processUtility: ProcessUtility) {

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
}
