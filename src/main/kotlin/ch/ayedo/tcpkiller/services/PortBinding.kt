package ch.ayedo.tcpkiller.services

inline class Port(val value: Int) : Comparable<Port> {
    override fun compareTo(other: Port) = value.compareTo(other.value)
}

inline class ProcessId(val value: Int) : Comparable<ProcessId> {
    override fun compareTo(other: ProcessId) = value.compareTo(other.value)
}

inline class ProcessName(val value: String) : Comparable<ProcessName> {

    companion object {
        val notFound = ProcessName("<< Process name not found >>")
    }

    override fun compareTo(other: ProcessName) = value.compareTo(other.value, ignoreCase = true)
}

data class Process(val processId: ProcessId, val processName: ProcessName)

data class PortBinding(val process: Process, val port: Port) {
    val processName = process.processName
    val processId = process.processId
}
