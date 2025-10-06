package sschr15.aocsolutions.util

import java.math.BigInteger

fun BigInteger.log(base: BigInteger): BigInteger {
    var count = BigInteger.ZERO
    var current = BigInteger.ONE
    while (current < this) {
        current *= base
        count++
    }
    return count
}
