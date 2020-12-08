package sschr15.aocsolutions

fun day7() {
    val challenge = getChallenge(2020, 7).readLines()

    println("Part 1")
    val map = mutableMapOf<String, MutableList<String>>()

    challenge.forEach {
        if (Regex("[a-z ]+ bags contain no other bags\\.").matches(it)) return@forEach

        val bagsText = it.split("contain ")[1].filter { c -> c != '.' }.split(", ")
        val bags = bagsText.map { s -> s[2, (if (s[0] == '1') s.length - 4 else s.length - 5)] }

        val bag = it[0, it.indexOf(" bags")]

        bags.forEach { b ->
            if (map.containsKey(b)) map[b]!!.add(bag)
            else map[b] = mutableListOf(bag)
        }
    }

    var bagList = mutableListOf("shiny gold")
    val finalBagList = mutableSetOf<String>()

    while (bagList.isNotEmpty()) {
        finalBagList.addAll(bagList)
        bagList = bagList.mapNotNull { map[it] }.flatten().toMutableList()
    }

    println(finalBagList.also { it.remove("shiny gold") }.toSet().size)

    println("Part 2")

    val challengeMap = getChallenge(2020, 7).readLines().associate {
        val (name, contentsText) = it.split(" contain ")
        val contents = if (!it.matches(Regex("[a-z ]+ bags contain no other bags\\."))) {
            contentsText.split(Regex(" bags?[,.] ?")).filter { s -> s.isNotEmpty() }.associate { s ->
                s[s.indexOf(' ') + 1, s.length] to s[0, s.indexOf(' ')].toInt()
            }
        } else {
            mapOf()
        }
        name[0, name.length - 5] to contents
    }

    println(getChildCount(challengeMap, "shiny gold") - 1)
}

private fun getChildCount(challengeMap: Map<String, Map<String, Int>>, bag: String): Int {
    val innerBags = challengeMap[bag] ?: mapOf()
    return innerBags.map { getChildCount(challengeMap, it.key) * it.value }.sum() + 1
}