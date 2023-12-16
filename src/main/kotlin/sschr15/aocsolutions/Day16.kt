package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 16](https://adventofcode.com/2023/day/16)
 * Challenge: We got lasers! (and a sixth grid this year what the heck) but how much energy is it really?
 */
object Day16 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 16) {
//        test()
        
        fun calculate(inputLines: List<String>, start: Point, startDirection: Direction = Direction.East): Int {
            val grid = inputLines.map { s -> s.map { it to 0 } }.toGrid()
            val toTrack = mutableListOf(start to startDirection)

            while (toTrack.isNotEmpty()) {
                val (point, direction) = toTrack.removeFirst()
                if (point !in grid) continue

                val (char, flags) = grid[point]

                val flag = when (direction) {
                    Direction.North -> 1
                    Direction.East -> 2
                    Direction.South -> 4
                    Direction.West -> 8
                }

                if (flags and flag != 0) continue // already visited in this direction (probably loop)

                when (char) {
                    '/' -> {
                        val newDirection = when (direction) {
                            Direction.North -> Direction.East
                            Direction.East -> Direction.North
                            Direction.South -> Direction.West
                            Direction.West -> Direction.South
                        }
                        toTrack.add(newDirection.mod(point) to newDirection)
                    }
                    '\\' -> {
                        val newDirection = when (direction) {
                            Direction.North -> Direction.West
                            Direction.East -> Direction.South
                            Direction.South -> Direction.East
                            Direction.West -> Direction.North
                        }
                        toTrack.add(newDirection.mod(point) to newDirection)
                    }
                    '|' -> {
                        if (direction == Direction.East || direction == Direction.West) {
                            toTrack.add(point.up() to Direction.North)
                            toTrack.add(point.down() to Direction.South)
                        } else {
                            toTrack.add(direction.mod(point) to direction)
                        }
                    }
                    '-' -> {
                        if (direction == Direction.North || direction == Direction.South) {
                            toTrack.add(point.left() to Direction.West)
                            toTrack.add(point.right() to Direction.East)
                        } else {
                            toTrack.add(direction.mod(point) to direction)
                        }
                    }
                    else -> toTrack.add(direction.mod(point) to direction)
                }

                grid[point] = char to (flags or flag)
            }

            return grid.flatten().count { (_, i) -> i != 0 }
        }

        part1 {
            calculate(inputLines, Point(0, 0))
        }
        part2 {
            val results = mutableListOf<Int>()
            for (y in inputLines.indices) {
                results.add(calculate(inputLines, Point(0, y), Direction.East))
                results.add(calculate(inputLines, Point(inputLines[0].length - 1, y), Direction.West))
            }

            for (x in inputLines[0].indices) {
                results.add(calculate(inputLines, Point(x, 0), Direction.South))
                results.add(calculate(inputLines, Point(x, inputLines.size - 1), Direction.North))
            }

            results.maxOrNull() ?: error("no max")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
