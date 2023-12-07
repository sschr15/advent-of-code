package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

enum class HandResult(val next: HandResult? = null) {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND(FIVE_OF_A_KIND),
    FULL_HOUSE(FOUR_OF_A_KIND),
    THREE_OF_A_KIND(FOUR_OF_A_KIND),
    TWO_PAIRS(FULL_HOUSE),
    ONE_PAIR(THREE_OF_A_KIND),
    HIGH_CARD(ONE_PAIR)
}

fun String.handResult(part2: Boolean): HandResult {
    // no suits, just values
    val values = this.map { it.toString() }.sorted()
    val valueCounts = values.groupingBy { it }.eachCount()
    val valueCountCounts = valueCounts
        .filterKeys { !part2 || it != "J" }.values // jacks are "jokers" in p2 and don't count on their own
        .groupingBy { it }.eachCount()

    var result = when {
        valueCounts["J"] == 5 -> return HandResult.FIVE_OF_A_KIND // (p2 needs this exception)
        valueCountCounts.containsKey(5) -> HandResult.FIVE_OF_A_KIND
        valueCountCounts.containsKey(4) -> HandResult.FOUR_OF_A_KIND
        valueCountCounts.containsKey(3) && valueCountCounts.containsKey(2) -> HandResult.FULL_HOUSE
        valueCountCounts.containsKey(3) -> HandResult.THREE_OF_A_KIND
        valueCountCounts.containsKey(2) && valueCountCounts.getValue(2) == 2 -> HandResult.TWO_PAIRS
        valueCountCounts.containsKey(2) -> HandResult.ONE_PAIR
        else -> HandResult.HIGH_CARD
    }
    if (part2) {
        // Each "J" (joker) counts as whatever makes a better hand
        repeat(valueCounts["J"] ?: 0) {
            result = result.next ?: error("what")
        }
    }
    return result
}

/**
 * AOC 2023 [Day 7](https://adventofcode.com/2023/day/7)
 * Challenge: Playing Camel Cards (it's like poker but meant to be
 * "easier to play while riding a camel")
 */
object Day7 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 7) {
//        test()
        fun cardComparator(part2: Boolean = false): Comparator<List<String>> {
            val defaultCardOrder = (2..9).map { it.digitToChar() } + listOf('T', 'J', 'Q', 'K', 'A')
            val cardSymbols = if (!part2) defaultCardOrder else {
                val new = defaultCardOrder.toMutableList()
                new.remove('J')
                new.addFirst('J')
                new
            }

            return Comparator { a, b ->

                val aResult = a.first().handResult(part2)
                val bResult = b.first().handResult(part2)

                if (aResult != bResult) return@Comparator bResult.compareTo(aResult)

                val aValues = a.first().map { cardSymbols.indexOf(it) }
                val bValues = b.first().map { cardSymbols.indexOf(it) }

                repeat(5) {
                    val aVal = aValues[it]
                    val bVal = bValues[it]
                    if (aVal != bVal) return@Comparator aVal.compareTo(bVal)
                }

                0
            }
        }

        part1 {
            inputLines
                .map { it.split(" ") }
                .sortedWith(cardComparator())
                .mapIndexed { index, (cards, bid) ->
                    bid.toLong() * (index + 1)
                }.sum()
        }
        part2 {
            inputLines
                .map { it.split(" ") }
                .sortedWith(cardComparator(true))
                .mapIndexed { index, (cards, bid) ->
                    bid.toLong() * (index + 1)
                }.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
