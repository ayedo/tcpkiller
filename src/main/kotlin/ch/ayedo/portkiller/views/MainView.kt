package ch.ayedo.portkiller.views

import ch.ayedo.portkiller.services.*
import tornadofx.*

class MainView : View() {

    private val processService = ProcessService(
        LsofNetworkUtility(),
        JpsProcessUtility(PsProcessUtility())
    )

    private val processTerminator = UnixKillProcessTerminator()

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

