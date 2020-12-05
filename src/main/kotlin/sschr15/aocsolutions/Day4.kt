package sschr15.aocsolutions

fun main() {
    val passportData = getChallenge(2020, 4).readLines().joinToString("\n").split("\n\n")
        .map { it.split(Regex("\\s")) }

    val passports = mutableListOf<Passport>()
    println("Part 1")

    passportData.forEach { data ->
        val passport = Passport(null, null, null, null, null, null, null, null)
        data.forEach {
            val (tag, value) = it.split(':')
            when(tag) {
                "byr" -> passport.birthYear      = value.toInt()
                "iyr" -> passport.issueYear      = value.toInt()
                "eyr" -> passport.expirationYear = value.toInt()
                "hgt" -> passport.height         = value
                "hcl" -> passport.hairColor      = value
                "ecl" -> passport.eyeColor       = value
                "pid" -> passport.passportId     = value
                "cid" -> passport.countryId      = value
            }
        }
        passports.add(passport)
    }

    println(passports.filter { it.isValid() }.size)
    println("Part 2")
    println(passports.filter { it.isValid(false) }.size)
}

private data class Passport(var birthYear: Int?, var issueYear: Int?, var expirationYear: Int?, var height: String?,
                      var hairColor: String?, var eyeColor: String?, var passportId: String?, var countryId: String?) {
    fun isValid(part1: Boolean = true): Boolean {
        if (part1) return birthYear != null && issueYear != null && expirationYear != null && height != null &&
                hairColor != null && eyeColor != null && passportId != null
        else {
            if (birthYear == null || issueYear == null || expirationYear == null || height == null ||
                hairColor == null || eyeColor == null || passportId == null) return false
            return birthYear in 1920..2002 && issueYear in 2010..2020 && expirationYear in 2020..2030 &&
                    (height!!.substring(0 until height!!.length - 2).toInt() in (if (height!!.endsWith("cm")) 150..193 else 59..76))
                    && hairColor!!.matches(Regex("^#[0-9a-f]{6}$"))
                    && eyeColor in listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth") && passportId!!.matches(Regex("^\\d{9}$"))
        }
    }
}