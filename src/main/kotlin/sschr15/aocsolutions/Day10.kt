package sschr15.aocsolutions

fun day10() {
    val challenge = getChallenge(2020, 10).readLines().map { it.toInt() }.sorted().toMutableList()
        .also { it.add(it.last() + 3) }.also { it.add(0, 0) }.toList()

    println("Part 1")
    var prevJoltage = 0

    var singleJoltJumps = 0
    var tripleJoltJumps = 0
    for (joltage in challenge) {
        val diff = joltage - prevJoltage
        prevJoltage = joltage
        when (diff) {
            1 -> singleJoltJumps++
            3 -> tripleJoltJumps++
        }
    }
    println("$singleJoltJumps * $tripleJoltJumps == ${singleJoltJumps * tripleJoltJumps}")

    println("Part 2")
    // recursive function go brr
    // ok maybe not the actual dataset takes *way* too long to compute
//    println("${part2(0, challenge)} permutations")

    // Thanks a lot to Phil (https://github.com/Philtard) for helping me with this!
    // I will also write my interpretation on what's happening

    // The possible amount of accesses to higher numbers this can do
    val permutations = mutableMapOf<Int, Long>()
    // We need a starting point (the final number becomes 1)
    permutations[challenge.last()] = 1

    // For the entire list in reversed order (except the final number)
    for (joltage in challenge.reversed().subList(1, challenge.size)) {
        // "amount" is the number of accesses to the possible higher numbers (+1, +2, +3)
        var amount = 0L
        // Add each possible higher numbers' amount themselves to the result
        for (i in (joltage + 1)..(joltage + 3)) amount += permutations[i] ?: 0
        // Add this to the list
        permutations[joltage] = amount
    }
    println("${permutations[0]} permutations")
}

/*
private fun part2(inputJoltage: Int, list: List<Int>): Int {
    val inputs = list.subList(0, min(3, list.size)).filter { it - inputJoltage <= 3 } // get valid inputs (up to 3)
    if (inputs.isEmpty()) return 1
    var out = 0
    for (i in inputs.indices) {
        val joltage = inputs[i]
        out += part2(joltage, list.subList(i + 1, list.size))
    }
    return out
}
*/
