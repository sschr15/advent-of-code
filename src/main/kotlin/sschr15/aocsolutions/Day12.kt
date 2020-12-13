package sschr15.aocsolutions

import kotlin.math.abs

fun day12() {
    println("Part 1")
    val instructions = getChallenge(2020, 12).readLines().map { it[0] to it[1, it.length].toInt() }
    var currentDirection = 'E'
    val currentPosition = MutableLongPoint(0, 0)
    for ((action, amount) in instructions) {
        currentDirection = part1(currentPosition, currentDirection, action, amount)
    }
    println(abs(currentPosition.x) + abs(currentPosition.y))

    println("Part 2")
    currentPosition.x = 0
    currentPosition.y = 0
    val waypointRelative = MutableLongPoint(10, -1) // north is negative :)
    for ((action, amount) in instructions) {
        currentDirection = part2(currentPosition, waypointRelative, currentDirection, action, amount)
    }
    println(abs(currentPosition.x) + abs(currentPosition.y))
}

private fun part1(point: MutableLongPoint, currentDirection: Char, action: Char, amount: Int): Char {
    when(action) {
        'N' -> point.y -= amount
        'E' -> point.x += amount
        'S' -> point.y += amount
        'W' -> point.x -= amount
        'R', 'L' -> {
            val rotateAmount = (amount * if (action == 'L') -1 else 1) + when(currentDirection) {
                'N' -> 270
                'E' -> 0
                'S' -> 90
                'W' -> 180
                else -> throw IllegalStateException()
            }
            return when((rotateAmount + 1080) % 360) {
                0 -> 'E'
                90 -> 'S'
                180 -> 'W'
                270  -> 'N'
                else  -> throw IllegalStateException()
            }
        }
        'F' -> part1(point, currentDirection, currentDirection, amount)
        else -> throw IllegalStateException(action.toString())
    }
    return currentDirection
}

private fun part2(point: MutableLongPoint, waypointRelative: MutableLongPoint, direction: Char, action: Char, amount: Int): Char {
    when(action) {
        'N' -> waypointRelative.y -= amount
        'E' -> waypointRelative.x += amount
        'S' -> waypointRelative.y += amount
        'W' -> waypointRelative.x -= amount
        'R' -> {
            for (i in 1..(amount / 90)) {
                val xAmt = waypointRelative.x
                val yAmt = waypointRelative.y

                waypointRelative.x = -yAmt
                waypointRelative.y = xAmt
            }
        }
        'L' -> {
            for (i in 1..(3 * (amount / 90))) {
                val xAmt = waypointRelative.x
                val yAmt = waypointRelative.y

                waypointRelative.x = -yAmt
                waypointRelative.y = xAmt
            }
        }
        'F' -> {
            point.x += waypointRelative.x * amount
            point.y += waypointRelative.y * amount
        }
        else -> throw IllegalStateException(action.toString())
    }
    return direction
}

fun main() {
    day12()
}
