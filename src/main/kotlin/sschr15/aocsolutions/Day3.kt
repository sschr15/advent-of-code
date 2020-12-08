package sschr15.aocsolutions

/**
 * See `GeneralUtils` for where [getChallenge] and [Grid] come from
 */
@ExperimentalUnsignedTypes
fun day3() {
    println("Part 1")
    var challenge = Grid.of(getChallenge(2020, 3).readLines().map { it.map { s -> (if (s == '.') 0 else 1).toByte() } })
    challenge[0, 0] = -1

    var x = 0
    var y = 0
    while (y + 1 < challenge.height) {
        y++
        x = (x + 3) % challenge.width

        challenge[x, y] = if (challenge[x, y].toInt() == 1) 4 else 2
    }
    println(challenge.flatten().filter { it.toInt() == 4 }.size)

    println("Part 2")
    var result = 0u
    for ((run, rise) in listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)) {
        challenge = Grid.of(getChallenge(2020, 3).readLines().map { it.map { s -> (if (s == '.') 0 else 1).toByte() } })
        challenge[0, 0] = -1

        x = 0
        y = 0
        while (y + rise < challenge.height) {
            y += rise
            x = (x + run) % challenge.width

            challenge[x, y] = if (challenge[x, y].toInt() == 1) 4 else 2
        }

        val answer = challenge.flatten().filter { it.toInt() == 4 }.size.toUInt()
        if (result == 0u) result = answer else result *= answer

        print("$answer ")
    }
    println("\n$result")
}