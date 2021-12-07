package sschr15.aocsolutions

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
            runCatching {
                println("Day 1: Sonar Sweep")
                println("Completed in ${measureTimeMillis { day1() }}ms")
            }.onFailure { failure(it, 1) }

            runCatching {
                println("Day 2: Dive!")
                println("Completed in ${measureTimeMillis { day2() }}ms")
            }.onFailure { failure(it, 2) }

            runCatching {
                println("Day 3: Binary Diagnostic")
                println("Completed in ${measureTimeMillis { day3() }}ms")
            }.onFailure { failure(it, 3) }

            runCatching {
                println("Day 4: Giant Squid")
                println("Completed in ${measureTimeMillis { day4() }}ms")
            }.onFailure { failure(it, 4) }

            runCatching {
                println("Day 5: Hydrothermal Venture")
                println("Completed in ${measureTimeMillis { day5() }}ms")
            }.onFailure { failure(it, 5) }
            
            runCatching {
                println("Day 6: Lanternfish")
                println("Completed in ${measureTimeMillis { day6() }}ms")
            }.onFailure { failure(it, 6) }
            
            runCatching {
                println("Day 7: The Treachery of Whales")
                println("Completed in ${measureTimeMillis { day7() }}ms")
            }.onFailure { failure(it, 7) }

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
