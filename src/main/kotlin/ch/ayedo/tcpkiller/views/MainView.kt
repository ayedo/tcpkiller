package ch.ayedo.tcpkiller.views

import ch.ayedo.tcpkiller.services.IanaTcpPortReservations
import ch.ayedo.tcpkiller.services.OperationSystem
import ch.ayedo.tcpkiller.services.ProcessService
import javafx.beans.property.StringProperty
import javafx.geometry.Pos.BASELINE_RIGHT
import tornadofx.*


class MainView : View() {

    private val os = OperationSystem.current()

    private val processService = ProcessService.forOperationSystem(os)

    private val ianaReservations =
        IanaTcpPortReservations(this.javaClass.getResourceAsStream("/service-names-port-numbers.csv"))

    private val processView = ProcessTableView(processService, ianaReservations)

    override val root = vbox(10) {

        title = getOsDependentTitle()

        style {
            padding = box(10.px)
        }

        textfield {
            promptText = "Filter Ports"
            textProperty().addListener({ observable, oldValue, newValue ->
                if (newValue.isNotBlank() && !newValue.isInt()) {
                    (observable as StringProperty).value = oldValue
                }
            })
            processView.filterByPort(textProperty())
        }

        processView.root.prefWidthProperty().bind(this.widthProperty())
        processView.root.prefHeightProperty().bind(this.heightProperty())

        add(processView)

        hbox {
            alignment = BASELINE_RIGHT
            button("End Process") {
                action {
                    processView.killSelectedProcess()
                }
            }

        }
    }

    private fun getOsDependentTitle() = if (os == OperationSystem.MAC) "" else "PortKiller"
}

