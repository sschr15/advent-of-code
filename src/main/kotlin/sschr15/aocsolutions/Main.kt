package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ThreadLocalPrintStream
import java.io.PrintWriter
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.exists
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private var anythingFailed = false

@OptIn(ExperimentalTime::class)
fun main() {
    fun challengeInstance(name: String) = try {
        Class.forName(name).kotlin.objectInstance
            as? Challenge ?: throw ClassNotFoundException()
    } catch (e: ClassNotFoundException) {
        null
    }

    System.setProperty("aoc.skip.clipboard.copy", "true")

    println("Total time: ${
        measureTime {
            val originalOut = System.out
            System.setOut(ThreadLocalPrintStream())

            for (day in 1..25) {
                val challenge = challengeInstance("sschr15.aocsolutions.Day${day}")
                if (challenge != null) {
                    // Kotlin "non-null Unit" compiles to return void instead of Unit
                    runCatching(day) {
                        val length = challenge.solve()
                        println("Day $day took $length")
                    }
                } else {
                    // Python! because i'm trying to polyglot my way to victory
                    val path = Path("src/main/python/day${day}.py")
                    if (path.exists()) {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        runCatching(day) {
                            val process = ProcessBuilder("python", path.toString())
                                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                                .redirectError(ProcessBuilder.Redirect.PIPE)
                                .start()

                            process.waitFor()
                            val output = process.inputStream.bufferedReader().readText()
                            val error = process.errorStream.bufferedReader().readText()
                            if (process.exitValue() != 0) {
                                throw Exception("Python process exited with code ${process.exitValue()}: $error")
                            } else {
                                println(output)
                            }
                        }
                    }
                    // else the file just doesn't exist, so we don't care
                }
            }

            // wait for all threads (in the `threads` list) to finish
            while (threads.any { it.isAlive }) {
                Thread.sleep(100)
            }

            // restore the original output stream
            System.setOut(originalOut)

            // combine all ByteArrays in `output` into the print stream
            outputs.toList().sortedBy { it.first }.forEach { (_, output) ->
                println(output.toString(Charsets.UTF_8))
            }

            if (anythingFailed) {
                println("Stacktraces for failed solutions are in files under the name `dayN_error.txt`")
            }
        }
    }")
}

private fun failure(t: Throwable, day: Int) {
    println("Day $day failed: $t")
    Path("day${day}_error.txt").bufferedWriter().use {
        t.printStackTrace(PrintWriter(it))
    }
    anythingFailed = true
}

val outputs = mutableMapOf<Int, ByteArray>()
val threads = mutableListOf<Thread>()

private fun runCatching(day: Int, block: () -> Unit) = Thread({
    runCatching(block).onFailure { failure(it, day) }

    outputs[day] = (System.out as ThreadLocalPrintStream).out.toByteArray()
}, "Day $day main thread").also {
    it.start()
    threads.add(it)
}
