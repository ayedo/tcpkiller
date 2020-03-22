package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.IanaTcpPortReservations.CsvHeaders.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import tornadofx.isInt
import java.io.InputStream

data class IanaTcpPortReservation(val serviceName: String, val description: String, val port: Port)

class IanaTcpPortReservations(ianaFormattedCsvFile: InputStream) {

    private enum class CsvHeaders(val title: String) {
        TRANSPORT_PROTOCOL("Transport Protocol"),
        SERVICE_NAME("Service Name"),
        DESCRIPTION("Description"),
        PORT_NUMBER("Port Number")
    }

    private val ianaPortAssignments =
        csvReader().readAllWithHeader(ianaFormattedCsvFile)

    private val tcpPortAssignments =
        ianaPortAssignments
            .asSequence()
            .filter({ row -> row[TRANSPORT_PROTOCOL.title] == "tcp" })
            .filter({ row -> row[SERVICE_NAME.title]?.isNotBlank() ?: false })
            .filter({ row -> row[DESCRIPTION.title]?.isNotBlank() ?: false })
            .filter({ row -> row[PORT_NUMBER.title]?.isInt() ?: false })
            .map({
                IanaTcpPortReservation(
                    serviceName = it.getValue(SERVICE_NAME.title),
                    description = it.getValue(DESCRIPTION.title),
                    port = Port(it.getValue(PORT_NUMBER.title).toInt())
                )
            })
            .toList()

    val reservations: Multimap<Port, IanaTcpPortReservation> = run {

        val multimap = HashMultimap.create<Port, IanaTcpPortReservation>()

        tcpPortAssignments.forEach({
            multimap.put(it.port, it)
        })

        multimap
    }

}


