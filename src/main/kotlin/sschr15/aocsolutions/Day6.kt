package sschr15.aocsolutions

/**
 * Day 6: Lanternfish
 *
 * Part 1: find how many lanternfish end up existing after 80 days
 * Part 2: find how many lanternfish end up existing after 256 days
 */
fun day6() {
    val input = getChallenge(2021, 6, ",").map { it.removeSuffix("\n").toInt() }

    // numbers get too big too quickly, so we shall use a map of the number of fish on a specific day of its cycle
    val fishMap = mutableMapOf<Int, Long>()
    input.forEach {
        fishMap[it] = fishMap.getOrDefault(it, 0) + 1
    }

    for (i in 1..256) {
        val entries = fishMap.entries.map { it.toPair() }
        fishMap.clear()
        entries.forEach {
            if (it.first - 1 < 0) {
                fishMap[8] = fishMap.getOrDefault(8, 0) + it.second
                fishMap[6] = fishMap.getOrDefault(6, 0) + it.second
            } else {
                fishMap[it.first - 1] = fishMap.getOrDefault(it.first - 1, 0) + it.second
            }
        }
        if (i == 80) {
            println("Part 1: ${fishMap.values.sum()}")
        }
    }

    println("Part 2: ${fishMap.values.sum()}")
}

fun main() {
    day6()
}