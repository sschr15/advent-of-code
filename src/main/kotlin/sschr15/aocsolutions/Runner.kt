package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

object Runner {
    @JvmStatic
    fun main(args: Array<String>) {
        val `class` = Class.forName(args.first())
        val method = Challenge::solve
        val solveDuration = method(`class`.kotlin.objectInstance as Challenge)
        println("Solved in $solveDuration")
    }
}
