package ch.ayedo.portkiller

import java.util.concurrent.TimeUnit

fun String.execute(): String {

    val process = ProcessBuilder(this)
        .start()
        .also { it.waitFor(10, TimeUnit.SECONDS) }

    if (process.exitValue() != 0) {
        throw Exception(process.errorStream.bufferedReader().readText())
    }

    return process.inputStream.bufferedReader().readText()
}
