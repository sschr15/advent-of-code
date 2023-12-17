package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 17](https://adventofcode.com/2023/day/17)
 * Challenge: How do you best fuel all those metal crucibles, anyway?
 */
object Day17 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 17) {
//        test()

        val grid: Grid<Int>
        val start = Point.origin
        val end: Point
        part1 {
            grid = inputLines.map { it.map(Char::digitToInt) }.toGrid()
            end = Point(grid.width - 1, grid.height - 1)
            val result = dijkstra(
                Triple(start, Direction.East as Direction, 0),
                { (pt, dir, count) ->
                    if (count > 3 || pt !in grid) emptyList() else listOf(
                        Triple(dir.mod(pt), dir, count + 1),
                        Triple(dir.turnRight().mod(pt), dir.turnRight(), 1),
                        Triple(dir.turnLeft().mod(pt), dir.turnLeft(), 1)
                    )
                },
                { (pt, _, count) -> if (count > 3 || pt !in grid) 2 pow 24 else grid[pt] }
            )
            result.filterKeys { (pt, _, _) -> pt == end }.values.min()
        }
        part2 {
            val result = dijkstra(
                Triple(start, Direction.North as Direction, 0),
                { (pt, dir, cumulativeCost) ->
                    val list = mutableListOf<Triple<Point, Direction, Int>>()
                    val left = dir.turnLeft()
                    val right = dir.turnRight()

                    var leftSide = pt
                    var leftSum = -grid[pt] // offset for adding the current point
                    var rightSide = pt
                    var rightSum = -grid[pt]

                    repeat(4) {
                        leftSum += if (leftSide in grid) grid[leftSide] else 0 // fail silently
                        leftSide = left.mod(leftSide)
                        rightSum += if (rightSide in grid) grid[rightSide] else 0
                        rightSide = right.mod(rightSide)
                    }

                    repeat(7) {
                        if (leftSide in grid) {
                            leftSum += grid[leftSide]
                            list.add(Triple(leftSide, left, leftSum))
                            leftSide = left.mod(leftSide)
                        }
                        if (rightSide in grid) {
                            rightSum += grid[rightSide]
                            list.add(Triple(rightSide, right, rightSum))
                            rightSide = right.mod(rightSide)
                        }
                    }

                    list
                },
                { (_, _, cumulativeCost) -> cumulativeCost }
            )

            result.filterKeys { (pt, _, _) -> pt == end }.values.min()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
