package sschr15.aocsolutions

import sschr15.aocsolutions.util.ThreadLocalPrintStream
import java.io.PrintWriter
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.exists
import kotlin.time.measureTime

private var anythingFailed = false

fun main(args: Array<String>) {
    fun classOrNull(name: String) = try {
        Class.forName(name)
    } catch (e: ClassNotFoundException) {
        null
    }

    // in case there are slow solves, we can skip them if necessary
    val shouldExecuteSlowFunctions = args.contains("--include-slow")

    println("Total time: ${
        measureTime {
            val originalOut = System.out
            System.setOut(ThreadLocalPrintStream())

            for (day in 1..25) {
                val clazz = classOrNull("sschr15.aocsolutions.Day${day}Kt")
                if (clazz != null) {
                    // Kotlin "non-null Unit" compiles to return void instead of Unit
                    val method = MethodHandles.lookup().findStatic(clazz, "solve", MethodType.methodType(Void::class.java))
                    runCatching(day, method::invokeExact)
                } else {
                    // Python! because i'm trying to polyglot my way to victory
                    val path = Path("day${day}.py")
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

private fun runCatching(day: Int, block: () -> Unit) = Thread(Runnable {
    runCatching(block).onFailure { failure(it, day) }

    outputs[day] = (System.out as ThreadLocalPrintStream).out.toByteArray()
}, "Day $day main thread").also {
    it.start()
    threads.add(it)
}
