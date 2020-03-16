package ch.ayedo.portmanager.services

import com.sun.javafx.PlatformUtil

enum class OperationSystem {
    WINDOWS,
    MAC,
    LINUX;

    companion object {
        fun current(): OperationSystem {

            if (PlatformUtil.isWindows()) {
                return WINDOWS
            }

            if (PlatformUtil.isMac()) {
                return MAC
            }

            if (PlatformUtil.isLinux()) {
                return LINUX
            }

            throw IllegalStateException("Unsupported Operation System")
        }
    }
}
