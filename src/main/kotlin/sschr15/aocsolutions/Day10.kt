package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.util.LinkedList
import java.util.Queue

/**
 * AOC 2023 [Day 10](https://adventofcode.com/2023/day/10)
 * Challenge: TODO (based on the day's description)
 */
object Day10 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 10) {
//        test()

        val grid: Grid<Pair<Char, Int>>
        val start: Point

        part1 {
            grid = inputLines.map { s -> s.map { it to Int.MIN_VALUE } }.toGrid()
            val startY = grid.indexOfFirst { ('S' to Int.MIN_VALUE) in it }
            val startX = grid.getRow(startY).indexOfFirst { it.first == 'S' }
            start = Point(startX, startY)

            // hardcoded start values (very hard complicated)
            grid[start] = '7' to 0
            var leftDirection: Direction = Direction.South
            var rightDirection: Direction = Direction.West
            var left = start.down()
            var right = start.left()

            if (_test) {
                grid[start] = 'F' to 0
                leftDirection = Direction.East
                rightDirection = Direction.South
                left = start.right()
                right = start.down()
            }

            var currentScore = 1

            while (left != right) {
                grid[left] = grid[left].first to currentScore
                grid[right] = grid[right].first to currentScore++

                leftDirection = findNextDirection(grid, left, leftDirection)
                rightDirection = findNextDirection(grid, right, rightDirection)

                left = leftDirection.mod(left)
                if (left == right) break // extra check
                right = rightDirection.mod(right)
            }

            grid.flatten().maxOf { it.second } + 1
        }
        part2 {
            val cleanGrid = Grid(grid.width, grid.height, ' ') // empty space default

            cleanGrid[start] = grid[start].first // guaranteed to be correct
            var currentPos = start.down()
            var currentDirection: Direction = Direction.South

            while (currentPos != start) {
                cleanGrid[currentPos] = when (val char = grid[currentPos].first) {
                    'L', 'F', '7', 'J', '|', '-' -> char
                    else -> error("Invalid character $char at $currentPos")
                }
                currentDirection = findNextDirection(grid, currentPos, currentDirection)
                currentPos = currentDirection.mod(currentPos)
            }

            val bigGrid = Grid((cleanGrid.width * 3) + 2, (cleanGrid.height * 3) + 2, ' ')
            for (y in 0 until cleanGrid.height) {
                for (x in 0 until cleanGrid.width) {
                    /*
                     * small grid to big grid:
                     *       . # .         . # .
                     *  L -> . # #    J -> # # .
                     *       . . .         . . .
                     * 
                     *       . . .         . . .
                     *  F -> . # #    7 -> # # .
                     *       . # .         . # .
                     * 
                     *       . # .         . . .
                     *  | -> . # .    - -> # # #
                     *       . # .         . . .
                     */
                    val char = cleanGrid[x, y]
                    if (char == ' ') continue
                    val bigX = (x * 3) + 2
                    val bigY = (y * 3) + 2
                    bigGrid[bigX, bigY] = '#' // center
                    when (char) {
                        'L' -> {
                            bigGrid[bigX, bigY - 1] = '#'
                            bigGrid[bigX + 1, bigY] = '#'
                        }
                        'F' -> {
                            bigGrid[bigX, bigY + 1] = '#'
                            bigGrid[bigX + 1, bigY] = '#'
                        }
                        '7' -> {
                            bigGrid[bigX, bigY + 1] = '#'
                            bigGrid[bigX - 1, bigY] = '#'
                        }
                        'J' -> {
                            bigGrid[bigX, bigY - 1] = '#'
                            bigGrid[bigX - 1, bigY] = '#'
                        }
                        '|' -> {
                            bigGrid[bigX, bigY - 1] = '#'
                            bigGrid[bigX, bigY + 1] = '#'
                        }
                        '-' -> {
                            bigGrid[bigX - 1, bigY] = '#'
                            bigGrid[bigX + 1, bigY] = '#'
                        }
                    }
                }
            }

            bfsReplaceFoundPoints(bigGrid, Point(0, 0))

            var count = 0
            for (y in 2..<bigGrid.height step 3) {
                for (x in 2..<bigGrid.width step 3) {
                    if (bigGrid[x, y] == ' ') { count++ }
                }
            }

            count
        }
    }

    private fun findNextDirection(
        grid: Grid<Pair<Char, Int>>,
        left: Point,
        leftDirection: Direction
    ) = when (grid[left].first) {
        '|', '-' -> leftDirection
        'L' -> when (leftDirection) {
            Direction.South -> Direction.East
            Direction.West -> Direction.North
            else -> error("Invalid direction $leftDirection into L")
        }

        'J' -> when (leftDirection) {
            Direction.South -> Direction.West
            Direction.East -> Direction.North
            else -> error("Invalid direction $leftDirection into J")
        }

        '7' -> when (leftDirection) {
            Direction.North -> Direction.West
            Direction.East -> Direction.South
            else -> error("Invalid direction $leftDirection into 7")
        }

        'F' -> when (leftDirection) {
            Direction.North -> Direction.East
            Direction.West -> Direction.South
            else -> error("Invalid direction $leftDirection into F")
        }

        '.' -> error("Shoult not be on ground at $left")
        else -> error("Invalid character ${grid[left].first} at $left")
    }

    private fun bfsReplaceFoundPoints(grid: Grid<Char>, start: Point) {
        val queue: Queue<Point> = LinkedList()
        queue.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.poll() ?: break // should never be null
            if (current.x < 0 || current.y < 0 || current.x >= grid.width || current.y >= grid.height) continue // out of bounds
            val currentChar = grid[current]
            if (currentChar == '.' || currentChar == '#') continue // wall or already visited
            grid[current] = '.'
            queue.add(current.up())
            queue.add(current.down())
            queue.add(current.left())
            queue.add(current.right())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
