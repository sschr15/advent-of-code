package sschr15.aocsolutions

import sschr15.aocsolutions.util.ThreadLocalPrintStream
import java.io.PrintWriter
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private var anythingFailed = false

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    // in case there are slow solves, we can skip them if necessary
    val shouldExecuteSlowFunctions = args.contains("--include-slow")

    println("Total time: ${
        measureTime {
            val originalOut = System.out
            System.setOut(ThreadLocalPrintStream())
            
            runCatching(1) {
                println("Day 1: Sonar Sweep")
                println("Completed in ${measureTimeMillis { day1() }}ms")
            }

            runCatching(2) {
                println("Day 2: Dive!")
                println("Completed in ${measureTimeMillis { day2() }}ms")
            }

            runCatching(3) {
                println("Day 3: Binary Diagnostic")
                println("Completed in ${measureTimeMillis { day3() }}ms")
            }

            runCatching(4) {
                println("Day 4: Giant Squid")
                println("Completed in ${measureTimeMillis { day4() }}ms")
            }

            runCatching(5) {
                println("Day 5: Hydrothermal Venture")
                println("Completed in ${measureTimeMillis { day5() }}ms")
            }
            
            runCatching(6) {
                println("Day 6: Lanternfish")
                println("Completed in ${measureTimeMillis { day6() }}ms")
            }
            
            runCatching(7) {
                println("Day 7: The Treachery of Whales")
                println("Completed in ${measureTimeMillis { day7() }}ms")
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
        }.let { 
            val mins = it.inWholeMinutes.toString().padStart(2, '0')
            val secs = it.toDouble(DurationUnit.SECONDS).toString().let { s ->
                s.split(".")[0].padStart(2, '0') + "." + s.split(".")[1]
            }
            
            "$mins:$secs"
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

private fun runCatching(day: Int, block: () -> Unit) = Thread {
    runCatching(block)
        .onFailure { failure(it, day) }

    outputs[day] = (System.out as ThreadLocalPrintStream).out.toByteArray()
}.also {
    it.start()
    threads.add(it)
}
