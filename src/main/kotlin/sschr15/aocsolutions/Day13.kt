package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

private sealed class Element : Comparable<Element> {
    data class Number(val value: Int) : Element() {
        override fun compareTo(other: Element) = when (other) {
            is Number -> value.compareTo(other.value)
            is Collection -> Collection(listOf(this)).compareTo(other)
        }
    }

    data class Collection(val elements: List<Element>) : Element() {
        override fun compareTo(other: Element): Int {
            return when (other) { // type necessary because of recursive call
                is Number -> compareTo(Collection(listOf(other)))
                is Collection -> {
                    val thisIterator = elements.iterator()
                    val otherIterator = other.elements.iterator()
                    while (thisIterator.hasNext() && otherIterator.hasNext()) {
                        val thisNext = thisIterator.next()
                        val otherNext = otherIterator.next()
                        val comparison = thisNext.compareTo(otherNext)
                        if (comparison != 0) return comparison
                    }
                    if (thisIterator.hasNext()) 1
                    else if (otherIterator.hasNext()) -1
                    else 0
                }
            }
        }
    }

    companion object {
        fun parse(input: String): Element {
            if (input == "[]") return Collection(emptyList())
            if (!input.startsWith('['))
                return Number(input.toInt())

            val elements = mutableListOf<Element>()
            var currentElement = ""
            var depth = 0
            for (char in input.substring(1, input.length - 1)) {
                currentElement += char
                when {
                    char == '[' -> depth++
                    char == ']' -> depth--
                    char == ',' && depth == 0 -> {
                        currentElement = currentElement.dropLast(1) // remove comma
                        elements.add(parse(currentElement))
                        currentElement = ""
                    }
                }
            }
            elements.add(parse(currentElement))
            return Collection(elements)
        }
    }
}

object Day13 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022) {
//        test()
        splitBy("\n\n")

        part1 {
            val lists = inputLines.map { twoLines -> 
                val (a, b) = twoLines.trim().split("\n").map(Element::parse)
                a to b
            }

            lists.mapIndexedNotNull { i, (a, b) -> if (a < b) i + 1 else null }.sum()
        }
        part2 {
            val entries = inputLines.flatMap { twoLines ->
                val (a, b) = twoLines.trim().split("\n").map(Element::parse)
                listOf(a, b)
            }.sorted()

            val dividerA = Element.parse("[[2]]")
            val dividerB = Element.parse("[[6]]")

            val a = entries.indexOfFirst { it > dividerA } + 1
            val b = entries.indexOfFirst { it > dividerB } + 2 // extra 1 because of dividerA

            a * b
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
