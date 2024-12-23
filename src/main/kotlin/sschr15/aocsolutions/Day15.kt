package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 15](https://adventofcode.com/2024/day/15)
 * Challenge: The lanternfish hired a robot to push boxes, but whoops the warehouse is also bigger than expected
 */
object Day15 : Challenge {
    override fun solve() = challenge(2024, 15) {
//        test()

        splitBy("\n\n")

        val moves: List<Direction>
        part1 {
            val grid = inputLines.first().lines().toGrid()
            moves = inputLines[1].mapNotNull {
                when (it) {
                    '^' -> Direction.North
                    'v' -> Direction.South
                    '<' -> Direction.West
                    '>' -> Direction.East
                    else -> null
                }
            }

            var robotPos = grid.toPointMap().filterValues { it == '@' }.keys.single()

            fun moveBox(box: Point, direction: Direction): Boolean {
                val newPoint = direction.mod(box)
                val c = grid[newPoint]
                if (c == '#') return false
                if (c == 'O') {
                    val moved = moveBox(newPoint, direction)
                    if (moved) {
                        grid[newPoint] = 'O'
                        grid[box] = '.'
                    }
                    return moved
                }
                grid[newPoint] = 'O'
                grid[box] = '.'
                return true
            }

            for (move in moves) {
                val newPos = move.mod(robotPos)
                if (grid[newPos] == '#') continue
                if (grid[newPos] == 'O') {
                    if (moveBox(newPos, move)) {
                        grid[robotPos] = '.'
                        grid[newPos] = '@'
                        robotPos = newPos
                    }
                } else {
                    grid[robotPos] = '.'
                    grid[newPos] = '@'
                    robotPos = newPos
                }
            }

            grid.toPointMap().filterValues { it == 'O' }.keys.sumOf { it.y * 100 + it.x }
        }
        part2 {
            val grid = inputLines.first().lines().map { l -> l.flatMap { when(it) {
                '#', '.' -> listOf(it, it)
                '@' -> listOf('@', '.')
                'O' -> listOf('[', ']')
                else -> error(it)
            } } }.toGrid()

            fun moveBoxEW(box: Point, direction: Direction): Boolean {
                val me = grid[box]
                val newPoint = direction.mod(box)
                val c = grid[newPoint]
                if (c == '#') return false
                if (c == '[' || c == ']') {
                    val moved = moveBoxEW(newPoint, direction)
                    if (moved) {
                        grid[newPoint] = me
                        grid[box] = '.'
                    }
                    return moved
                }
                grid[newPoint] = me
                grid[box] = '.'
                return true
            }

            fun canMoveBoxNS(box: Point, direction: Direction): Boolean {
                val otherSide = if (grid[box] == '[') box.right() else box.left()

                val newPoint = direction.mod(box)
                val otherNew = direction.mod(otherSide)
                val c = grid[newPoint]
                val d = grid[otherNew]

                if (c == '#' || d == '#') return false
                if ((c == '[' || c == ']') && !canMoveBoxNS(newPoint, direction)) return false
                if ((d == '[' || d == ']') && !canMoveBoxNS(otherNew, direction)) return false
                return true
            }

            fun moveBoxNS(box: Point, direction: Direction) {
                val me = grid[box]
                val otherMe = if (me == '[') ']' else '['
                val otherSide = if (me == '[') box.right() else box.left()

                val newPoint = direction.mod(box)
                val otherNew = direction.mod(otherSide)
                val c = grid[newPoint]

                if (c == '[' || c == ']') moveBoxNS(newPoint, direction)

                val d = grid[otherNew] // only test after left-side box might've moved
                if (d == '[' || d == ']') moveBoxNS(otherNew, direction)

                grid[newPoint] = me
                grid[otherNew] = otherMe
                grid[box] = '.'
                grid[otherSide] = '.'
            }

            fun moveBox(box: Point, direction: Direction): Boolean = when (direction) {
                Direction.East, Direction.West -> moveBoxEW(box, direction)
                Direction.North, Direction.South -> {
                    val canMove = canMoveBoxNS(box, direction)
                    if (canMove) {
                        moveBoxNS(box, direction)
                    }
                    canMove
                }
            }

            var robotPos = grid.toPointMap().filterValues { it == '@' }.keys.single()
//            println(grid)

            for ((i, move) in moves.withIndex()) {
//                println(when (move) {
//                    Direction.North -> '^'
//                    Direction.South -> 'v'
//                    Direction.East -> '>'
//                    Direction.West -> '<'
//                } + " $i")
                val newPos = move.mod(robotPos)
                if (grid[newPos] == '#') continue
                if (grid[newPos] in "[]") {
                    if (moveBox(newPos, move)) {
                        grid[robotPos] = '.'
                        grid[newPos] = '@'
                        robotPos = newPos
                    }
                } else {
                    grid[robotPos] = '.'
                    grid[newPos] = '@'
                    robotPos = newPos
                }
//                println(grid)
            }

//            println(grid)

            grid.toPointMap().filterValues { it == '[' }.keys.sumOf { it.y * 100 + it.x }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
