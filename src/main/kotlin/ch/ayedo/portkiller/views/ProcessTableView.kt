package ch.ayedo.portkiller.views

import ch.ayedo.portkiller.services.PortBinding
import ch.ayedo.portkiller.services.Process
import ch.ayedo.portkiller.services.ProcessService
import ch.ayedo.portkiller.services.ProcessTerminator
import io.reactivex.rxjava3.core.Observable
import javafx.beans.value.ObservableValue
import tornadofx.*
import java.util.concurrent.TimeUnit

class ProcessTableView(
    private val processService: ProcessService,
    private val processTerminator: ProcessTerminator
) : View() {

    private val portBindings = processService.processPortBindings().toList().asObservable()

    private val sortedPortBindings = SortedFilteredList(portBindings)

    private var selectedProcess: Process? = null

    init {
        Observable.interval(
            2,
            2,
            TimeUnit.SECONDS
        ).subscribe({ onRefresh() })
    }

    override val root = tableview(sortedPortBindings) {
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
        readonlyColumn("Port", PortBinding::intPort).cellFormat {
            text = it.toString()
        }
        readonlyColumn("Process", PortBinding::processName).cellFormat {
            text = it
        }
        resizeColumnsToFitContent()
        onUserSelect(clickCount = 1) {
            selectedProcess = it.process
        }
    }

    fun filterByPort(textProperty: ObservableValue<String>) {
        sortedPortBindings.filterWhen(textProperty) { query, item ->
            item.port.value.toString().startsWith(query)
        }
    }

    fun killSelectedProcess() {
        selectedProcess?.let { process ->
            processTerminator.terminate(process.processId)
            this.onRefresh()
        }
    }

    override fun onRefresh() {
        this.sortedPortBindings.asyncItems { processService.processPortBindings().toList() }
    }
}
