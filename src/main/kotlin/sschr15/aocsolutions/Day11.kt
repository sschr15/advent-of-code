package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

private class Monkey(
    val items: MutableList<Long>,
    val operation: (Long, Int) -> Long,
    val modulo: Int,
    val number: Long
) {
    lateinit var ifTrue: Monkey
    lateinit var ifFalse: Monkey
    var timesInspecting = 0

    fun throwItems(divByThree: Boolean = true, modulo: Int) {
        items.forEach {
            val result = operation(it, modulo) / (3.takeIf { divByThree } ?: 1)
            if (result % this.modulo == 0L) {
                ifTrue.items.add(result)
            } else {
                ifFalse.items.add(result)
            }
        }
        timesInspecting += items.size
        items.clear()
    }

    override fun toString() = "Monkey $number"

    fun copy() = Monkey(items.map { it }.toMutableList(), operation, modulo, number).also {
        it.ifTrue = ifTrue
        it.ifFalse = ifFalse
    }
}

/**
 * parse an operation string like `old + 4`, `old * old`, `old / 2`, etc
 */
private fun parseOperation(operation: String): (Long, Int) -> Long {
    val split = operation.split(" ")
    val op = split[1]
    val num = split[2].toLongOrNull()
    return when (op) {
        "+" -> { old, mod -> modAdd(old, num ?: old, mod) }
        "*" -> { old, mod -> modMult(old, num ?: old, mod) }
        else -> throw IllegalArgumentException("Unknown operation $op")
    }
}

/**
 * AOC 2022 [Day 11](https://adventofcode.com/2022/day/11)
 * Challenge: Monkeys hate you and your things.
 */
object Day11 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 11) {
        splitBy("\n\n")
//        test()
        part1 {
            // this s*** is bananas
            // actually it's monkeys
            val monkeys = mutableListOf<Monkey>()
            val toThrowTo = mutableListOf<Pair<Int, Int>>()
            for (data in inputLines) {
                // Monkey N:
                //   Starting items: ...
                //   Operation: new = ...
                //   Test: divisible by ...
                //     If true: throw to monkey X
                //     If false: throw to monkey Y
                val lines = data.lines()
                val n = lines[0].substringAfter("Monkey ").substringBefore(":").toLong()
                val startingItems = lines[1].substringAfter("Starting items: ").split(", ").map { it.toLong() }
                val operation = parseOperation(lines[2].substringAfter("Operation: new = "))
                val test = lines[3].substringAfter("divisible by ").toInt()
                val ifTrue = lines[4].substringAfter("monkey ").toInt()
                val ifFalse = lines[5].substringAfter("monkey ").toInt()
                toThrowTo.add(Pair(ifTrue, ifFalse))

                monkeys.add(Monkey(startingItems.toMutableList(), operation, test, n) )
            }

            var totalMod = 1
            monkeys.forEachIndexed { index, monkey ->
                monkey.ifTrue = monkeys[toThrowTo[index].first]
                monkey.ifFalse = monkeys[toThrowTo[index].second]
                totalMod *= monkey.modulo
            }

            addInfo("monkeys", monkeys.map { it.copy() })
            addInfo("totalMod", totalMod)

            repeat(20) {
                monkeys.forEach { it.throwItems(modulo = totalMod) }
            }

            // top two multiplied
            monkeys.sortByDescending { it.timesInspecting }
            submit(monkeys[0].timesInspecting * monkeys[1].timesInspecting)
        }
        part2 {
            val monkeys = getInfo<List<Monkey>>("monkeys")!!.toMutableList()
            val totalMod = getInfo<Int>("totalMod")!!

            monkeys.sortBy { it.number }

            monkeys.forEach {
                it.ifTrue = monkeys[it.ifTrue.number.toInt()]
                it.ifFalse = monkeys[it.ifFalse.number.toInt()]
            }

            repeat(10_000) {
                monkeys.forEach {
                    it.throwItems(false, modulo = totalMod)
                }
            }

            // top two multiplied
            monkeys.sortByDescending { it.timesInspecting }
            submit(monkeys[0].timesInspecting.toLong() * monkeys[1].timesInspecting)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
