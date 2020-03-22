package ch.ayedo.portmanager.views

import ch.ayedo.portmanager.services.IanaTcpPortReservations
import ch.ayedo.portmanager.services.ProcessService
import tornadofx.*

class MainView : View("Port Manager") {

    private val processService = ProcessService.forCurrentOperationSystem()

    private val ianaReservations =
        IanaTcpPortReservations(this.javaClass.getResourceAsStream("/service-names-port-numbers.csv"))

    private val processView = ProcessTableView(processService, ianaReservations)

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

        button("End Process") {
            action {
                processView.killSelectedProcess()
            }
        }

    }
}

