package sschr15.aocsolutions

fun day8() {
    val instructions = getChallenge(2020, 8).readLines().map { Instruction(it[0, 3], it.substring(4).toInt()) }

    println("Part 1")
    val part1Instructions = instructions.map { Instruction(it.type, it.amount) } // clone instructions
    var acc = 0
    var idx = 0
    while (true) {
        val instruction = part1Instructions[idx++]
        if (instruction.visited) {
            println("Accumulator is $acc")
            break
        }
        instruction.visited = true
        when (instruction.type) {
            "nop" -> {} // ignore
            "jmp" -> idx += instruction.amount - 1
            "acc" -> acc += instruction.amount
            else  -> throw IllegalStateException()
        }
    }

    println("Part 2")
    var terminates = false
    val modifiable = instructions.mapIndexed { index, insn -> index to (insn.type == "acc") }.filter { !it.second }.map { it.first }
    var modifiedIdx = 0
    while (!terminates) {
        val part2Instructions = instructions.map { Instruction(it.type, it.amount) } // clone instructions
            .toMutableList()

        val modified = part2Instructions[modifiable[modifiedIdx++]]
        modified.type = if (modified.type == "nop") "jmp" else "nop"

        acc = 0
        idx = 0
        try {
            while (true) {
                val instruction = part2Instructions[idx++]
                if (instruction.visited) break

                instruction.visited = true
                when (instruction.type) {
                    "nop" -> {} // ignore
                    "jmp" -> idx += instruction.amount - 1
                    "acc" -> acc += instruction.amount
                    else -> throw IllegalStateException()
                }
            }
        } catch (oob: IndexOutOfBoundsException) {
            terminates = true
        }
    }
    println("Accumulator is $acc, flipped index ${modifiable[--modifiedIdx]}")
}

private data class Instruction(var type: String, val amount: Int) {
    var visited = false
}