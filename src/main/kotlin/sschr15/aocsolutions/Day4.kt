package sschr15.aocsolutions

/**
 * Day 4: Giant Squid
 *
 * Goal: Play bingo! Sum all uncalled numbers on the winning bingo card, then multiply by the winning called number.
 */
fun day4() {
    val input = getChallenge(2021, 4, separator = "\n\n")
    val calledNumbers = input[0].split(',').map { it.toInt() }
    val bingoCards = input.drop(1).map {
        BingoCard(it)
    }.toMutableList()

    var mostRecentWin: BingoCard? = null
    var mostRecentWinNumber: Int? = null
    for (i in calledNumbers) {
        bingoCards.forEach { it.markNumber(i) }
        // Part 1: first winning card
        bingoCards.filter { it.checkBingo() }.forEach { bingoCard ->
            val win = bingoCard.toInt(i)
            if (mostRecentWin == null) println("Bingo! $win")
            mostRecentWin = bingoCard
            mostRecentWinNumber = win
            bingoCards.remove(bingoCard)
        }
    }
    println("Final bingo! $mostRecentWinNumber")
}

private class BingoCard(input: String) {
    var card: List<List<BingoInput>>

    init {
        val card = (1..5).map { mutableListOf<BingoInput?>(null, null, null, null, null) }

        val numbers = input.split('\n').map { it.split(' ').filterNot{ s -> s.isEmpty() } }
        for (i in numbers.indices) {
            for (j in numbers[i].indices) {
                card[i][j] = BingoInput(i, j, numbers[i][j].toInt())
            }
        }

        this.card = card.map { it.filterNotNull() }

        this.card.flatten().forEach { it.init() }
    }

    inner class BingoInput(val row: Int, val col: Int, val value: Int) {
        val possibleBingos = mutableListOf<List<BingoInput>>()
        var called = false

        fun init() {
            // check row
            val row = card[row]
            possibleBingos.add(row)

            // check column
            val col = card.map { it[col] }
            possibleBingos.add(col)

            // no diagonal checks because this bingo is not a good one
        }

        fun checkBingo() = possibleBingos.any { attempt ->
            attempt.all { it.called }
        }

        override fun toString() = value.toString()
    }

    fun checkBingo() = card[0].any { it.checkBingo() } || card.map { it[0] }.any { it.checkBingo() }

    fun markNumber(number: Int) {
        card.flatten().forEach { if (it.value == number) it.called = true }
    }

    override fun toString(): String = card.joinToString("\n") { row ->
        row.joinToString(" ") { it.value.toString().padStart(2) }
    }

    fun toInt(winningNumber: Int): Int {
        val numbers = card.flatten().filter { !it.called }.map { it.value }

        return numbers.sum() * winningNumber
    }
}

fun main() {
    day4()
}