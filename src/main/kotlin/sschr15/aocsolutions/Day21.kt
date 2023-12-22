package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day N](https://adventofcode.com/2023/day/N)
 * Challenge: TODO (based on the day's description)
 */
object Day21 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 21) {
//        test()

        fun <T> Grid<T>.getNeighborsInfinity(point: Point) =
            listOf(point.up(), point.down(), point.left(), point.right()).associateWith {
                Point(it.x mod width, it.y mod height)
            }

        fun Grid<*>.coercePoint(point: Point) = Point(point.x mod width, point.y mod height)

        val grid: Grid<Char>
        part1 {
            grid = inputLines.toGrid()
            var locations = setOf(grid.toPointMap().filterValues { it == 'S' }.keys.single())

            repeat(64) {
                locations = locations.asSequence()
                    .flatMap { grid.getNeighbors(it, includeDiagonals = false).entries }
                    .filter { it.value != '#' }
                    .map { it.key }
                    .map { it.toPoint() }
                    .toSet()
            }

            locations.size
        }
        part2 {
            val start = grid.toPointMap().filterValues { it == 'S' }.keys.single()
            val everyPointValidNeighbors = grid.toPointMap().filterValues { it != '#' }.mapValues { (pt, _) ->
                listOf(pt.up(), pt.down(), pt.left(), pt.right())
                    .associateWith { grid.coercePoint(it) }
                    .filterValues { grid[it] != '#' }
                    .map { (k, v) ->
                        when { // Pair of (point in grid, grid "offset")
                            k.x < 0 -> v to Point(-1, 0)
                            k.x >= grid.width -> v to Point(1, 0)
                            k.y < 0 -> v to Point(0, -1)
                            k.y >= grid.height -> v to Point(0, 1)
                            else -> v to Point(0, 0)
                        }
                    }
            }

            // each state point refers to a given copy of the grid
            var currentState = everyPointValidNeighbors.keys.associateWith { emptySet<Point>() }.toMutableMap()
            currentState[start] = setOf(Point.origin) // original grid

            val threeValues = LongArray(3)
            repeat(131 * 2 + 65) { i ->
                val nextState = mutableMapOf<Point, Set<Point>>()
                currentState.filterValues(Set<*>::isNotEmpty).forEach { (pt, current) ->
                    val neighbors = everyPointValidNeighbors[pt]!!
                    for ((neighbor, gridOffset) in neighbors) {
                        val newOffsets = current.map { it + gridOffset }
                        nextState[neighbor] = (nextState[neighbor] ?: emptySet()) + newOffsets
                    }
                }

                currentState = nextState

                if (i % 131 == 64) {
                    val value = currentState.values.sumOf(Set<*>::size)
                    threeValues[i / 131] = value.toLong()
                }
            }

            // funky arithmetic somehow makes it work for 26501365 steps
            val (a, b, c) = threeValues
            val x = 202300L // 26501365 / 131 (rounded down)

            // i need to learn how this works instead of just seeing something about triangle numbers and asking others for help
            (
                  a
                + (b - a) * x
                + (c - 2 * b + a) * x * (x - 1) / 2
            )
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
