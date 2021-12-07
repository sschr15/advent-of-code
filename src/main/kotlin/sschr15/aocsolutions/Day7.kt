package sschr15.aocsolutions

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * Day 7: The Treachery of Whales
 *
 * Part 1: find the least fuel-expensive solution to move a list of ints to a single int
 * Part 2: each step takes 1 more fuel than the previous step
 */
fun day7() {
    val input = getChallenge(2021, 7, ",").map { it.removeSuffix("\n").toInt() }

    val possibilities = input.minOrNull()!!..input.maxOrNull()!!
    val result = possibilities.minOf {
        input.sumOf { i -> (i - it).absoluteValue }
    }
    println("Part 1: $result")

    // there's a geometric way of solving this ono
    val result2 = possibilities.minOf {
        input.sumOf { i -> i.step(it) }
    }

    println("Part 2: $result2")
}

fun main() {
    day7()
}

private fun Int.step(result: Int): Int {
    val start = min(this, result)
    val end = max(this, result)
    return (start..end).sumOf { it - start }
}
