package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.math.BigInteger
import kotlin.math.absoluteValue

/**
 * AOC 2023 [Day 18](https://adventofcode.com/2023/day/18)
 * Challenge: day 10 part 2 (part 2 (part 2))
 * 
 * or:
 * 
 * Challenge: you gotta store all that lava *somewhere*, but how much where do you have?
 */
object Day18 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 18) {
//        test()

        fun fill(
            grid: Grid<Int>,
            pointInFillArea: Point,
            fillWith: Int,
        ) {
            val queue = ArrayDeque<Point>()
            queue.add(pointInFillArea)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current !in grid || grid[current] != 0) continue
                grid[current] = fillWith
                queue.add(Direction.North.mod(current))
                queue.add(Direction.South.mod(current))
                queue.add(Direction.East.mod(current))
                queue.add(Direction.West.mod(current))
            }
        }

        part1 {
            var top = 0
            var left = 0
            var bottom = 0
            var right = 0
            var currentPos = Point.origin
            val input = inputLines.map { it.split(" ") }
            for ((dir, amt, _) in input) {
                val direction = when (dir) {
                    "R" -> Direction.East
                    "L" -> Direction.West
                    "U" -> Direction.North
                    "D" -> Direction.South
                    else -> throw IllegalArgumentException("Invalid direction: $dir")
                }

                val amount = amt.toInt()
                repeat(amount) {
                    currentPos = direction.mod(currentPos)
                    top = minOf(top, currentPos.y)
                    left = minOf(left, currentPos.x)
                    bottom = maxOf(bottom, currentPos.y)
                    right = maxOf(right, currentPos.x)
                }
            }

            val offset = Point(left.absoluteValue, top.absoluteValue)
            val width = right - left + 1
            val height = bottom - top + 1

            val grid = Grid(width, height, 0)
            currentPos = Point.origin
            for ((dir, amt, _) in input) {
                val direction = when (dir) {
                    "R" -> Direction.East
                    "L" -> Direction.West
                    "U" -> Direction.North
                    "D" -> Direction.South
                    else -> throw IllegalArgumentException("Invalid direction: $dir")
                }

                val amount = amt.toInt()
                repeat(amount) {
                    currentPos = direction.mod(currentPos)
                    grid[currentPos.x + offset.x, currentPos.y + offset.y]++
                }
            }

            // hardcoded inside point, cry about it
            fill(
                grid,
                if (_test) Point(1, 1) else Point(200, 200),
                1
            )

            grid.flatten().count { it > 0 }
        }
        part2 {
            // as much as i love grids, these numbers are way too big for a grid to handle
            val instructions = inputLines.map {
                val (_, _, colorCode) = it.split(" ")
                val chars = colorCode.substring(2, 8)
                val distance = chars.dropLast(1).toBigInteger(16)
                val direction = when (chars.last()) {
                    '0' -> BigInteger.ONE to BigInteger.ZERO
                    '1' -> BigInteger.ZERO to BigInteger.ONE
                    '2' -> -BigInteger.ONE to BigInteger.ZERO
                    '3' -> BigInteger.ZERO to -BigInteger.ONE
                    else -> throw IllegalArgumentException("Invalid direction: ${chars.last()}")
                }
                direction to distance
            }

            var currentPos: BiggerPoint = BigInteger.ZERO to BigInteger.ZERO
            val points = mutableListOf(currentPos)
            var edgeTotal = BigInteger.ZERO

            for ((direction, distance) in instructions) {
                val (dx, dy) = direction
                val (x, y) = currentPos
                val (x2, y2) = x + dx * distance to y + dy * distance
                currentPos = x2 to y2
                points.add(currentPos)
                edgeTotal += distance
            }

            val shoelaceResult = shoelace(points)
            shoelaceResult + (edgeTotal / 2.toBigInteger()) + BigInteger.ONE
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
