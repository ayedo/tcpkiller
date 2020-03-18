package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

class RequiredToolNotFoundException(name: String) :
    IllegalStateException("Cannot find required commandline tool: $name")

interface ToolFinder {

    fun toolExists(name: String): Boolean

    fun requireTool(name: String) {
        if (!toolExists(name)) {
            throw RequiredToolNotFoundException(name)
        }
    }

    companion object {
        fun forOperationSystem(os: OperationSystem, runner: CommandLineRunner): ToolFinder =
            when (os) {
                WINDOWS -> WhereToolFinder(runner)
                MAC, LINUX -> WhichToolFinder(runner)
            }
    }
}

class WhichToolFinder(private val runner: CommandLineRunner) : ToolFinder {
    override fun toolExists(name: String): Boolean {
        return try {
            runner.run("which $name")
            true
        } catch (ex: Exception) {
            false
        }
    }
}

class WhereToolFinder(private val runner: CommandLineRunner) : ToolFinder {
    override fun toolExists(name: String): Boolean {
        return try {
            runner.run("where.exe $name")
            true
        } catch (ex: Exception) {
            false
        }
    }

}
