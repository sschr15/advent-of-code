@file:Suppress("NOTHING_TO_INLINE")

package sschr15.aocsolutions

/**
 * Day 1: Sonar Sweep
 * Part 1 - Given a list of numbers, count the number of times the number increases.
 * Part 2 - like part 1 but the list is the sum of each 3 numbers.
 */
fun day1() {
    val data = getChallenge(2021, 1).use { it.readLines() }.map { it.toInt() }

    // default to max value to force the first value to not be considered an increase
    val result = data.countIncreases()

    println("Part 1: $result")

    // Part 2
    val newData = data.windowed(3) { it.sum() }
    val result2 = newData.countIncreases()

    println("Part 2: $result2")
}

fun main() {
    day1()
}

private inline fun List<Int>.countIncreases() = fold(0 to maxValue) { (count, last), current ->
    if (current > last) {
        count + 1 to current
    } else {
        count to current
    }
}.first
