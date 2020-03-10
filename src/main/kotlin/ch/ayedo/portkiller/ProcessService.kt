package ch.ayedo.portkiller

inline class Port(val value: Int)

inline class ProcessId(val value: Int)

inline class ProcessName(val value: String)

data class Process(val processId: ProcessId, val processName: ProcessName)

data class PortBinding(val process: Process, val port: Port) {
    override fun toString(): String =
        "PortBinding(processId=${process.processId.value}, processName=${process.processName.value}, port=$port)"
}

class ProcessService(private val networkUtility: NetworkUtility, private val processUtility: ProcessUtility) {

    fun processPortBindings(): List<PortBinding> {

        val pidsToPorts = networkUtility.processIdPortMappings()

        val pidsToNames = processUtility.processNamesById()

        return pidsToPorts.map({ (processId, port) ->

            val processName = pidsToNames.getValue(processId)

            val process = Process(processId = processId, processName = processName)

            PortBinding(process = process, port = port)

        })

    }
}
