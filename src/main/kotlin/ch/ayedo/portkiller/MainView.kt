package ch.ayedo.portkiller

import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class MainView : View() {

    // alright so what's the plan?
    // I want to have a box where I can enter a port number
    // then it queries, and shows the process the listens on that port number
    // -> we need to execute a command depending on the operating system
    // maybe even do a jps to figure out which one it is?
    // then we can kill it with a click

    //  if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)

    // ok, so I need to find out how to do something when somebody clicks on OK
    override val root = vbox {
        label("Port")
        textfield("8080")
        button("OK") {
            action {
                "lsof -nP -i:8080 | grep LISTEN".execute()
            }
        }
        text("adid.exe") {
            fill = Color.PURPLE
            font = Font(20.0)
        }
    }
}
