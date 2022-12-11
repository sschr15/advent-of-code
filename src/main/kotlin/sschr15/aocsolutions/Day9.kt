package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.math.sign

private enum class Direction {
    UP, DOWN, LEFT, RIGHT;
}

private enum class Status {
    VISITED,
    EMPTY
}

/**
 * AOC 2022 [Day 9](https://adventofcode.com/2022/day/9)
 * Challenge: TODO (based on the day's description)
 */
object Day9 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 9) {
//        test()
        part1 {
            // oh no more grids
            val grid = Grid(1000, 1000, Status.EMPTY)
            val working = MutablePoint(500, 500)
            val tail = MutablePoint(500, 500)
            for ((dir, i) in inputLines.map { it.split(" ") }) {
                val direction = Direction.values().first { it.name.startsWith(dir) }
                val distance = i.toInt()
                repeat(distance) {
                    val prevWorking = working.toPoint() // copy
                    when (direction) {
                        Direction.UP -> working.y--
                        Direction.DOWN -> working.y++
                        Direction.LEFT -> working.x--
                        Direction.RIGHT -> working.x++
                    }
                    grid[tail.toPoint()] = Status.VISITED
                    if (tail.toPoint().chessDistance(working.toPoint()) > 1) {
                        tail.x = prevWorking.x
                        tail.y = prevWorking.y
                    }
//                    grid[working.toPoint()] = Status.HEAD
//                    grid[tail.toPoint()] = Status.TAIL
                }
            }
            grid[tail.toPoint()] = Status.VISITED
            submit(grid.flatten().count { it == Status.VISITED })
        }
        part2 {
            // now there are 9 "tails" indicated by numbers
            val grid = Grid(1000, 1000, ' ')
            val working = MutablePoint(500, 500)
            val tail = Array(9) { MutablePoint(500, 500) }
            for ((dir, i) in inputLines.map { it.split(" ") }) {
                val direction = Direction.values().first { it.name.startsWith(dir) }
                val distance = i.toInt()
                var before = working.toPoint()
                repeat(distance) { _ ->
                    when (direction) {
                        Direction.UP -> working.y--
                        Direction.DOWN -> working.y++
                        Direction.LEFT -> working.x--
                        Direction.RIGHT -> working.x++
                    }
                    repeat(9) {
                        val current = tail[it]
                        val copy = current.toPoint()

                        if (before.toPoint().chessDistance(current.toPoint()) > 1) {
                            // thanks to @llamalad7 for this great way of calculating the next position
                            // go check him out https://llama.is-a.dev/
                            val dx = (before.x - current.x).sign
                            val dy = (before.y - current.y).sign
                            current.x += dx
                            current.y += dy
                        }

                        before = copy
                    }
                    before = working.toPoint()

                    grid[tail.last().toPoint()] = 'V'
                }
            }

            fun diffs() = tail.toList().windowed(2, partialWindows = false).map { (a, b) -> a.chessDistance(b) }
            while (diffs().mul() != 1) {
                var before = working.toPoint()
                repeat(9) {
                    val current = tail[it]
                    val copy = current.toPoint()

                    if (before.toPoint().chessDistance(current.toPoint()) > 1) {
                        val dx = (before.x - current.x).sign
                        val dy = (before.y - current.y).sign
                        current.x += dx
                        current.y += dy
                    }

                    before = copy
                    grid[tail.last().toPoint()] = 'V'
                }
            }

            grid[tail.last().toPoint()] = 'V'
            submit(grid.flatten().count { it == 'V' })
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
