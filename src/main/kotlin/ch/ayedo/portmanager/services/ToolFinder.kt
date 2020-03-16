package ch.ayedo.portmanager.services

import ch.ayedo.portmanager.services.OperationSystem.*

interface ToolFinder {
    fun toolExists(name: String): Boolean

    companion object {
        fun forOperationSystem(os: OperationSystem): ToolFinder {
            val cmd = CommandLineRunner()
            return when (os) {
                WINDOWS -> WhereToolFinder(cmd)
                MAC, LINUX -> WhichToolFinder(cmd)
            }
        }
    }
}

class WhichToolFinder(private val cmd: CommandLineRunner) : ToolFinder {
    override fun toolExists(name: String): Boolean {
        return try {
            cmd.run("which $name")
            true
        } catch (ex: Exception) {
            false
        }
    }
}

class WhereToolFinder(private val cmd: CommandLineRunner) : ToolFinder {
    override fun toolExists(name: String): Boolean {
        return try {
            cmd.run("where.exe $name")
            true
        } catch (ex: Exception) {
            false
        }
    }

}
