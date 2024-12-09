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
            var direction = when ((nums[1] - nums[0]).value) {
                -3, -2, -1 -> true
                1, 2, 3 -> false
                else -> return false
            }

            var prev = nums[1]
            for (next in nums.subList(2, nums.size)) {
                when ((prev - next).value) {
                    -3, -2, -1 -> if (!direction) return false
                    1, 2, 3 -> if (direction) return false
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
                    checkLine(nums.subList(0, it) + nums.subList(it + 1, nums.size))
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
