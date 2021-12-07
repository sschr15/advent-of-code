package sschr15.aocsolutions

import java.math.BigInteger

/**
 * Day 6: Lanternfish
 *
 * Part 1: find how many lanternfish end up existing after 80 days
 * Part 2: find how many lanternfish end up existing after 256 days
 */
fun day6() {
    val input = getChallenge(2021, 6, ",").map { it.removeSuffix("\n").toInt() }

    // numbers get too big too quickly, so we shall use a map of the number of fish on a specific day of its cycle
    val fishMap = mutableMapOf<Int, BigInteger>()
    input.forEach {
        fishMap[it] = fishMap.getOrDefault(it, 0.toBigInteger()) + 1.toBigInteger()
    }

    repeat(10000) { i ->
        val entries = fishMap.entries.map { it.toPair() }
        fishMap.clear()
        entries.forEach {
            if (it.first - 1 < 0) {
                fishMap[8] = fishMap.getOrDefault(8, 0.toBigInteger()) + it.second
                fishMap[6] = fishMap.getOrDefault(6, 0.toBigInteger()) + it.second
            } else {
                fishMap[it.first - 1] = fishMap.getOrDefault(it.first - 1, 0.toBigInteger()) + it.second
            }
        }
        when (i) {
            79 -> {
                println("Part 1: ${fishMap.values.sumOf { it }}")
            }
            255 -> {
                println("Part 2: ${fishMap.values.sumOf { it }}")
            }
            999 -> {
                println("Fun part: ${fishMap.values.sumOf { it }}")
            }
            9999 -> {
                println("Fun part #2: ${fishMap.values.sumOf { it }}")
            }
        }
    }
}

fun main() {
    day6()
}