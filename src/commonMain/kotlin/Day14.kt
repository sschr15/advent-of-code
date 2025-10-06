package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 14](https://adventofcode.com/2024/day/14)
 * Challenge: Merry Christmas from a bunch of robots
 */
object Day14 : Challenge {
    override fun solve() = challenge(2024, 14) {
//        test()

        val velocities: List<IntArray>
        part1 {
            val regex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
            velocities = inputLines.map { regex.matchEntire(it)!!.groupValues.drop(1).map(String::toInt).toIntArray() }
            val quadrants = IntArray(4)

            val t = 100

            val w = if (testing) 11 else 101
            val h = if (testing) 7 else 103

            velocities.forEach { (x, y, dx, dy) ->
                val pos100x = (dx * t + x).mod(w)
                val pos100y = (dy * t + y).mod(h)
                if (pos100x == w / 2 || pos100y == h / 2) return@forEach
                val quadrant = if (pos100x < w / 2) {
                    if (pos100y < h / 2) 0 else 1
                } else {
                    if (pos100y < h / 2) 2 else 3
                }
                quadrants[quadrant]++
            }

            println(quadrants.sum())
            quadrants.map { it.toLong() }.mul()
        }
        part2 {
            val gridOrig = Grid(101, 103, ' ')
//            var t = 0

//            val out = System.out
//            System.setOut(PrintStream(FileOutputStream("run/day14results")))
//
//            while (t++ < 10_000) {
//                println(t)
//                val grid = gridOrig.toGrid()
//
//                val w = if (testing) 11 else 101
//                val h = if (testing) 7 else 103
//
//                val allUnique = velocities.map { (x, y, dx, dy) ->
//                    val pos100x = (dx * t + x).mod(w)
//                    val pos100y = (dy * t + y).mod(h)
//                    grid[pos100x, pos100y] = '#'
//                    pos100x to pos100y
//                }.toSet().size == 500
//
//                if (allUnique) println(grid)
//            }
//
//            System.setOut(out)

            // Taken from the output of the above loop
            val tree = """
                ###############################
                #                             #
                #                             #
                #                             #
                #                             #
                #              #              #
                #             ###             #
                #            #####            #
                #           #######           #
                #          #########          #
                #            #####            #
                #           #######           #
                #          #########          #
                #         ###########         #
                #        #############        #
                #          #########          #
                #         ###########         #
                #        #############        #
                #       ###############       #
                #      #################      #
                #        #############        #
                #       ###############       #
                #      #################      #
                #     ###################     #
                #    #####################    #
                #             ###             #
                #             ###             #
                #             ###             #
                #                             #
                #                             #
                #                             #
                #                             #
                ###############################
            """.trimIndent().lines().toGrid().toPointMap()

            val onlyVelocities = velocities.map { it.drop(2).toIntArray() }
            val currentPositions = velocities.map { (x, y) -> Point(x, y) }.toTypedArray()

            var t = 0
            while (true) {
                t++
                val grid = gridOrig.toGrid()
                val w = 101
                val h = 103

                for (i in currentPositions.indices) {
                    val (x, y) = currentPositions[i]
                    val (dx, dy) = onlyVelocities[i]
                    val newX = (dx + x).mod(w)
                    val newY = (dy + y).mod(h)
                    grid[newX, newY] = '#'
                    currentPositions[i] = Point(newX, newY)
                }

                if (currentPositions.any { pt ->
                    tree.all { (treePt, c) -> (pt + treePt) in grid && grid[pt + treePt] == c }
                }) {
                    println(grid)
                    break
                }
            }

            t
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
