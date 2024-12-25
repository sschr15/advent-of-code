package sschr15.aocsolutions

import com.sschr15.aoc.annotations.Memoize
import org.jgrapht.Graphs
import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.graphwrap.*

/**
 * AOC 2024 [Day 24](https://adventofcode.com/2024/day/24) (Christmas Eve!)
 * Challenge: 
 */
object Day24 : Challenge {
    override fun solve() = challenge(2024, 24) {
//        test()

        splitBy("\n\n")

        fun List<Boolean>.toNumber() = joinToString("") { if (it) "1" else "0" }.reversed().toLong(2)

        val deps = directedGraphOf<String>()
        val registers = mutableMapOf<String, Boolean>()
        val registersByInputs = mutableMapOf<String, Boolean?>()
        val eval: Long

        @Memoize
        fun getRegister(register: String): Boolean {
            if (register !in registersByInputs) return registers[register] ?: error("Unexpected register $register")
            val op = registersByInputs[register]

            val (a, b) = deps.incomingEdgesOf(register).map { Graphs.getOppositeVertex(deps, it, register) }
            return when (op) {
                true -> getRegister(a) && getRegister(b)
                false -> getRegister(a) || getRegister(b)
                null -> getRegister(a) xor getRegister(b)
            }
        }

        part1 {

            for (line in inputLines.first().lines()) {
                val (reg, n) = line.split(": ")
                registers[reg] = n.toInt() != 0
            }

            for (line in inputLines.last().lines()) {
                val (a, op, b, _, res) = line.split(" ")
                deps.addAndConnectVertices(a, res)
                deps.addAndConnectVertices(b, res)
                registersByInputs[res] = when (op) {
                    "AND" -> true
                    "OR" -> false
                    "XOR" -> null
                    else -> error(line)
                }
            }

            eval = deps.vertexSet()
                .filter { it.startsWith("z") }
                .sortedBy { it.findNumbers().single() }
                .map { getRegister(it) }
                .toNumber()
            eval
        }
        part2 {
            val xNumber = registers
                .filterKeys { it.startsWith("x") }
                .entries
                .sortedBy { it.key.findNumbers().single() }
                .map { it.value }
                .toNumber()

            val yNumber = registers
                .filterKeys { it.startsWith("y") }
                .entries
                .sortedBy { it.key.findNumbers().single() }
                .map { it.value }
                .toNumber()

            val expectedOutputs = (xNumber + yNumber).toString(2)
            val realOutput = eval.toString(2)

            val incorrectRegisters = expectedOutputs.zip(realOutput)
                .reversed()
                .mapIndexedNotNull { i, (a, b) -> if (a == b) null else i }
                .map { "z${it.toString().padStart(2, '0')}" }

            fun shape(register: String): String = when (registersByInputs[register]) {
                true -> "triangle"
                false -> "rectangle"
                null -> "ellipse"
            }

            println(deps.toDot { "$it\", color=\"${if (getRegister(it)) "green" else "red"}\", style=\"filled\", shape=\"${shape(it)}" })

            //TODO - figure out how to find it with code instead of manually
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
