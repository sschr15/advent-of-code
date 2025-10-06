package sschr15.aocsolutions

import sschr15.aocsolutions.util.AdvancedChallenge
import sschr15.aocsolutions.util.counts
import sschr15.aocsolutions.util.default
import sschr15.aocsolutions.util.log10iSmall
import sschr15.aocsolutions.util.plus
import sschr15.aocsolutions.util.pow
import java.math.BigInteger
import kotlin.collections.get
import kotlin.text.toLong

internal actual fun AdvancedChallenge.day11JvmBonus() {
    try { assert(false) } catch (_: AssertionError) {
        var longRunningState = inputLines.map { it.toLong() }.counts().mapValues { (_, v) -> v.toBigInteger() }
        val zero = 0.toBigInteger()

        repeat(100000) {
            val newState = mutableMapOf<Long, BigInteger>().default(zero)
            for ((s, n) in longRunningState.entries) {
                if (s == 0L) {
                    newState[1L] += n
                } else if (log10iSmall(s) % 2 == 1) {
                    val half = (log10iSmall(s) + 1) / 2
                    val pow10 = 10L.pow(half.toInt())
                    newState[s / pow10] += n
                    newState[s % pow10] += n
                } else {
                    newState[s * 2024] += n
                }
            }
            longRunningState = newState
        }

        val result = longRunningState.values.reduce { a, b -> a + b }
        println(result)
        println(result.toString().length)
    }
}
