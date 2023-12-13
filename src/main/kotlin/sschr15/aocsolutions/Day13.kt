package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 13](https://adventofcode.com/2023/day/13)
 * Challenge: Find a bunch of mirrors (so you don't run into them in such an embarrassing way)
 * 
 * but wait! the mirrors actually got smudged so now you gotta make sure you can avoid them even though they're
 * in unexpected places
 */
object Day13 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 13) {
        splitBy("\n\n")
//        test()
        part1 {
            inputLines.sumOf {
                val grid = it.trim().split("\n").chars().toGrid()

                var toLeft = 1
                outer@while (true) {
                    var distance = 0
                    while (true) {
                        // check if we're at the edge
                        if (toLeft - 1 - distance < 0) break@outer
                        if (toLeft + distance >= grid.width) break@outer

                        val leftCol = grid.getColumn(toLeft - 1 - distance)
                        val rightCol = grid.getColumn(toLeft + distance)
                        if (leftCol != rightCol) break // reached a mismatch
                        distance++
                    }
                    toLeft++
                }
                if (toLeft != grid.width) return@sumOf toLeft

                var above = 1
                outer@while (true) {
                    var distance = 0
                    while (true) {
                        if (above - 1 - distance < 0) break@outer
                        if (above + distance >= grid.height) break@outer

                        val topRow = grid.getRow(above - 1 - distance)
                        val bottomRow = grid.getRow(above + distance)
                        if (topRow != bottomRow) break
                        distance++
                    }
                    above++
                }

                above * 100
            }
        }
        part2 {
            inputLines.sumOf {
                val grid = it.trim().split("\n").chars().toGrid()
                val columns = grid.columns()
                val rows = grid.rows()

                var toLeft = 1
                while (true) {
                    if (toLeft >= columns.size) break
                    val (left, right) = columns.take(toLeft).asReversed() to columns.drop(toLeft)
                    val diffs = left.zip(right).map { (l, r) -> l.zip(r).count { (a, b) -> a != b } }
                    if (diffs.sum() == 1) {
                        // exactly one difference is what we want
                        return@sumOf toLeft
                    }
                    toLeft++
                }

                var above = 1
                while (true) {
                    if (above >= rows.size) break
                    val (top, bottom) = rows.take(above).asReversed() to rows.drop(above)
                    val diffs = top.zip(bottom).map { (t, b) -> t.zip(b).count { (a, b) -> a != b } }
                    if (diffs.sum() == 1) {
                        return@sumOf above * 100
                    }
                    above++
                }

                error("no solution found")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
