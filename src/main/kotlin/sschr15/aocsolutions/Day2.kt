package sschr15.aocsolutions

/**
 * Day 2: Dive!
 *
 * Part 1: find distance and depth, and multiply results
 *  - `up` and `down` control the depth (inverse of expected)
 *  - `forward` controls the distance
 *
 * Part 2: find distance, depth, and aim, and multiply results
 *  - `up` and `down` control the aim (inverse of expected)
 *  - `forward X` controls the distance and changes the depth by `X * aim`
 */
fun day2() {
    val data = getChallenge(2021, 2)
        .map { it.split(' ') }
        .map { it[0] to it[1].toInt() }

    val (distance, depth) = data.fold(0 to 0) { (distance, depth), (direction, amount) ->
        when (direction) {
            "up" -> distance to depth - amount
            "down" -> distance to depth + amount
            "forward" -> distance + amount to depth
            else -> throw IllegalArgumentException("Unknown direction: $direction")
        }
    }

    println("Part 1: ${distance * depth}")

    val (distance2, depth2, _) = data.fold(Triple(0, 0, 0)) { (distance, depth, aim), (direction, amount) ->
        when (direction) {
            "up" -> Triple(distance, depth, aim - amount)
            "down" -> Triple(distance, depth, aim + amount)
            "forward" -> Triple(distance + amount, depth + (aim * amount), aim)
            else -> throw IllegalArgumentException("Unknown direction: $direction")
        }
    }

    println("Part 2: ${distance2 * depth2}")
}

fun main() {
    day2()
}
