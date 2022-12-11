package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

/**
 * AOC 2022 [Day 10](https://adventofcode.com/2022/day/10)
 * Challenge: You're running a two-instruction-set "computer"!
 */
object Day10 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 10) {
//        test()
        part1 {
            // cpu time
            var register = 1
            var cycleCount = 0
            val signals = mutableListOf<Int>()
            fun cycle() {
                if (++cycleCount % 40 == 20) { // 20, 60, 100, 140 etc.
                    signals.add(register * cycleCount)
                }
            }
            for (line in inputLines) {
                when (line.split(" ").first()) {
                    "noop" -> {
                        cycle()
                    }
                    "addx" -> {
                        val param = line.split(" ").last().toInt()
                        cycle()
                        cycle()
                        register += param
                    }
                }
            }
            submit(signals.sum())
        }
        part2 {
            // register controls horiz position of middle of 3px sprite
            // crt is 40x6
            // we must print crt
            val crt = CharArray(40 * 6) { ' ' }
            var register = 1
            var crtLocation = 0 // previously cycleCount
            fun drawNext() {
                val compare = crtLocation % 40
                if (register - compare in -1..1) {
                    crt[crtLocation] = '#'
                }
                crtLocation++
            }
            for (line in inputLines) {
                when (line.split(" ").first()) {
                    "noop" -> {
                        drawNext()
                    }
                    "addx" -> {
                        val param = line.split(" ").last().toInt()
                        drawNext()
                        drawNext()
                        register += param
                    }
                }
            }
            submit("\n" + crt.joinToString("").chunked(40).joinToString("\n"))
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
