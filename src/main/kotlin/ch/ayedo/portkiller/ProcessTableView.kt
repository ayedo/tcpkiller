package ch.ayedo.portkiller

import io.reactivex.rxjava3.core.Observable
import javafx.beans.value.ObservableValue
import tornadofx.*
import java.util.concurrent.TimeUnit

class ProcessTableView : View() {

    private val processService = ProcessService(
        LsofNetworkUtility(),
        JpsProcessUtility(PsProcessUtility())
    )

    private val processTerminator = UnixKillProcessTerminator()

    private val portBindings = processService.processPortBindings().asObservable()

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
        readonlyColumn("Port", PortBinding::port).cellFormat {
            text = it.value.toString()
        }
        readonlyColumn("Process", PortBinding::process).cellFormat {
            text = it.processName.value
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
        this.sortedPortBindings.asyncItems { processService.processPortBindings() }
    }
}
