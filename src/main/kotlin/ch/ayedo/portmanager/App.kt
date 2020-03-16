package ch.ayedo.portmanager

import ch.ayedo.portmanager.views.MainView
import javafx.stage.Stage
import tornadofx.App


class PortManager : App(MainView::class) {
    override fun start(stage: Stage) {
        with(stage) {
            width = 800.0
            height = 600.0
            title = "Port Manager"
        }
        super.start(stage)
    }
}