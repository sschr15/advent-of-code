package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.*

/**
 * AOC 2024 [Day 2](https://adventofcode.com/2024/day/2)
 * Challenge: double-check that the Red-Nosed Reindeer Nuclear Fusion/Fission Plant has safe reports
 */
object Day2 : Challenge {
    override fun solve() = challenge(2024, 2) {
//        test()
        fun checkLine(nums: List<WatchedInt>): Boolean {
            var direction: Boolean? = null
            var prev = nums.first()
            for (next in nums.drop(1)) {
                when (prev - next) {
                    in -3..-1 -> if (direction == null) direction = true else if (!direction) return false
                    in 1..3 -> if (direction == null) direction = false else if (direction) return false
                    else -> return false
                }
                prev = next
            }
            return true
        }

        part1 {
            inputLines.count { line ->
                checkLine(line.split(' ').ints())
            }
        }
        part2 {
            inputLines.count { line ->
                val nums = line.split(' ').ints()
                checkLine(nums) || nums.indices.any { 
                    checkLine(nums.take(it) + nums.drop(it + 1))
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
