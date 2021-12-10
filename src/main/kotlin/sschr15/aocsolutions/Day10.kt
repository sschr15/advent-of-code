package sschr15.aocsolutions

import sschr15.aocsolutions.util.getChallenge
import sschr15.aocsolutions.util.solid

/**
 * Woo, double digits!
 *
 * Day 10: Syntax Scoring
 * - Part 1: Find mismatched pairs of brackets / braces / parentheses / angle brackets then sum their bad scores
 * - Part 2: Find unfinished sets and finish them then give score from different scoring system, then get the median of the resulting scores
 */
fun day10() {
    val input = getChallenge(2021, 10) // woo double digits!

    val badScores = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    ).solid(0)

    val brackets = input.map {
        val opens = mutableListOf<Char>()
        for (char in it) {
            when (char) {
                '(', '[', '{', '<' -> opens.add(char)
                ')' -> {
                    if (opens.last() == '(') {
                        opens.removeLast()
                    } else {
                        return@map char
                    }
                }
                ']', '}', '>' -> {
                    if (opens.last() == char - 2) {
                        opens.removeLast()
                    } else {
                        return@map char
                    }
                }
            }
        }
        return@map '.' // OK
    }

    println("Part 1: ${brackets.sumOf { badScores[it] }}")

    val unfinished = input.filterIndexed { i, _ -> brackets[i] == '.' }

    val newScoring = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4,
    ).solid(0)

    val closers = unfinished.map {
        val opens = mutableListOf<Char>()
        for (char in it) {
            when (char) {
                '(', '[', '{', '<' -> opens.add(char)
                ')', ']', '}', '>' -> opens.removeLast()
            }
        }

        return@map opens.reversed().map { char -> if (char == '(') char + 1 else char + 2 }
    }

    val scores = closers.map { chars -> chars.fold(0L) { acc, c ->
        (acc * 5) + newScoring[c]
    } }.sorted()

    // AoC guarantees that there will be an odd number of scores

    println("Part 2: ${scores[scores.size / 2]}")
}

fun main() {
    day10()
}
