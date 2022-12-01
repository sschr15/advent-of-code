package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.getChallenge
import kotlin.time.measureTime

/**
 * AOC 2022 [Day N](https://adventofcode.com/2022/day/N)
 * Challenge: TODO (based on the day's description)
 */
object DayN : Challenge {
    @ReflectivelyUsed
    override fun solve() = measureTime {
        val data = getChallenge(2022, 1)
        TODO("Complete your challenge here!")
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
