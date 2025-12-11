@file:Suppress("PropertyName")

package sschr15.aocsolutions.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

fun challenge(year: Int, day: Int, block: AdvancedChallenge.Scope.() -> Unit): Duration {
    return measureTime {
        val scope = AdvancedChallenge.Scope(year, day)
        scope.block()
    }
}

object AdvancedChallenge {
    class Scope(private val year: Int, private val day: Int) {
        internal var _test = false
        internal var _splitBy: String? = "\n"

        val inputLines: List<String> by lazy {
            val d = if (_test) day + 30 else day
            if (_test) {
                System.setProperty("aoc.test", "true")
            }
            getChallenge(year, d, _splitBy)
        }

        fun part1(block: ChallengePart.() -> Any?) {
            contract {
                callsInPlace(block, EXACTLY_ONCE)
            }
            val part = ChallengePart(inputLines)
            val (result, duration) = measureTimedValue { part.block() }
            val finalResult = part._res ?: result
            println("Part 1: $finalResult (calculated in $duration)")
            copyToClipboard(finalResult.toString(), _test)
        }

        fun part2(block: ChallengePart.() -> Any?) {
            contract {
                callsInPlace(block, EXACTLY_ONCE)
            }
            val part = ChallengePart(inputLines)
            val (result, duration) = measureTimedValue { part.block() }
            val finalResult = part._res ?: result
            if (finalResult != "Some other result" && finalResult != Unit) {
                println("Part 2: $finalResult (calculated in $duration)")
                copyToClipboard(finalResult.toString(), _test)
            }
        }

        fun test() {
            _test = true
        }

        fun splitBy(splitBy: String?) {
            _splitBy = splitBy
        }
    }

    private fun copyToClipboard(text: String, test: Boolean) {
        if (System.getProperty("aoc.clipboard.skip") == "true" || test) return

        val copyUtility = System.getProperty("aoc.clipboard.cli")
        if (copyUtility == null) {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val selection = StringSelection(text)
            clipboard.setContents(selection, selection)
        } else {
            val process = ProcessBuilder(copyUtility)
                .start()
            process.outputStream.bufferedWriter().use { it.write(text) }
            process.waitFor()
        }
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
}
