package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.whitespaceRegex
import org.zeroturnaround.exec.ProcessExecutor

class CommandLineRunner {
    fun run(command: String): String =
        ProcessExecutor()
            .command(command.split(whitespaceRegex))
            .readOutput(true)
            .execute()
            .outputUTF8()
}

