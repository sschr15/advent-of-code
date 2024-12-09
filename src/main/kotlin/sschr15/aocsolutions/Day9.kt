package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

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
            val storage = inputLines.drop(1).dropLastWhile { it == "\n" }.flatMapIndexed { i, c -> 
                listOf(if (i % 2 == 1) -1 else id++).repeat(c.toInt())
            }.toMutableList()

            var currentEnd = storage.indexOfLast { it != -1 }
            while (-1 in storage.subList(0, currentEnd + 1)) {
                val firstAvailable = storage.indexOf(-1)
                val lastUnavailable = currentEnd--
                storage[firstAvailable] = storage[lastUnavailable]
                storage[lastUnavailable] = -1
                while (storage[currentEnd] == -1) currentEnd--
            }

            storage.take(storage.indexOf(-1)).mapIndexed { i, id -> i.toLong() * id }.sum()
        }
        part2 {
            var id = 0
            val storage = inputLines.drop(1).dropLastWhile { it == "\n" }.mapIndexed { i, c ->
                c.toInt() to if (i % 2 == 1) -1 else id++
            }.toMutableList()

            fun recombine() {
                var i = -1
                while (++i < storage.size) {
                    if (storage[i].second != -1) continue
                    if (i != storage.size - 1 && storage[i + 1].second == -1) {
                        val (a) = storage[i]
                        val (b) = storage[i + 1]
                        storage[i] = a + b to -1
                        storage.removeAt(i-- + 1)
                    }
                }
            }

            var workDone: Boolean
            outer@while (true) {
                workDone = false
                var location = storage.lastIndex
                while (location > 0) {
                    val file = storage[location--]
                    if (file.second == -1) continue

                    val firstOpenBlock = storage.indexOfFirst { (sz, id) -> id == -1 && sz >= file.first }
                    if (firstOpenBlock != -1 && firstOpenBlock <= location) {
                        val (avail) = storage.removeAt(firstOpenBlock)
                        val idx = storage.indexOf(file)
                        storage.add(idx, file.first to -1)
                        storage.remove(file)
                        storage.add(firstOpenBlock, file)
                        storage.add(firstOpenBlock + 1, avail - file.first to -1)
                        workDone = true
                        continue@outer
                    }
                }

                if (!workDone) break@outer

                recombine()
            }

            storage.asSequence()
                .flatMap { (sz, id) -> listOf(id).repeat(sz) }
                .mapIndexed { i, id -> id.toLong() * i }
                .filter { i -> i > 0 }
                .sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
