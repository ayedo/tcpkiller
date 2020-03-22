package ch.ayedo.portmanager.views

import ch.ayedo.portmanager.services.IanaTcpPortReservations
import ch.ayedo.portmanager.services.PortBinding
import ch.ayedo.portmanager.services.ProcessService
import com.google.common.collect.Sets
import io.reactivex.rxjava3.core.Observable
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import tornadofx.*
import java.util.concurrent.TimeUnit.SECONDS

class ProcessTableView(
    private val processService: ProcessService,
    private val ianaReservations: IanaTcpPortReservations
) : View() {

    private val portBindings = processService.processPortBindings().toList().asObservable()

    private val sortedPortBindings = SortedFilteredList(portBindings)

    init {
        Observable.interval(
            1,
            1,
            SECONDS
        ).subscribe({ reloadBindings() })

    }

    override val root = tableview(sortedPortBindings) {

        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)

        readonlyColumn("Port", PortBinding::port).cellFormat { port ->

            val intPort = port.value

            text = intPort.toString()

            val reservation = ianaReservations.reservations[port]

            val tooltipText = if (reservation.isNotEmpty()) {
                reservation.joinToString("\n", transform = { it.description })
            } else {
                "No IANA reservations found for port $intPort"
            }

            tooltip(tooltipText)

        }

        readonlyColumn("Process Name", PortBinding::processName).cellFormat { processName ->
            text = processName.value
        }
        readonlyColumn("Process Id", PortBinding::processId).cellFormat { processId ->
            text = processId.value.toString()
        }

        resizeColumnsToFitContent()

        placeholder = label("No processes found")
    }

    fun filterByPort(textProperty: ObservableValue<String>) {
        sortedPortBindings.filterWhen(textProperty) { query, item ->
            item.port.value.toString().startsWith(query)
        }
    }

    fun killSelectedProcess() {
        root.selectionModel.selectedItem?.let { binding ->
            Platform.runLater {
                processService.terminate(binding.process.processId)
                this.reloadBindings()
            }
        }
    }

    object PortBindingLock

    private fun reloadBindings() {
        synchronized(PortBindingLock) {
            val previous = this.portBindings.toSet()
            val current = processService.processPortBindings().toSet()
            val toRemove = Sets.difference(previous, current)
            val toAdd = Sets.difference(current, previous)
            this.portBindings.removeAll(toRemove)
            this.portBindings.addAll(toAdd)
        }

    }
}
