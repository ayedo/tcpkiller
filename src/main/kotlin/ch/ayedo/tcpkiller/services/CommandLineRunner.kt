package ch.ayedo.tcpkiller.services

import ch.ayedo.tcpkiller.whitespaceRegex
import org.zeroturnaround.exec.ProcessExecutor

class CommandLineRunner {

    fun run(command: String): String =
        ProcessExecutor()
            .command(command.split(whitespaceRegex))
            .readOutput(true)
            .exitValueNormal()
            .execute()
            .outputUTF8()
}

