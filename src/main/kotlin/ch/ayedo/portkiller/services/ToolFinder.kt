package ch.ayedo.portkiller.services

import ch.ayedo.portkiller.exec
import ch.ayedo.portkiller.services.OperationSystem.*
import java.nio.file.Paths

interface ToolFinder {
    fun toolExists(name: String): Boolean

    companion object {
        fun forOperationSystem(os: OperationSystem): ToolFinder {
            return when (os) {
                WINDOWS -> WhereToolFinder()
                MAC, LINUX -> WhichToolFinder()
            }
        }
    }
}

class WhichToolFinder : ToolFinder {
    override fun toolExists(name: String): Boolean {
        return try {
            Paths.get(".").toFile() exec "which $name"
            true
        } catch (ex: Exception) {
            false
        }
    }
}

class WhereToolFinder : ToolFinder {
    override fun toolExists(name: String): Boolean {
        return try {
            Paths.get(".").toFile() exec "where.exe $name"
            true
        } catch (ex: Exception) {
            false
        }
    }

}
