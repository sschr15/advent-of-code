package sschr15.aocsolutions

fun main() {
    val challenge = getChallenge(2020, 2).readLines()

    println("Part 1")
    var validPasswords = 0

    challenge.forEach { s ->
        val (rangeText, letterText, passwordText) = s.split(' ')
        val range = rangeText.split('-')[0].toInt()..rangeText.split('-')[1].toInt()
        val letter = letterText[0]
        if (range.contains(passwordText.filter { it == letter }.length)) validPasswords++
    }

    println("$validPasswords valid passwords")
    println("Part 2")

    validPasswords = 0
    challenge.forEach { s ->
        val (rangeText, letterText, passwordText) = s.split(' ')
        val range = rangeText.split('-').map { it.toInt() }
        val letter = letterText[0]
        val password = " $passwordText"
        val chars = "${password[range[0]]}${password[range[1]]}"
        if (chars != "$letter$letter" && chars.contains(letter)) validPasswords++
    }

    println("$validPasswords valid passwords")
    println("Part 3")


    val part3 = getChallenge(0, 2).readLines()
    val builder = StringBuilder()

    part3.forEach { s ->
        val (rangeText, letterText, passwordText) = s.split(' ')
        val range = rangeText.split('-').map { it.toInt() }
        val letter = letterText[0]

        /*
         * This problem stumped me for a few minutes. Substring works by doing substring(inclusive, exclusive)
         * so if it's "abcde".substring(1, 3), the result will be "bc" instead of "bcd". As a result, it took
         * a fair bit of time for me to remember this. Plus the index-starts-at-1 problem made it a bit more
         * difficult. This code does in fact work, though
         */
        val password = passwordText.substring(range[0] - 1, range[1])
        if (password.reversed() == password) {
            val rot13 = if (letter.toLowerCase() + 13 > 'z') letter - 13 else letter + 13
            builder.append(rot13)
        }
    }
    println("Password: $builder")

}