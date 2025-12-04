package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 4](https://adventofcode.com/2025/day/1) (without using my [Grid] utility)
 * Challenge: that's a lot of paper, how'd you get it in rolls?
 */
object Day4gridless : Challenge {
    override fun solve() = challenge(2025, 4) {
//        test()

        val papers: Set<Point>
        part1 {
            papers = mutableSetOf()
            for (y in inputLines.indices) for (x in inputLines[y].indices) {
                if (inputLines[y][x] == '@') papers += Point(x, y)
            }
            papers.count { it.neighbors(includeDiagonals = true).count(papers::contains) < 4 }
        }
        part2 {
            val papers = papers.toMutableSet()
            var removed = 0
            while (true) {
                val existing = papers.toSet()
                for (paper in existing) {
                    if (paper.neighbors(includeDiagonals = true).count { it in existing } < 4) {
                        papers.remove(paper)
                        removed++
                    }
                }
                if (papers == existing) return@part2 removed
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
