package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.WatchedInt
import sschr15.aocsolutions.util.watched.product
import sschr15.aocsolutions.util.watched.sum
import sschr15.aocsolutions.util.watched.toLong

/**
 * AOC 2023 [Day 3](https://adventofcode.com/2023/day/3)
 * Challenge: Fix an engine, but you can't see the engine, and the blueprint is neither blue nor a print
 */
object Day3 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 3) {
//        test()
        val numbers: List<Triple<WatchedInt, IntRange, Int>>
        val symbols: List<Triple<Char, Int, Int>>
        part1 {
            val number = "\\d+".toRegex()
            numbers = inputLines.flatMapIndexed { i, line ->
                number.findAll(line).map { Triple(it.value.toInt().w, it.range, i) }.toList()
            }
            symbols = inputLines.flatMapIndexed { y, line ->
                line.mapIndexed { x, c -> Triple(c, x, y) }.filter { !it.first.isDigit() && it.first != '.' }
            }
            val touchingNumbers = numbers.filter { (n, r, ny) -> 
                symbols.any { (_, x, y) -> 
                    val (nx1, nx2) = r.first to r.last
                    val xRange = (nx1 - 1)..(nx2 + 1)
                    val yRange = (ny - 1)..(ny + 1)
                    x in xRange && y in yRange
                }
            }

            touchingNumbers.sumOf { it.first.toLong().value }
        }
        part2 {
            val gears = symbols.filter { it.first == '*' }
                .mapNotNull { (_, x, y) -> 
                    val searchWidth = (x - 1)..(x + 1)
                    val searchHeight = (y - 1)..(y + 1)
                    val validNumbers = numbers.filter { (n, r, ny) ->
                        ny in searchHeight && (r intersect searchWidth).isNotEmpty()
                    }
                    if (validNumbers.size == 2) validNumbers.map { it.first }.product() else null
                }
            gears.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
