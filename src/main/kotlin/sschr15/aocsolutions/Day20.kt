package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.util.ArrayDeque

/**
 * AOC 2023 [Day 20](https://adventofcode.com/2023/day/20)
 * Challenge: VM day (for some definition of VM involving any kind of machine that can be virtual)
 */
object Day20 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 20) {
//        test()

        data class Module(val name: String, val outputModules: List<String>, val isConjunction: Boolean) // false == flip flop

        val modules: Map<String, Module>
        part1 {
            modules = inputLines.associate {
                val (nameWithOperand, pulses) = it.split(" -> ")

                // broadcaster is special state (neither flipflop nor conjunction)
                if (nameWithOperand == "broadcaster") return@associate nameWithOperand to Module(nameWithOperand, pulses.split(", "), false)

                val operand = nameWithOperand.first() // & for conjunction, % for flipflop
                val name = nameWithOperand.drop(1)

                val pulseList = pulses.split(", ")
                name to Module(name, pulseList, operand == '&')
            }

            val flipflopStates = modules.values.filter { !it.isConjunction }.associateWith { false }.toMutableMap()
            val conjunctionStates = modules.values.filter { it.isConjunction }.associateWith { mutableMapOf<String, Boolean>() }.toMutableMap()

            // Initialize conjunctions (they need to know how many inputs they have)
            for (module in modules.values) {
                for (dst in module.outputModules) {
                    val dstModule = modules[dst] ?: continue
                    if (dstModule.isConjunction) {
                        conjunctionStates[dstModule]!![module.name] = false
                    }
                }
            }

            val queue = ArrayDeque<Triple<String, String, Boolean>>() // src, dst, isHighPulse
            var pressedCount = 0

            var highPulses = 0
            var lowPulses = 0

            while (queue.isNotEmpty() || pressedCount++ < 1000) {
                if (queue.isEmpty()) queue.add("button" to "broadcaster" and false) // always send a low pulse to the broadcaster

                val (src, dst, isHighPulse) = queue.removeFirst()

                if (isHighPulse) highPulses++ else lowPulses++

                val module = modules[dst] ?: continue // module is some kind of output

                if (module.name == "broadcaster") {
                    // broadcaster is a special case: broadcast its low pulse to all of its output modules and nothing else
                    module.outputModules.forEach { queue.add(dst to it and false) }
                    continue
                }

                if (module.isConjunction) {
                    val states = conjunctionStates[module] ?: error("conjunction module $dst not found")
                    states[src] = isHighPulse

                    if (states.values.all { it }) {
                        queue.addAll(module.outputModules.map { dst to it and false })
                    } else {
                        queue.addAll(module.outputModules.map { dst to it and true })
                    }
                } else {
                    val state = flipflopStates[module] ?: error("flipflop module $dst not found")
                    if (!isHighPulse) {
                        flipflopStates[module] = !state
                        module.outputModules.forEach { queue.add(dst to it and !state) }
                    }
                }
            }

            highPulses.toLong() * lowPulses
        }
        part2 {
            val flipflopStates = modules.values.filter { !it.isConjunction }.associateWith { false }.toMutableMap()
            val conjunctionStates = modules.values.filter { it.isConjunction }.associateWith { mutableMapOf<String, Boolean>() }.toMutableMap()

            for (module in modules.values) {
                for (dst in module.outputModules) {
                    val dstModule = modules[dst] ?: continue
                    if (dstModule.isConjunction) {
                        conjunctionStates[dstModule]!![module.name] = false
                    }
                }
            }

            val queue = ArrayDeque<Triple<String, String, Boolean>>()
            var pressedCount = 0

            val outputsToRx = modules.values.single { it.outputModules == listOf("rx") }
            val outputsToTheOutput = modules.values.filter { outputsToRx.name in it.outputModules }

            val outputLoopLengths = outputsToTheOutput.associate { it.name to (null as Long?) }.toMutableMap()

            while (outputLoopLengths.values.any { it == null }) {
                if (queue.isEmpty()) {
                    pressedCount++
                    queue.add("button" to "broadcaster" and false)
                }

                val (src, dst, isHighPulse) = queue.removeFirst()

                val module = modules[dst] ?: continue

                if (module.name == "broadcaster") {
                    module.outputModules.forEach { queue.add(dst to it and false) }
                    continue
                }

                if (module.isConjunction) {
                    val states = conjunctionStates[module] ?: error("conjunction module $dst not found")

                    states[src] = isHighPulse

                    if (module == outputsToRx) {
                        for ((name, state) in states) {
                            if (outputLoopLengths[name] == null && state) {
                                outputLoopLengths[name] = pressedCount.toLong()
                            }
                        }
                    }

                    if (states.values.all { it }) {
                        queue.addAll(module.outputModules.map { dst to it and false })
                    } else {
                        queue.addAll(module.outputModules.map { dst to it and true })
                    }
                } else {
                    val state = flipflopStates[module] ?: error("flipflop module $dst not found")
                    if (!isHighPulse) {
                        flipflopStates[module] = !state
                        module.outputModules.forEach { queue.add(dst to it and !state) }
                    }
                }
            }

            // in theory the correct answer should be something-chinese-remainder-theorem but my solution (and others' it seems)
            // is equivalent to just taking the LCM of all the loop lengths
            outputLoopLengths.values.map { it!! }.reduce { acc, l -> lcm(acc, l) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
