package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.product
import sschr15.aocsolutions.util.watched.times

/**
 * AOC 2023 [Day 6](https://adventofcode.com/2023/day/6)
 * Challenge: TODO (based on the day's description)
 */
object Day6 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 6) {
//        test()
        part1 {
            val times = inputLines.first().findNumbers()
            val distances = inputLines.last().findNumbers()
            times.zip(distances).map {  (time, distance) ->
                (0..time.value).count {
                    val remainingTime = time.value - it
                    val distanceTravelled = it * remainingTime
                    distanceTravelled >= distance.value
                }
            }.map { it.toLong().w }.product()
        }
        part2 {
            val time = inputLines.first().filter { it.isDigit() }.toLong().w
            val distance = inputLines.last().filter { it.isDigit() }.toLong().w
            var leastTimePressed = 0L
            while (true) {
                val remainingTime = time - leastTimePressed
                val distanceTravelled = leastTimePressed * remainingTime
                if (distanceTravelled >= distance) break
                leastTimePressed++
            }

            var mostTimePressed = time
            while (true) {
                val remainingTime = time - mostTimePressed
                val distanceTravelled = mostTimePressed * remainingTime
                if (distanceTravelled >= distance) break
                mostTimePressed--
            }

            mostTimePressed - leastTimePressed + 1 // off by one, of course
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
