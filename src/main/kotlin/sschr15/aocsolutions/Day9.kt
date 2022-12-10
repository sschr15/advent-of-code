package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    enum class Extended(val down: Boolean? = null, val right: Boolean? = null) {
        UL(false, false),
        U(false, null),
        UR(false, true),
        L(null, false),
        R(null, true),
        DL(true, false),
        D(true, null),
        DR(true, true);
    }
}

enum class Status {
    HEAD,
    TAIL,
    VISITED,
    EMPTY
}

/**
 * AOC 2022 [Day 9](https://adventofcode.com/2022/day/9)
 * Challenge: TODO (based on the day's description)
 */
object Day9 : Challenge {
    @OptIn(ExperimentalStdlibApi::class)
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 9) {
        test()
        part1 {
            // oh no more grids
            val grid = Grid(1000, 1000, Status.EMPTY)
            val working = MutablePoint(500, 500)
            val tail = MutablePoint(500, 500)
            val prevWorking = MutablePoint(500, 500)
            for ((dir, i) in inputLines.map { it.split(" ") }) {
                val direction = Direction.values().first { it.name.startsWith(dir) }
                val distance = i.toInt()
                repeat(distance) {
                    prevWorking.x = working.x
                    prevWorking.y = working.y
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
        @Suppress("UNREACHABLE_CODE")
        part2 {
            TODO("Part 2 code does not function correctly")
            // now there are 9 "tails" indicated by numbers
//            val grid = Grid(1000, 1000, ' ')
//            val working = MutablePoint(500, 500)
//            val tail = Array(9) { MutablePoint(500, 500) }
//            var offset = tail.map { it.toPoint() }
//            for ((dir, i) in inputLines.map { it.split(" ") }) {
//                val direction = Direction.values().first { it.name.startsWith(dir) }
//                val distance = i.toInt()
//                repeat(distance) {
//                    val prevWorking = working.toPoint()
//                    when (direction) {
//                        Direction.UP -> working.y--
//                        Direction.DOWN -> working.y++
//                        Direction.LEFT -> working.x--
//                        Direction.RIGHT -> working.x++
//                    }
//                    grid[tail.last().toPoint()] = 'V'
//                    var direction: Direction.Extended? = null
//                    for (j in 0 ..< 9) {
//                        val before = if (j > 0) offset[j - 1] else null
//                        val current = tail[j]
//
//                        if (before == null) {
//                            if (working.toPoint().chessDistance(current.toPoint()) > 1) {
//                                // get direction
//                                val xDiff = (working.x - current.x).coerceIn(-1, 1)
//                                val yDiff = (working.y - current.y).coerceIn(-1, 1)
//                                current.x = prevWorking.x
//                                current.y = prevWorking.y
//                                val vert = when (yDiff) {
//                                    -1 -> "U"
//                                    1 -> "D"
//                                    else -> ""
//                                }
//                                val horiz = when (xDiff) {
//                                    -1 -> "L"
//                                    1 -> "R"
//                                    else -> ""
//                                }
//                                direction = Direction.Extended.valueOf(vert + horiz)
//                            }
//                            continue
//                        }
//
//                        if (before.toPoint().chessDistance(current.toPoint()) > 1) {
//                            current.x += when (direction?.right) {
//                                true -> 1
//                                false -> -1
//                                else -> 0
//                            }
//                            current.y += when (direction?.down) {
//                                true -> 1
//                                false -> -1
//                                else -> 0
//                            }
//                        }
//                    }
//                    offset = tail.map { it.toPoint() }
//                }
//            }
//
//            grid[tail.last().toPoint()] = 'V'
//            submit(grid.flatten().count { it == 'V' })
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
