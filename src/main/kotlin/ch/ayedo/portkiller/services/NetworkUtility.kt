package ch.ayedo.portkiller.services

import ch.ayedo.portkiller.exec
import com.google.common.net.HostAndPort
import java.nio.file.Paths

interface NetworkUtility {
    /**
     * Returns a list of mappings from all process ids which are in TCP LISTEN state,
     * and the ports they are bound to.
     *
     * IMPORTANT: If an Process is listening on both IPv4 and IPv6 only one mapping is expected to be returned
     * **/
    fun processIdPortMappings(): Iterable<Pair<ProcessId, Port>>
}

private val whitespaceRegex = "\\s+".toRegex()

class LsofNetworkUtility() : NetworkUtility {

    override fun processIdPortMappings(): Iterable<Pair<ProcessId, Port>> {

        val lsofResult = Paths.get(".").toFile() exec "lsof -a -itcp -nP -sTCP:LISTEN"

        // The expected result of running lsof is expected in the following example format:
        // LastPassH   453 ayedo    4u  IPv6 0x3b950fb5c09e6679      0t0  TCP [::1]:19536 (LISTEN)

        val portMappings = lsofResult
            .lines()
            // remove the column names from the output
            .drop(1)
            // remove empty line at the end of the output
            .dropLast(1)
            // we are only interested in the pid, and the port
            .map({ row ->
                val columns = row.split(whitespaceRegex)

                val pid = columns[1].toInt()
                val hostAndPort = HostAndPort.fromString(columns[8])

                ProcessId(pid) to Port(
                    hostAndPort.port
                )
            })

        // respect contract to return only one mapping for both IPv4 and IPv6
        return portMappings.toSet()
    }
}

class WindowsNetstatNetworkUtility : NetworkUtility {
    override fun processIdPortMappings(): Iterable<Pair<ProcessId, Port>> {
        
        val netstatResult = Paths.get(".").toFile() exec "netstat -p TCP -anvo"

        val portMappings = netstatResult
            .lines()
            .drop(4)
            .map(String::trim)
            .mapNotNull({ row ->
                val columns = row.split(whitespaceRegex)

                // special case: SystemEventsBroker
                if (columns.size != 3) {
                    return@mapNotNull null
                }

                val state = columns[3]

                if (state.trim() != "LISTENING") {
                    return@mapNotNull null
                }

                val pid = columns[4].toInt()
                val hostAndPort = HostAndPort.fromString(columns[1])

                ProcessId(pid) to Port(
                    hostAndPort.port
                )
            })

        return portMappings.toSet()
    }

}
