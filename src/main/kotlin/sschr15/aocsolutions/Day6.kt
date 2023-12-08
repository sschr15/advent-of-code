package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.math.sqrt

/**
 * AOC 2023 [Day 6](https://adventofcode.com/2023/day/6)
 * Challenge: How many ways can you get a boat down a river if you press a button for random amounts of time?
 */
object Day6 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 6) {
//        test()

        // fun fact: INVALID_CHARACTERS is a compiler error, but kotlin lets you suppress compiler errors :)
        @Suppress("LocalVariableName", "INVALID_CHARACTERS")
        fun rangeOfPossibilities(time: Long, distanceNeeded: Long): Long {
            val sqrtPart = sqrt((time * time - 4 * distanceNeeded).toDouble())
            val positiveNumerator = -time + sqrtPart
            val negativeNumerator = -time - sqrtPart
            val denominator = -2

            val positiveAnswer = positiveNumerator / denominator
            val negativeAnswer = negativeNumerator / denominator

            val `x equals negative b plus or minus the square root of b squared minus 4ac all over two a` = positiveAnswer..negativeAnswer

            // for LaTeX people:
            val `x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}` =
                `x equals negative b plus or minus the square root of b squared minus 4ac all over two a`

            val shortestPress = `x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}`.start.ceilingToLong()
            val longestPress = `x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}`.endInclusive.floorToLong()

            return longestPress - shortestPress + 1
        }

        part1 {
            val times = inputLines.first().findLongs()
            val distances = inputLines.last().findLongs()
            times.zip(distances).map { (time, distance) ->
                rangeOfPossibilities(time, distance)
            }.reduce(Long::times)
        }
        part2 {
            val time = inputLines.first().filter { it.isDigit() }.toLong()
            val distance = inputLines.last().filter { it.isDigit() }.toLong()
            rangeOfPossibilities(time, distance)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
