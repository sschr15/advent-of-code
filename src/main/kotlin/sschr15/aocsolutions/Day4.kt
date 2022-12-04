package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.time.measureTime

/**
 * AOC 2022 [Day 4](https://adventofcode.com/2022/day/4)
 * Challenge: Find pairs where one entirely contains the other
 */
object Day4 : Challenge {
    @ReflectivelyUsed
    override fun solve() = measureTime {
        val data = getChallenge(2022, 4).csv()
        var totalContains = 0
        for ((first, second) in data) {
            val fRange = first.split("-").map { it.toInt() }.let { it[0]..it[1] }
            val sRange = second.split("-").map { it.toInt() }.let { it[0]..it[1] }

            if (fRange.containsAll(sRange) || sRange.containsAll(fRange)) {
                totalContains++
            }
        }
        println("Part 1: $totalContains")
        totalContains = 0
        // looking for any overlap
        for ((first, second) in data) {
            val fRange = first.split("-").map { it.toInt() }.let { it[0]..it[1] }
            val sRange = second.split("-").map { it.toInt() }.let { it[0]..it[1] }

            if (fRange.first in sRange || fRange.last in sRange || sRange.first in fRange || sRange.last in fRange) {
                totalContains++
            }
        }
        println("Part 2: $totalContains")
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
