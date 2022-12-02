package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.getChallenge
import sschr15.tools.qblo.Unsafe
import kotlin.time.measureTime

enum class RPS(val beat: RPS?) {
    ROCK(null),
    PAPER(ROCK),
    SCISSORS(PAPER);
}

/**
 * AOC 2022 [Day 2](https://adventofcode.com/2022/day/2)
 * Challenge: Rock Paper Scissors
 */
object Day2 : Challenge {
    @ReflectivelyUsed
    override fun solve() = measureTime {
        val data = getChallenge(2022, 2)
            .map { it[0] to it[2] }

        // Using unsafe to set the beat property on rock
        val unsafe = Unsafe.sun()
        val offset = unsafe.objectFieldOffset(RPS::class.java.getDeclaredField("beat"))
        unsafe.putObject(RPS.ROCK, offset, RPS.SCISSORS)

        val points = mutableListOf<Int>()
        for ((opponent, mine) in data) {
            val amountPlayed = mine - 'X'
            val myChoice = RPS.values()[amountPlayed]
            val opponentChoice = RPS.values()[opponent - 'A']
            points.add(when {
                opponentChoice == myChoice -> 3
                opponentChoice.beat == myChoice -> 0
                else -> 6
            } + amountPlayed + 1)
        }
        println("Part 1: ${points.sum()}")

        // now it indicates how we must end rather than what to play
        points.clear()
        for ((opponent, mine) in data) {
            val opponentChoice = RPS.values()[opponent - 'A']
            val choice = when (mine) {
                'X' -> /* Lose */ opponentChoice.beat
                'Y' -> /* Draw */ opponentChoice
                'Z' -> /* Win */ opponentChoice.beat!!.beat
                else -> error("Invalid type of game: $mine")
            }!!
            points.add(when {
                opponentChoice == choice -> 3
                opponentChoice.beat == choice -> 0
                else -> 6
            } + RPS.values().indexOf(choice) + 1)
        }

        println("Part 2: ${points.sum()}")
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
