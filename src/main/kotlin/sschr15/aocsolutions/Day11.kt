package sschr15.aocsolutions

import kotlin.math.min

fun day11() {
    val seats = Grid.of(getChallenge(2020, 11).readLines().map { it.toList() })
    println("Both parts take seconds to execute. Please wait...")

    println("Part 1")
    // seats.map { it.map { c -> c } }.toGrid() is basically just seats.clone()
    var modifiableSeats = seats.map { it.map { c -> c } }.toGrid()
    var generation = Grid.of(listOf<List<Char>>()) // basically an empty grid
    while (generation.flatten() != modifiableSeats.flatten()) {
        generation = modifiableSeats.map { it.map { c -> c } }.toGrid()
        val points = generation.toPointMap()
        points.filterNot { it.value == '.' }.forEach { (point, _) ->
            val adjacentTakenSeats = generation.getNeighbors(point).filter { it.value == '#' }.size
            modifiableSeats[point] = when(min(adjacentTakenSeats, 4)) {
                0 -> '#'
                4 -> 'L'
                else -> modifiableSeats[point]
            }
        }
    }
    println(modifiableSeats.flatten().filter { it == '#' }.size)

    println("Part 2")
    modifiableSeats = seats.map { it.map { c -> c } }.toGrid()
    generation = Grid.of(listOf())
    while (generation.flatten() != modifiableSeats.flatten()) {
        generation = modifiableSeats.map { it.map { c -> c } }.toGrid()
        val points = generation.toPointMap()
        points.filterNot { it.value == '.' }.forEach { (point, _) ->
            modifiableSeats[point] = when(min(part2(point, generation), 5)) {
                0 -> '#'
                5 -> 'L'
                else -> modifiableSeats[point]
            }
        }
    }
    println(modifiableSeats.flatten().filter { it == '#' }.size)
}

private fun part2(point: Point, grid: Grid<Char>): Int {
    var seenPeople = 0
    val col = grid.getColumn(point.x)
    val row = grid.getRow(point.y)

    if (col.subList(point.y + 1, col.size).firstOrNull { it != '.' } == '#') seenPeople++
    if (col.subList(0, point.y).lastOrNull { it != '.' } == '#') seenPeople++

    if (row.subList(point.x + 1, row.size).firstOrNull { it != '.' } == '#') seenPeople++
    if (row.subList(0, point.x).lastOrNull { it != '.' } == '#') seenPeople++

    if (getDiagonal(point, grid, down = false, right = false).firstOrNull { it != '.' } == '#') seenPeople++
    if (getDiagonal(point, grid, down = false, right = true) .firstOrNull { it != '.' } == '#') seenPeople++
    if (getDiagonal(point, grid, down = true,  right = false).firstOrNull { it != '.' } == '#') seenPeople++
    if (getDiagonal(point, grid, down = true,  right = true) .firstOrNull { it != '.' } == '#') seenPeople++

    return seenPeople
}

private fun getDiagonal(point: Point, grid: Grid<Char>, down: Boolean, right: Boolean): List<Char> {
    var x = point.x
    var y = point.y
    val list = mutableListOf<Char>()

    while (true) {
        x += if (right) 1 else -1
        y += if (down) 1 else -1

        try {
            list.add(grid[x, y])
        } catch (e: IndexOutOfBoundsException) {
            return list
        }
    }
}
