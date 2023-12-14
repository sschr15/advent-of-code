package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

/**
 * AOC 2023 [Day N](https://adventofcode.com/2023/day/N)
 * Challenge: TODO (based on the day's description)
 */
object _Template : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 0) {
//        test()
        part1 {
            inputLines
        }
        part2 {
            "Some other result"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
