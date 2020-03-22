package ch.ayedo.tcpkiller.services

import ch.ayedo.tcpkiller.services.OperationSystem.*
import org.zeroturnaround.exec.InvalidExitValueException

class RequiredToolNotFoundException(name: String) :
    IllegalStateException("Cannot find required commandline tool: $name")

interface ToolUtility {

    fun toolExists(name: String): Boolean

    fun requireTool(name: String) {
        if (!toolExists(name)) {
            throw RequiredToolNotFoundException(name)
        }
    }

    companion object {
        fun forOperationSystem(os: OperationSystem, runner: CommandLineRunner): ToolUtility =
            when (os) {
                WINDOWS -> WhereToolUtility(runner)
                MAC, LINUX -> WhichToolUtility(runner)
            }
    }
}

class WhichToolUtility(private val runner: CommandLineRunner) : ToolUtility {
    override fun toolExists(name: String): Boolean {
        return try {
            runner.run("which $name")
            true
        } catch (ex: InvalidExitValueException) {
            false
        }
    }
}

class WhereToolUtility(private val runner: CommandLineRunner) : ToolUtility {
    override fun toolExists(name: String): Boolean {
        return try {
            runner.run("where.exe $name")
            true
        } catch (ex: InvalidExitValueException) {
            false
        }
    }

}
