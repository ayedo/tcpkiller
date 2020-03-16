package ch.ayedo.portmanager.views

import ch.ayedo.portmanager.services.OperationSystem
import ch.ayedo.portmanager.services.ProcessService
import ch.ayedo.portmanager.services.ProcessTerminator
import tornadofx.*

class MainView : View() {

    private val os = OperationSystem.current()

    private val processService = ProcessService.forOperationSystem(os)

    private val processTerminator = ProcessTerminator.forOperationSystem(os)

    private val processView = ProcessTableView(processService, processTerminator)

    override val root = vbox(10) {

        style {
            padding = box(10.px)
        }

        textfield {
            promptText = "Filter Ports"
            processView.filterByPort(textProperty())
        }

        processView.root.prefWidthProperty().bind(this.widthProperty())
        processView.root.prefHeightProperty().bind(this.heightProperty())

        add(processView)

        button("End Task") {
            action {
                processView.killSelectedProcess()
            }
        }

    }
}

