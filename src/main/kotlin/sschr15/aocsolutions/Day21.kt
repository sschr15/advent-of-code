package sschr15.aocsolutions

import com.microsoft.z3.IntExpr
import com.sschr15.z3kt.*
import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

private sealed class Monkey(val name: String) {
    enum class Operation {
        Plus, Minus, Times, DividedBy;
        companion object {
            fun fromString(string: String) = when (string) {
                "+" -> Plus
                "-" -> Minus
                "*" -> Times
                "/" -> DividedBy
                else -> throw IllegalArgumentException("Unknown operation: $string")
            }
        }
    }

    class Operator(name: String, val first: String, val second: String, val operation: Operation) : Monkey(name)
    class Number(name: String, val value: Int) : Monkey(name)
}

object Day21 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022) {
        //        test()

        val monkeys: Map<String, Monkey>
        part1 {
            monkeys = inputLines.associate { line ->
                val name = line.substringBefore(": ")
                val value = line.substringAfter(": ").split(" ")
                val monkey = if (value.size == 1) {
                    Monkey.Number(name, value.single().toInt())
                } else {
                    Monkey.Operator(name, value[0], value[2], Monkey.Operation.fromString(value[1]))
                }
                name to monkey
            }

            z3 {
                val monkeyExprs = mutableMapOf<String, IntExpr>()

                // first pass: create all variables / constants
                for ((name, monkey) in monkeys) {
                    monkeyExprs[name] = when (monkey) {
                        is Monkey.Number -> monkey.value.toZ3Int()
                        is Monkey.Operator -> int(name)
                    }
                }

                val model = solve {
                    // second pass: specify all constraints (literally just solve for `root`)
                    for ((name, monkey) in monkeys) {
                        if (monkey !is Monkey.Operator) continue
                        val expr = monkeyExprs.getValue(name)
                        val first = monkeyExprs.getValue(monkey.first)
                        val second = monkeyExprs.getValue(monkey.second)
                        when (monkey.operation) {
                            Monkey.Operation.Plus -> add(expr eq first + second)
                            Monkey.Operation.Minus -> add(expr eq first - second)
                            Monkey.Operation.Times -> add(expr eq first * second)
                            Monkey.Operation.DividedBy -> add(expr eq first / second)
                        }
                    }
                } ?: error("Model was deemed unsatisfiable")

                val rootExpr = monkeyExprs.getValue("root")
                model.eval(rootExpr, true).toString()
            }
        }
        part2 {
            z3 {
                val human = int("humn")
                val monkeyExprs = mutableMapOf("humn" to human)

                // very similar first pass, but skip the humn variable (that's what needs solving)
                for ((name, monkey) in monkeys) {
                    if (name == "humn") continue
                    monkeyExprs[name] = when (monkey) {
                        is Monkey.Number -> monkey.value.toZ3Int()
                        is Monkey.Operator -> int(name)
                    }
                }

                val model = solve {
                    // second pass also similar, but the inputs to root must now be equal instead
                    for ((name, monkey) in monkeys) {
                        if (monkey !is Monkey.Operator) continue

                        if (name == "root") {
                            val first = monkeyExprs.getValue(monkey.first)
                            val second = monkeyExprs.getValue(monkey.second)
                            add(first eq second)
                            continue
                        }

                        val expr = monkeyExprs.getValue(name)
                        val first = monkeyExprs.getValue(monkey.first)
                        val second = monkeyExprs.getValue(monkey.second)
                        when (monkey.operation) {
                            Monkey.Operation.Plus -> add(expr eq first + second)
                            Monkey.Operation.Minus -> add(expr eq first - second)
                            Monkey.Operation.Times -> add(expr eq first * second)
                            Monkey.Operation.DividedBy -> add(expr eq first / second)
                        }
                    }
                } ?: error("Model was deemed unsatisfiable")

                model.eval(human, true).toString()
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
