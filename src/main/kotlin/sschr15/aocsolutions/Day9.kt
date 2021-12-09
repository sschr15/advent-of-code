package sschr15.aocsolutions

import sschr15.aocsolutions.util.Grid
import sschr15.aocsolutions.util.Point
import sschr15.aocsolutions.util.getChallenge
import sschr15.aocsolutions.util.toGrid

/**
 * Day 9: Smoke Basin
 *
 * - Part 1: Find the sum of all "low points'" "risk levels"
 * - Part 2: Find the three largest "basins" and multiply their sizes
 */
fun day9() {
    val input = getChallenge(2021, 9)
    val grid = input.map { it.toList()
        .filter { c -> c.isDigit() }
        .map { c -> c.digitToInt() } }
        .toGrid()

    // region Part 1

    val lowPoints = grid.toPointMap().filter { (point, value) ->
        val neighbors = grid.getNeighbors(point, includeDiagonals = false)
        neighbors.all { (_, i) -> i > value }
    }.toList()

    // a "risk level" is just a low point's value plus 1
    val riskLevels = lowPoints.map { (_, value) -> value + 1 }

    println("Part 1: ${riskLevels.sum()}")

    // endregion

    // region Part 2

    val lowPointsToCheck = lowPoints.toMutableList()
    val basinSizes = mutableListOf<Int>()
    while (lowPointsToCheck.isNotEmpty()) {
        val (lowPoint, _) = lowPointsToCheck.removeAt(0)
        val basinPoints = findBasin(grid, lowPoint)
        lowPointsToCheck.removeIf { (point, _) -> point in basinPoints }
        basinSizes.add(basinPoints.size)
    }

    // largest three basins
    val largestBasins = basinSizes.sortedDescending().take(3)
    println("Part 2: ${largestBasins.reduce { acc, i -> acc * i }}")

    // endregion
}

fun main() {
    day9()
}

/**
 * Finds the points in a basin around a given point. A basin is a set of points enclosed by
 * points with a value of 9.
 */
private fun findBasin(grid: Grid<Int>, lowPoint: Point): Set<Point> {
    val visited = mutableSetOf<Point>()
    val queue = mutableListOf(lowPoint)
    while (queue.isNotEmpty()) {
        val point = queue.removeAt(0)
        visited.add(point)
        val neighbors = grid.getNeighbors(point, includeDiagonals = false)
        queue.addAll(neighbors.filter { (it, value) -> value < 9 && !visited.contains(it) }.keys)
    }
    return visited
}
