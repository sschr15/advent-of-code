package sschr15.aocsolutions

import sschr15.aocsolutions.util.getChallenge
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * Day 7: The Treachery of Whales
 *
 * - Part 1: find the least fuel-expensive solution to move a list of ints to a single int
 * - Part 2: each step takes 1 more fuel than the previous step
 */
fun day7() {
    val input = getChallenge(2021, 7, ",").map { it.removeSuffix("\n").toInt() }

    // Part 1 has the ideal position being the median of the input values
    val median = input.sorted().let { if (it.size % 2 == 0) (it[it.size / 2 - 1] + it[it.size / 2]) / 2 else it[it.size / 2] }
    val part1 = input.sumOf { (it - median).absoluteValue }

    println("Part 1: $part1")

    // Part 2 has an ideal solution being the mean, +/- 1
    val possiblePart2Solutions = input.average().let {
        listOf(it.toInt(), it.toInt() + 1)
    }
    val part2 = possiblePart2Solutions.minOf {
        input.sumOf { i -> i.step(it) }
    }

    println("Part 2: $part2")
}

fun main() {
    day7()
}

private fun Int.step(result: Int): Int {
    val start = min(this, result)
    val end = max(this, result)
    return (0..(end - start)).sum()
}
