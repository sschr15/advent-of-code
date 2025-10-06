@file:Suppress("PropertyName")

package sschr15.aocsolutions.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTime

fun challenge(year: Int, day: Int, block: AdvancedChallenge.() -> Unit): Duration {
    val challenge = AdvancedChallenge(year, day)
    challenge.block()
    return challenge.p1Time + challenge.p2Time
}

class AdvancedChallenge(year: Int, day: Int) {
    var testing = false
    private var splitBy: String? = "\n"
    var p1Time = Duration.ZERO
    var p2Time = Duration.ZERO

    var lastSolution: Any? = NO_LAST_SOLUTION

    val inputLines by lazy {
        getChallenge(year, if (testing) 30 + day else day, splitBy)
    }

    fun test() {
        testing = true
    }

    fun splitBy(s: String?) {
        splitBy = s
    }

    inline fun part1(block: () -> Any?) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        inputLines // Ensure the input is loaded
        p1Time = measureTime { 
            val result = block()?.takeIf { it !is Unit } ?: lastSolution
            if (result === NO_LAST_SOLUTION) error("Part 1 did not return a result")
            println(result)
            copyToClipboard(result.toString())
        }
    }

    inline fun part2(block: () -> Any?) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        lastSolution = NO_LAST_SOLUTION
        p2Time = measureTime {
            val result = block()?.takeIf { it !is Unit } ?: lastSolution
            if (result === NO_LAST_SOLUTION) return@measureTime 
            println(result)
            copyToClipboard(result.toString())
        }
    }

    companion object {
        val NO_LAST_SOLUTION = Any()
    }
}
