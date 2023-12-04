@file:Suppress("PropertyName")
@file:OptIn(ExperimentalContracts::class)

package sschr15.aocsolutions.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTime

fun challenge(year: Int, day: Int, block: AdvancedChallenge.Builder.() -> Unit): Duration {
    val builder = AdvancedChallenge.Builder()
    builder.block()
    val d = if (builder._test) day + 30 else day
    if (builder._test) {
        System.setProperty("aoc.test", "true")
    }
    val challenge = AdvancedChallenge(year, d, builder)
    return challenge.solve()
}

class AdvancedChallenge(private val year: Int, private val day: Int, private val builder: Builder) : Challenge {
    override fun solve() = measureTime {
        val lines = getChallenge(year, day, builder._splitBy)
        val challengePart = ChallengePart(lines)
        val implicitResult = builder._p1!!.invoke(challengePart)
        val result = challengePart._res ?: implicitResult
        println("Part 1: $result")
        if (builder._p2 != null) {
            val implicitResult2 = builder._p2!!.invoke(challengePart)
            val result2 = challengePart._res ?: implicitResult2
            println("Part 2: $result2")
            copyToClipboard(result2.toString())
        } else {
            copyToClipboard(result.toString())
        }
    }

    private fun copyToClipboard(text: String) {
        if (System.getProperty("aoc.skip.clipboard.copy") == "true" || builder._test) return
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, selection)
    }

    class ChallengePart(
        val inputLines: List<String>,
    ) {
        internal var _res: Any? = null
        @PublishedApi
        internal val _extra = mutableMapOf<String, Any>()

        fun submit(result: Any?) {
            this._res = result
        }

        fun addInfo(key: String, value: Any) {
            _extra[key] = value
        }

        fun addInfo(key: String, value: () -> Any) {
            _extra[key] = value()
        }

        inline fun <reified T> getInfo(key: String): T {
            return _extra[key] as T
        }
    }

    class Builder {
        internal var _p1: (ChallengePart.() -> Any?)? = null
        internal var _p2: (ChallengePart.() -> Any?)? = null
        internal var _test = false
        internal var _splitBy = "\n"

        fun part1(block: ChallengePart.() -> Any?) {
            contract {
                callsInPlace(block, EXACTLY_ONCE)
            }
            _p1 = block
        }

        fun part2(block: ChallengePart.() -> Any?) {
            contract {
                callsInPlace(block, EXACTLY_ONCE)
            }
            _p2 = block
        }

        fun test() {
            _test = true
        }

        fun splitBy(splitBy: String) {
            _splitBy = splitBy
        }
    }
}
