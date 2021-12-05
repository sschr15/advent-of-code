package sschr15.aocsolutions

import kotlin.math.*

/**
 * Day 5: Hydrothermal Venture
 *
 * Part 1: find how many points horizontal and vertical lines intersect
 * Part 2: consider diagonal lines as well
 */
fun day5() {
    val input = getChallenge(2021, 5)

    val grid = Grid(1000, 1000, 0)

    val lines = input.map { it.split(" -> ") }.map { (p1, p2) ->
        val (x1, y1) = p1.split(",").map { it.toInt() }
        val (x2, y2) = p2.split(",").map { it.toInt() }

        Line(Point(x1, y1), Point(x2, y2))
    }

    // Part 1
    lines.filter { it.slope == 0.0 || it.slope.isInfinite() }
        .flatMap { it.wholeNumberPoints }
        .filter { (x, y) -> x >= 0 && y >= 0 && x < grid.width && y < grid.height }
        .forEach { grid[it] = grid[it] + 1 }

    println("Part 1: ${grid.flatten().count { it > 1 }}")

    // Part 2
    lines.filter { it.slope.absoluteValue == 1.0 }.flatMap { it.wholeNumberPoints }
        .filter { (x, y) -> x >= 0 && y >= 0 && x < grid.width && y < grid.height }
        .forEach { grid[it] = grid[it] + 1 }

    println("Part 2: ${grid.flatten().count { it > 1 }}")
}

fun main() {
    day5()
}
