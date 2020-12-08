package sschr15.aocsolutions

fun day6() {
    println("Part 1")
    // get groups (a group is separated from others by two newlines)
    getChallenge(2020, 6).readLines().joinToString("\n").split("\n\n")
        .map { // Get only letters then remove copies
            it.toList().filter { c -> Regex("[a-z]").matches("$c") }.toSet()
        }.sumBy { it.size }.also { println(it) } // sum then print the sum

    println("Part 2")
    // get groups (a group is separated from others by two newlines)
    getChallenge(2020, 6).readLines().joinToString("\n").split("\n\n")
        .map { // Get only letters then remove copies
            it.toList().filter { c -> Regex("[a-z]").matches("$c") }.toSet()
                // filter to only letters everyone had
                .filter { c -> it.split("\n").all { s -> s.contains(c) } }
        }.sumBy { it.size }.also { println(it) } // sum then print the sum
}