package sschr15.aocsolutions

import com.sschr15.chekt.Memoize
import sschr15.aocsolutions.util.*
import java.util.TreeSet

/**
 * AOC 2025 [Day 1](https://adventofcode.com/2025/day/1)
 * Challenge: joltage: it's like voltage but better
 */
object Day3 : Challenge {
    override fun solve() = challenge(2025, 3) {
//        test()

        part1 {
            inputLines.sumOf { chars -> 
                val max = chars.dropLast(1).max()
                val nextMax = chars.substring(chars.indexOf(max) + 1).max()
                "$max$nextMax".toInt()
            }
        }
        part2 {
            @Memoize
            fun maxValue(s: String, remainingCharacters: Int): Long? {
                if (remainingCharacters < 0) return null
                if (s.length < remainingCharacters) return null
                if (s.length == remainingCharacters) return s.toLongOrNull() ?: 0
                var max = Long.MIN_VALUE
                for (i in s.indices) {
                    val number = s[i].digitToInt()
                    val left = s.take(i)
                    val leftMax = maxValue(left, remainingCharacters - 1) ?: continue
                    max = maxOf(max, leftMax * 10 + number)
                }
                return max
            }

            inputLines.sumOf { s ->
                maxValue(s, 12).also(::println) ?: error(s)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
