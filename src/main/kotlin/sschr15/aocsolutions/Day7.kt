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

data class Hand(val cards: String, val bid: Int, val result: HandResult) : Comparable<Hand> {
    private val cardNums = cards.map { completeOrder.indexOf(it) }

    fun toPart2(): Hand {
        if (cards == "JJJJJ") return Hand("LLLLL", bid, result) // an all-jacks hand becomes the best hand (aside from sorting)
        if ('J' !in cards) return this // no jokers, no change
        return Hand(cards.replace('J', 'L'), bid, cards.handResult(true))
    }

    override fun compareTo(other: Hand): Int {
        if (result != other.result) return other.result.compareTo(result)
        repeat(5) {
            val thisVal = cardNums[it]
            val otherVal = other.cardNums[it]
            if (thisVal != otherVal) return thisVal.compareTo(otherVal)
        }
        return 0 // equal
    }

    companion object {
        private val completeOrder = listOf('L') + (2..9).map { it.digitToChar() } + listOf('T', 'J', 'Q', 'K', 'A')

        fun fromString(input: String): Hand {
            val (cards, bid) = input.split(" ")
            return Hand(cards, bid.toInt(), cards.handResult(false))
        }
    }
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
        val hands: List<Hand>
        part1 {
            hands = inputLines.map(Hand::fromString)
            hands.sorted()
                .mapIndexed { index, hand -> hand.bid.toLong() * (index + 1) }.sum()
        }
        part2 {
            hands.map(Hand::toPart2)
                .sorted()
                .mapIndexed { index, hand -> hand.bid.toLong() * (index + 1) }.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
