package ch.ayedo.portkiller.services

inline class Port(val value: Int)

inline class ProcessId(val value: Int)

inline class ProcessName(val value: String)

data class Process(val processId: ProcessId, val processName: ProcessName)

data class PortBinding(val process: Process, val port: Port) {

    val processName: String = process.processName.value

    val intPort: Int = port.value

}
