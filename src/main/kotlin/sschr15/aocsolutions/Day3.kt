package sschr15.aocsolutions

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * See `GeneralUtils` for where [getChallenge] and [Grid] come from
 */
@ExperimentalUnsignedTypes
fun main() {
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

    // I never got a right answer for this as I spent a long time on it but got tired and had to go to bed.
    println("Part 3")
    challenge = Grid.of(getChallenge(0, 3).readLines().map { it.map { s -> (if (s == '.') 0 else 1).toByte() } })
    challenge[0, 0] = -1

    val width = challenge.width
    val height = challenge.height

    var cost = 0
    val path = aStarAlgorithm(Point(0, 0), Point(width - 1, height - 1), {
        (sqrt((it.y - (height - 1)).toDouble().pow(2) + (it.x - (width - 1)).toDouble().pow(2)) * 100).toInt()
    }, {
        listOf(Point(it.x + 1, it.y), Point(it.x - 1, it.y), Point(it.x, it.y + 1), Point(it.x, it.y - 1))
    }, { _, neighbor ->
        try {
            if (challenge[neighbor].toInt() == 1) 400 else 100
        } catch (outOfBounds: IndexOutOfBoundsException) {
            2147483647
        }
    })

    path!!.forEach {
        cost += if (challenge[it].toInt() == 1) 4 else 1
        challenge[it] = if (challenge[it].toInt() == 1) 4 else 2
    }

    println(challenge.data.joinToString("\n") { l ->
        l.map { when (it.toInt()) {
            -1   -> '+'
            0    -> ' '
            1    -> '^'
            2    -> '•'
            4    -> 'X'
            else -> '?'
        } }.joinToString("")
    })
    println("\n")
    println("$cost, ${challenge.flatten().filter { it.toInt() == 4 }.size}")
}