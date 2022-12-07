package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

data class File(val name: String, val size: Int)

open class Directory(val name: String, val parent: Directory?) {
    object Root : Directory("/", null)

    val children = mutableMapOf<String, Directory>()

    val files = mutableListOf<File>()

    val size: Int get() = files.sumOf { it.size } + children.values.sumOf { it.size }

    fun flatten(): List<Directory> = listOf(this) + children.values.flatMap { it.flatten() }
}

/**
 * AOC 2022 [Day 7](https://adventofcode.com/2022/day/7)
 * Challenge: Go through a filesystem
 */
object Day7 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 7) {
//        test()
        part1 {
            var currentDir: Directory = Directory.Root
            for (line in inputLines) {
                when {
                    line.startsWith("$ cd") -> {
                        val dirName = line.substringAfter("$ cd ")
                        currentDir = when (dirName) {
                            "/" -> Directory.Root
                            ".." -> currentDir.parent!!
                            else -> currentDir.children.getOrPut(dirName) { Directory(dirName, currentDir) }
                        }
                    }
                    line.startsWith("dir ") -> {
                        currentDir.children.putIfAbsent(line.substringAfter("dir "), Directory(line.substringAfter("dir "), currentDir))
                    }
                    line.matches("""\d+ \S+""".toRegex()) -> {
                        val (size, name) = line.split(" ")
                        currentDir.files.add(File(name, size.toInt()))
                    }
                }
            }
            // sum of all directories at most 100000 in size altogether
            val dirs = Directory.Root.flatten().filter { it.size <= 100_000 }
            submit(dirs.sumOf { it.size })
        }
        part2 {
            // Delete one directory that will bring total under 40_000_000
            val dirs = Directory.Root.flatten().sortedBy { it.size }
            val toDelete = dirs.first { Directory.Root.size - it.size <= 40_000_000 }
            submit(toDelete.size)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
