@file:Suppress("LocalVariableName")

package sschr15.aocsolutions

fun day9() {
    val numbers = getChallenge(2020, 9).readLines().map { it.toLong() }
    val preambleSize = 25 // resizing at will if needed

    println("Part 1")

    val part2Helper: Long

    val preamble = numbers.subList(0, preambleSize).toMutableList()
    var idx = preambleSize
    while (true) {
        val num = numbers[idx++]
        var found = false
        for (i in 0 until preambleSize) {
            for (j in (i + 1) until preambleSize) {
                if (i == j) continue
                if (preamble[i] + preamble[j] == num) {
                    found = true
                    break
                }
            }
            if (found) break
        }
        if (!found) {
            println("Number $num is invalid!")
            part2Helper = num
            break
        }
        preamble.removeAt(0)
        preamble.add(num)
    }

    println("Part 2")
    for (i in numbers.indices) {
        for (j in (i + 1) .. numbers.size) {
            val section = numbers.subList(i, j)
            if (section.sum() == part2Helper) {
                val smol = section.minOrNull()!!
                val BIIG = section.maxOrNull()!!
                println("$smol + $BIIG == ${smol + BIIG}")
                return
            }
        }
    }
}