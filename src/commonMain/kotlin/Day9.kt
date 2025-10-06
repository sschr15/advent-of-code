package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 9](https://adventofcode.com/2024/day/9)
 * Challenge: 
 */
object Day9 : Challenge {
    override fun solve() = challenge(2024, 9) {
//        test()

        splitBy("")

        part1 {
            var id = 0
            val storage = inputLines.dropLastWhile { it == "\n" }.asSequence().drop(1).flatMapIndexed { i, c -> 
                listOf(if (i % 2 == 1) -1 else id++).repeat(c.toInt())
            }.toMutableList()

            var currentEnd = storage.indexOfLast { it != -1 }
            var firstAvailable = storage.indexOf(-1)
            while (firstAvailable < currentEnd) {
                val lastUnavailable = currentEnd--
                storage[firstAvailable] = storage[lastUnavailable]
                storage[lastUnavailable] = -1
                while (storage[currentEnd] == -1) currentEnd--
                firstAvailable += storage.subList(firstAvailable, storage.size).indexOf(-1)
            }

            storage.subList(0, currentEnd + 1).sumOfIndexed { i, id -> i.toLong() * id }
        }
        part2 {
            data class Data(var start: Int, val size: Int, val id: Int) : Comparable<Data> {
                override fun compareTo(other: Data) = start.compareTo(other.start)
                inline val end get() = start + size
            }

            var id = 0
            var currentPos = 0
            val storage = inputLines.dropLastWhile { it == "\n" }.asSequence().drop(1).mapIndexedNotNull { i, c -> 
                val size = c.toInt()
                val data = if (i % 2 == 1 || size == 0) null else Data(currentPos, size, id++)
                currentPos += size
                data
            }.toMutableList()

            for (block in storage.reversed()) {
                var j = 0
                val currentIndex = storage.indexOf(block)
                while (j < currentIndex) {
                    val a = storage[j]
                    val b = storage[j + 1]
                    if (b.start - a.end >= block.size) {
                        storage.add(j + 1, block.copy(start = a.end))
                        storage.remove(block)
                        break
                    }
                    j++
                }
            }

            storage.sumOf {
                (it.start..<it.end).sumOf { i -> i.toLong() * it.id }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
