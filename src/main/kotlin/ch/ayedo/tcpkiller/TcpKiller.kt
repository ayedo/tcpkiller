package ch.ayedo.tcpkiller

import ch.ayedo.tcpkiller.services.OperationSystem
import ch.ayedo.tcpkiller.services.OperationSystem.MAC
import ch.ayedo.tcpkiller.views.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch


class TcpKiller : App(MainView::class) {

    override fun start(stage: Stage) {
        with(stage) {
            width = 800.0
            height = 600.0
        }

        if (OperationSystem.current() != MAC) {
            stage.icons.add(Image(this::class.java.getResourceAsStream("/logo.png")));
        }

        super.start(stage)
    }

}

fun main(args: Array<String>) {
    launch<TcpKiller>(args)
}
