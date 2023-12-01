package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.time.measureTime

/**
 * AOC 2023 [Day 1](https://adventofcode.com/2023/day/1)
 * Challenge: Unknown quite yet, as of writing it won't be out for another 35 minutes
 */
object Day1 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 1) {
//        test()
        part1 {
            val input = inputLines.ints()
            input.reduce { acc, i -> (acc + i) % 2023 }
        }
//        part2 {
//            "Some other result"
//        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
