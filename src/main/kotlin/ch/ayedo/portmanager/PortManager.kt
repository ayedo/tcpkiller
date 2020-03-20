package ch.ayedo.portmanager

import ch.ayedo.portmanager.views.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch


class PortManager : App(MainView::class) {

    override fun start(stage: Stage) {
        with(stage) {
            width = 800.0
            height = 600.0
            title = "Port Manager"
        }

        stage.icons.add(Image(this::class.java.getResourceAsStream("/logo.png")));

        super.start(stage)
    }

}

fun main(args: Array<String>) {
    launch<PortManager>(args)
}
