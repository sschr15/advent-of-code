@file:Suppress("NOTHING_TO_INLINE")

package sschr15.aoc.annotations

inline fun plus(a: Int, b: Int): Int = Math.addExact(a, b) 
inline fun minus(a: Int, b: Int): Int = Math.subtractExact(a, b) 
inline fun times(a: Int, b: Int): Int = Math.multiplyExact(a, b) 
inline fun inc(a: Int): Int = Math.incrementExact(a) 
inline fun dec(a: Int): Int = Math.decrementExact(a)
inline fun unaryMinus(a: Int): Int = Math.negateExact(a)
inline fun abs(a: Int): Int = Math.absExact(a)

inline fun rem(a: Int, b: Int): Int {
    if (a < 0) {
        System.err.println("Warning: Remainder of a negative number")
    }
    return a % b
}

inline fun toFloat(a: Int): Float =
    if (a > 16777216 || a < -16777216) throw ArithmeticException("Not enough precision") else a.toFloat()

inline fun plus(a: Long, b: Long): Long = Math.addExact(a, b)
inline fun minus(a: Long, b: Long): Long = Math.subtractExact(a, b)
inline fun times(a: Long, b: Long): Long = Math.multiplyExact(a, b)
inline fun inc(a: Long): Long = Math.incrementExact(a)
inline fun dec(a: Long): Long = Math.decrementExact(a)
inline fun unaryMinus(a: Long): Long = Math.negateExact(a)
inline fun abs(a: Long): Long = Math.absExact(a)

inline fun rem(a: Long, b: Long): Long {
    if (a < 0) {
        System.err.println("Warning: Remainder of a negative number")
    }
    return a % b
}

inline fun toInt(a: Long): Int = Math.toIntExact(a)
inline fun toFloat(a: Long): Float =
    if (a > 16777216 || a < -16777216) throw ArithmeticException("Not enough precision") else a.toFloat()
inline fun toDouble(a: Long): Double =
    if (a > 9007199254740992 || a < -9007199254740992) throw ArithmeticException("Not enough precision") else a.toDouble()
