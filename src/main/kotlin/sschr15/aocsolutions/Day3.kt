package sschr15.aocsolutions

/**
 * Day 3: Binary Diagnostic
 *
 * Part 1: multiply "gamma" rate by "epsilon" rate
 * - gamma rate: the most common bits of all numbers in the input
 * - epsilon rate: the least common bits of all numbers in the input
 *
 * Part 2: find "life support rating" by multiplying "oxygen generator" rate by "CO₂" rate
 * - oxygen generator rate: filter bit-by-bit the input to find a single number with the most common bits (hard to explain)
 *     - if there are as many 0s as 1s, the default is 1
 * - CO₂ rate: the inverse of the oxygen generator rate
 *     - if there are as many 1s as 0s, the default is 0
 */
fun day3() {
    // interpreting each number as a list of booleans to represent the bits
    val input = getChallenge(2021, 3)
        .map { it.toCharArray().map { c -> c.digitToInt() != 0 } }

    // gamma rate
    val gammaBits = mutableListOf<Boolean>()
    for (i in input[0].indices) {
        val bitCount = input.map { it[i] }.groupingBy { it }.eachCount()
        gammaBits.add(bitCount.maxByOrNull { it.value }!!.key)
    }
    val gamma = gammaBits.map { if (it) 1 else 0 }.joinToString("").toInt(2)

    // epsilon rate
    val epsilonBits = mutableListOf<Boolean>()
    for (i in input[0].indices) {
        val bitCount = input.map { it[i] }.groupingBy { it }.eachCount()
        epsilonBits.add(bitCount.minByOrNull { it.value }!!.key)
    }
    val epsilon = epsilonBits.map { if (it) 1 else 0 }.joinToString("").toInt(2)

    println("Part 1: ${gamma * epsilon}")

    // oxygen generator rate
    var remainingElements = input
    for (i in input[0].indices) {
        val bits = remainingElements.map { it[i] }
        val use1 = bits.count { it } >= bits.count { !it }
        remainingElements = if (use1) {
            remainingElements.filter { it[i] }
        } else {
            remainingElements.filter { !it[i] }
        }
        if (remainingElements.size == 1) break
    }
    // in theory there should only be one element left
    val oxygenResult = remainingElements.map { it.joinToString("") { bl -> if (bl) "1" else "0" }.toInt(2) }
        .let { if (it.size == 1) it[0] else error("Not the correct result size! (${it.size})") }

    // CO₂ rate
    remainingElements = input
    for (i in input[0].indices) {
        val bits = remainingElements.map { it[i] }
        val use0 = bits.count { it } >= bits.count { !it }
        remainingElements = if (use0) {
            remainingElements.filter { !it[i] }
        } else {
            remainingElements.filter { it[i] }
        }
        if (remainingElements.size == 1) break
    }
    // in theory there should only be one element left
    val co2Result = remainingElements.map { it.joinToString("") { bl -> if (bl) "1" else "0" }.toInt(2) }
        .let { if (it.size == 1) it[0] else error("Not the correct result size (${it.size})!") }

    println("Part 2: ${oxygenResult * co2Result}")
}

fun main() {
    day3()
}
