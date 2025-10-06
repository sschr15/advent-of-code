package sschr15.aocsolutions

import com.sschr15.z3kt.*
import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 17](https://adventofcode.com/2024/day/17)
 * Challenge: it's vm day!
 */
object Day17 : Challenge {
    override fun solve() = challenge(2024, 17) {
//        test()

        splitBy("\n\n")

        fun run(program: List<Int>, registers: List<Int>): List<Int> {
            val out = mutableListOf<Int>()

            var (a, b, c) = registers
            var pc = 0

            fun combo() = when (val operand = program[pc++]) {
                in 0..3 -> operand
                4 -> a
                5 -> b
                6 -> c
                7 -> error("Combo operand 7 is reserved")
                else -> error("Unexpected operand $operand")
            }

            while (pc < program.size) {
                val insn = program[pc++]
                when (insn) {
                    0 -> a = a / (powi(2, combo()))
                    1 -> b = b xor program[pc++]
                    2 -> b = combo() mod 8
                    3 -> {
                        if (a == 0) pc++
                        else pc = program[pc++]
                    }
                    4 -> {
                        b = b xor c
                        pc++
                    }
                    5 -> out += combo() mod 8
                    6 -> b = a / (powi(2, combo()))
                    7 -> c = a / (powi(2, combo()))
                }
            }

            return out
        }

        val registers: List<Int>
        val program: List<Int>
        part1 {
            // VM DAY!!!!!!

            registers = inputLines.first().split("\n").map { it.filter(Char::isDigit).toInt() }
            program = inputLines.last().filter { it.isDigit() || it == ',' }.split(",").map { it.toInt() }
            val out = run(program, registers)
            out.joinToString(",")
        }

        part2 {
            z3 {
                val initialA by bitVec(64)
                val model = optimize {
                    val three = 3.toBitVec(64)
                    val four = 4.toBitVec(64)
                    val seven = 7.toBitVec(64)
                    var i = 0
                    var a = initialA
                    for (value in program) {
                        val aNow = bitVec("a${i++}", 64)
                        add(aNow eq a)
                        var b = aNow % 8
                        b = mkBVXOR(b, seven)
                        val c = mkBVLSHR(aNow, b)
                        b = mkBVXOR(b, c)
                        b = mkBVXOR(b, four)
                        b %= 8
                        add(b % 8 eq value.toBitVec(64))
                        a = mkBVLSHR(aNow, three)
                    }
                    minimize(initialA)
                } ?: error("Unsat")

                model.eval(initialA, true)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
