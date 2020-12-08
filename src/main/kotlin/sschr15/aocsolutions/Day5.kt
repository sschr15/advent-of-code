package sschr15.aocsolutions

fun day5() {
    val challenge = getChallenge(2020, 5).readLines()

    println("Part 1")
    var highestId = 0

    val indices = mutableListOf<Int>() //NOTE: this is meant for part 2, but is used here for non-repetitiveness
    challenge.forEach {
        val row = binarySearch(it[0, 7].toList(), 'F', 'B')
        val col = binarySearch(it[7, 10].toList(), 'L', 'R')

        val id = row * 8 + col
        if (id > highestId) highestId = id

        indices.add(id) //NOTE: this is meant for part 2, but is used here for non-repetitiveness
    }
    println(highestId)

    println("Part 2")
    // The outputs for this may be one off of your seat number...
    indices.sorted().forEach {
        if (!indices.contains(it + 1) && it != indices.last()) println((it - 1).toString(3))
    }
}