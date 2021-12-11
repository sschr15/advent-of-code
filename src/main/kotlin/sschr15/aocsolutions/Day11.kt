package sschr15.aocsolutions

import sschr15.aocsolutions.util.Point
import sschr15.aocsolutions.util.allEqual
import sschr15.aocsolutions.util.getChallenge
import sschr15.aocsolutions.util.toGrid

/**
 * Day 11: Dumbo Octopus
 *
 * - Part 1: Octopi flash based on values. Count flashes after 100 steps.
 * - Part 2: Repeat until all values are the same.
 */
fun day11() {
    val input = getChallenge(2021, 11)

    var grid = input.map { it.map { c -> c.digitToInt() } }.toGrid()

    var output = 0L // no clue how large this needs to be
    var i = 0
    while (true) {
        grid = grid.map { it.map { i -> i + 1 } }.toGrid()
        val flashyPoints = grid.toPointMap().filterValues { it > 9 }.keys.toMutableSet()
        val flashedPoints = mutableSetOf<Point>()
        while (flashyPoints.isNotEmpty()) {
            val point = flashyPoints.first()
            flashyPoints.remove(point)
            flashedPoints.add(point)
            val neighbors = grid.getNeighbors(point).keys
            flashyPoints.addAll(neighbors.filter {
                grid[it] = grid[it] + 1
                grid[it] > 9 && !flashedPoints.contains(it)
            })
        }
        // unflash all points
        grid = grid.map { it.map { i -> if (i > 9) 0 else i } }.toGrid()

        // then add to output
        output += flashedPoints.size

        if (i++ == 99) {
            println("Part 1: $output")
        }
        if (grid.flatten().allEqual()) {
            println("Part 2: $i")
            break
        }
    }
}

fun main() {
    day11()
}
