package ch.ayedo.portkiller.views

import ch.ayedo.portkiller.services.PortBinding
import ch.ayedo.portkiller.services.ProcessService
import ch.ayedo.portkiller.services.ProcessTerminator
import com.google.common.collect.Sets
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

    init {
        Observable.interval(
            1,
            1,
            TimeUnit.SECONDS
        ).subscribe({ reloadBindings() })

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
    }

    fun filterByPort(textProperty: ObservableValue<String>) {
        sortedPortBindings.filterWhen(textProperty) { query, item ->
            item.port.value.toString().startsWith(query)
        }
    }

    fun killSelectedProcess() {
        root.selectionModel.selectedItem?.let { binding ->
            processTerminator.terminate(binding.process.processId)
            this.reloadBindings()
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
