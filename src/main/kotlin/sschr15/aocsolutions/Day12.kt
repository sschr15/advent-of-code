package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 12](https://adventofcode.com/2024/day/12)
 * Challenge: 
 */
object Day12 : Challenge {
    override fun solve() = challenge(2024, 12) {
//        test()

        fun bfs(point: Point, grid: Grid<Char>): Triple<Int, Int, Set<Point>> {
            val toCheck = ArrayDeque(listOf(point))
            val visited = mutableSetOf<Point>()

            var perimeter = 0
            var area = 0

            while (toCheck.isNotEmpty()) {
                val next = toCheck.removeFirst()
                if (next in visited) continue

                val c = grid[next]
                val adjacent = grid.getNeighbors(next).filterValues { it == c }.keys
                toCheck.addAll(adjacent)
                perimeter += 4 - adjacent.size
                area++
                visited += next
            }

            return Triple(perimeter, area, visited)
        }

        part1 {

            val grid = inputLines.toGrid()
            val points = grid.toPointMap().keys.toMutableSet()

            var total = 0

            while (points.isNotEmpty()) {
                val (perimeter, area, pts) = bfs(points.first(), grid)
                total += area * perimeter
                points -= pts
            }

            total
        }
        part2 {
            fun findSides(start: Point, grid: Grid<Char>): Triple<Int, Int, Set<Point>> {
                val c = grid[start]
                val toCheck = ArrayDeque(listOf(start))
                val visited = mutableSetOf<Point>()
                val edgePoints = mutableSetOf<Point>()
                var area = 0

                while (toCheck.isNotEmpty()) {
                    val next = toCheck.removeFirst()
                    if (next in visited) continue
                    val (check, nonCheck) =
                        Grid.getNeighboringPoints(next).partition { it in grid && grid[it] == c }

                    toCheck.addAll(check)

                    if (nonCheck.isNotEmpty()) {
                        edgePoints += next
                    }

                    area++
                    visited += next
                }

                var sides = 0
                var nextPoint: Point? = Point.origin // dummy value
                while (true) {
                    nextPoint = edgePoints.firstOrNull { it.left() !in grid || grid[it.left()] != c }
                    val start = nextPoint ?: break
                    val startDirection = Direction.North
                    var current = start
                    edgePoints.remove(start)
                    var currentDirection: Direction = startDirection

                    do {
                        val newMaybe = currentDirection.mod(current)
                        if (newMaybe !in grid || grid[newMaybe] != c) {
                            currentDirection = currentDirection.turnRight()
                            sides++
                            if (currentDirection == Direction.North) {
                                edgePoints.remove(current)
                            }
                            continue
                        }
                        current = newMaybe
                        val turnedLeft = currentDirection.turnLeft().mod(current)
                        if (turnedLeft in grid && grid[turnedLeft] == c) {
                            currentDirection = currentDirection.turnLeft()
                            sides++
                        } else if (currentDirection == Direction.North) {
                            edgePoints.remove(current)
                        }
                    } while (current != start || currentDirection != startDirection)
                }

                return Triple(sides, area, visited)
            }

            val grid = inputLines.toGrid()
            val points = grid.toPointMap().keys.toMutableSet()

            var total = 0

            while (points.isNotEmpty()) {
                val (sides, area, pts) = findSides(points.first(), grid)
                total += area * sides
                points -= pts
            }

            total
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
