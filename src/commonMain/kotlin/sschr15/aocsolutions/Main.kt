@file:OptIn(ExperimentalCoroutinesApi::class)

package sschr15.aocsolutions

import kotlinx.coroutines.*
import kotlin.time.measureTime

private var anythingFailed = false

val days = listOf(
    Day1,  Day2,  Day3,  Day4,  Day5,
    Day6,  Day7,  Day8,  Day9,  Day10,
    Day11, Day12, Day13, Day14, Day15,
    Day16, Day17, Day18, Day19, Day20,
    Day21, Day22, Day23, Day24, Day25
)

val dayMap = days.associateBy { it.toString() }

fun main() {
    val daySingle = getProperty("aoc.day.single")
    if (daySingle != null) {
        val challenge = dayMap[daySingle] ?: error("Invalid day: $daySingle")
        val time = challenge.solve()
        println("Solved in $time")
        return
    }

    setProperty("aoc.skip.clipboard.copy", "true")

    println("Total time: ${
        measureTime {
            runBlocking {
                val pool = newFixedThreadPoolContext(8, "Challenge thread pool")
                days.map { 
                    launch(pool, CoroutineStart.LAZY) {
                        val time = it.solve()
                        println("Solved in $time")
                    }
                }.joinAll()
            }

            if (anythingFailed) {
                println("Stacktraces for failed solutions are in files under the name `dayN_error.txt`")
            }
        }
    }")
}
