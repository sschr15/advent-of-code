package sschr15.aocsolutions.util.watched

/**
 * A wrapper around [Int] that throws an exception if an operation would cause an overflow or underflow.
 */
@JvmInline
value class WatchedInt(val value: Int) {
    operator fun compareTo(other: WatchedInt) = value.compareTo(other.value)

    operator fun plus(other: WatchedInt): WatchedInt {
        require(value < Int.MAX_VALUE - other.value) { "Integer Overflow" }
        require(value > Int.MIN_VALUE + other.value) { "Integer Underflow" }
        return WatchedInt(value + other.value)
    }

    operator fun minus(other: WatchedInt): WatchedInt {
        require(value > Int.MIN_VALUE + other.value) { "Integer Underflow" }
        require(value < Int.MAX_VALUE - other.value) { "Integer Overflow" }
        return WatchedInt(value - other.value)
    }

    operator fun times(other: WatchedInt): WatchedInt {
        require(value < Int.MAX_VALUE / other.value) { "Integer Overflow" }
        require(value > Int.MIN_VALUE / other.value) { "Integer Underflow" }
        return WatchedInt(value * other.value)
    }

    // operations incapable of overflow or underflow
    operator fun div(other: WatchedInt) = WatchedInt(value / other.value)
    operator fun rem(other: WatchedInt) = WatchedInt(value % other.value)
    operator fun rangeTo(other: WatchedInt) = value..other.value
    operator fun rangeUntil(other: WatchedInt) = value..<other.value // new as of kt 1.8 if I remember correctly

    // unary operators
    operator fun unaryPlus() = this
    operator fun unaryMinus() = WatchedInt(-value)
    operator fun inc() = this + WatchedInt(1)
    operator fun dec() = this - WatchedInt(1)

    // compat with Int
    operator fun compareTo(other: Int) = value.compareTo(other)
    operator fun plus(other: Int) = this + WatchedInt(other)
    operator fun minus(other: Int) = this - WatchedInt(other)
    operator fun times(other: Int) = this * WatchedInt(other)
    operator fun div(other: Int) = this / WatchedInt(other)
    operator fun rem(other: Int) = this % WatchedInt(other)
    operator fun rangeTo(other: Int) = this..WatchedInt(other)
    operator fun rangeUntil(other: Int) = this..<WatchedInt(other)
    
    override fun toString() = value.toString()
}

fun Int.watched() = WatchedInt(this)
fun Byte.watched() = WatchedInt(toInt())
fun Short.watched() = WatchedInt(toInt())
fun UByte.watched() = WatchedInt(toInt())
fun UShort.watched() = WatchedInt(toInt())

fun Char.digitToWatchedInt(radix: Int = 10) = WatchedInt(digitToInt(radix))
fun Char.digitToWatchedIntOrNull(radix: Int = 10) = digitToIntOrNull(radix)?.let(::WatchedInt)
fun String.toWatchedInt(radix: Int = 10) = WatchedInt(toInt(radix))
fun String.toWatchedIntOrNull(radix: Int = 10) = toIntOrNull(radix)?.let(::WatchedInt)

@Deprecated("Only here for consistency and maybe speed", ReplaceWith("this.code.watched()"))
fun Char.watched() = WatchedInt(code)

operator fun Int.plus(other: WatchedInt) = WatchedInt(this) + other
operator fun Int.minus(other: WatchedInt) = WatchedInt(this) - other
operator fun Int.times(other: WatchedInt) = WatchedInt(this) * other
operator fun Int.div(other: WatchedInt) = WatchedInt(this) / other
operator fun Int.rem(other: WatchedInt) = WatchedInt(this) % other
operator fun Int.rangeTo(other: WatchedInt) = WatchedInt(this)..other
operator fun Int.rangeUntil(other: WatchedInt) = WatchedInt(this)..<other

//region: Bitwise operators (no need to check for overflow - purely here for completeness)
infix fun WatchedInt.and(other: WatchedInt) = WatchedInt(value and other.value)
infix fun WatchedInt.or(other: WatchedInt) = WatchedInt(value or other.value)
infix fun WatchedInt.xor(other: WatchedInt) = WatchedInt(value xor other.value)
infix fun WatchedInt.shl(other: WatchedInt) = WatchedInt(value shl other.value)
infix fun WatchedInt.shr(other: WatchedInt) = WatchedInt(value shr other.value)
infix fun WatchedInt.ushr(other: WatchedInt) = WatchedInt(value ushr other.value)

infix fun WatchedInt.and(other: Int) = WatchedInt(value and other)
infix fun WatchedInt.or(other: Int) = WatchedInt(value or other)
infix fun WatchedInt.xor(other: Int) = WatchedInt(value xor other)
infix fun WatchedInt.shl(other: Int) = WatchedInt(value shl other)
infix fun WatchedInt.shr(other: Int) = WatchedInt(value shr other)
infix fun WatchedInt.ushr(other: Int) = WatchedInt(value ushr other)
//endregion

//region: Byte
operator fun Byte.plus(other: WatchedInt) = WatchedInt(toInt()) + other
operator fun WatchedInt.plus(other: Byte) = this + WatchedInt(other.toInt())
operator fun Byte.minus(other: WatchedInt) = WatchedInt(toInt()) - other
operator fun WatchedInt.minus(other: Byte) = this - WatchedInt(other.toInt())
operator fun Byte.times(other: WatchedInt) = WatchedInt(toInt()) * other
operator fun WatchedInt.times(other: Byte) = this * WatchedInt(other.toInt())
operator fun Byte.div(other: WatchedInt) = WatchedInt(toInt()) / other
operator fun WatchedInt.div(other: Byte) = this / WatchedInt(other.toInt())
operator fun Byte.rem(other: WatchedInt) = WatchedInt(toInt()) % other
operator fun WatchedInt.rem(other: Byte) = this % WatchedInt(other.toInt())
operator fun Byte.rangeTo(other: WatchedInt) = WatchedInt(toInt())..other
operator fun WatchedInt.rangeTo(other: Byte) = this..WatchedInt(other.toInt())
operator fun Byte.rangeUntil(other: WatchedInt) = WatchedInt(toInt())..<other
operator fun WatchedInt.rangeUntil(other: Byte) = this..<WatchedInt(other.toInt())
operator fun Byte.compareTo(other: WatchedInt) = WatchedInt(toInt()).compareTo(other)
operator fun WatchedInt.compareTo(other: Byte) = this.compareTo(WatchedInt(other.toInt()))

operator fun UByte.plus(other: WatchedInt) = WatchedInt(toInt()) + other
operator fun WatchedInt.plus(other: UByte) = this + WatchedInt(other.toInt())
operator fun UByte.minus(other: WatchedInt) = WatchedInt(toInt()) - other
operator fun WatchedInt.minus(other: UByte) = this - WatchedInt(other.toInt())
operator fun UByte.times(other: WatchedInt) = WatchedInt(toInt()) * other
operator fun WatchedInt.times(other: UByte) = this * WatchedInt(other.toInt())
operator fun UByte.div(other: WatchedInt) = WatchedInt(toInt()) / other
operator fun WatchedInt.div(other: UByte) = this / WatchedInt(other.toInt())
operator fun UByte.rem(other: WatchedInt) = WatchedInt(toInt()) % other
operator fun WatchedInt.rem(other: UByte) = this % WatchedInt(other.toInt())
operator fun UByte.rangeTo(other: WatchedInt) = WatchedInt(toInt())..other
operator fun WatchedInt.rangeTo(other: UByte) = this..WatchedInt(other.toInt())
operator fun UByte.rangeUntil(other: WatchedInt) = WatchedInt(toInt())..<other
operator fun WatchedInt.rangeUntil(other: UByte) = this..<WatchedInt(other.toInt())
operator fun UByte.compareTo(other: WatchedInt) = WatchedInt(toInt()).compareTo(other)
operator fun WatchedInt.compareTo(other: UByte) = this.compareTo(WatchedInt(other.toInt()))
//endregion

//region: Short
operator fun Short.plus(other: WatchedInt) = WatchedInt(toInt()) + other
operator fun WatchedInt.plus(other: Short) = this + WatchedInt(other.toInt())
operator fun Short.minus(other: WatchedInt) = WatchedInt(toInt()) - other
operator fun WatchedInt.minus(other: Short) = this - WatchedInt(other.toInt())
operator fun Short.times(other: WatchedInt) = WatchedInt(toInt()) * other
operator fun WatchedInt.times(other: Short) = this * WatchedInt(other.toInt())
operator fun Short.div(other: WatchedInt) = WatchedInt(toInt()) / other
operator fun WatchedInt.div(other: Short) = this / WatchedInt(other.toInt())
operator fun Short.rem(other: WatchedInt) = WatchedInt(toInt()) % other
operator fun WatchedInt.rem(other: Short) = this % WatchedInt(other.toInt())
operator fun Short.rangeTo(other: WatchedInt) = WatchedInt(toInt())..other
operator fun WatchedInt.rangeTo(other: Short) = this..WatchedInt(other.toInt())
operator fun Short.rangeUntil(other: WatchedInt) = WatchedInt(toInt())..<other
operator fun WatchedInt.rangeUntil(other: Short) = this..<WatchedInt(other.toInt())
operator fun Short.compareTo(other: WatchedInt) = WatchedInt(toInt()).compareTo(other)
operator fun WatchedInt.compareTo(other: Short) = this.compareTo(WatchedInt(other.toInt()))

operator fun UShort.plus(other: WatchedInt) = WatchedInt(toInt()) + other
operator fun WatchedInt.plus(other: UShort) = this + WatchedInt(other.toInt())
operator fun UShort.minus(other: WatchedInt) = WatchedInt(toInt()) - other
operator fun WatchedInt.minus(other: UShort) = this - WatchedInt(other.toInt())
operator fun UShort.times(other: WatchedInt) = WatchedInt(toInt()) * other
operator fun WatchedInt.times(other: UShort) = this * WatchedInt(other.toInt())
operator fun UShort.div(other: WatchedInt) = WatchedInt(toInt()) / other
operator fun WatchedInt.div(other: UShort) = this / WatchedInt(other.toInt())
operator fun UShort.rem(other: WatchedInt) = WatchedInt(toInt()) % other
operator fun WatchedInt.rem(other: UShort) = this % WatchedInt(other.toInt())
operator fun UShort.rangeTo(other: WatchedInt) = WatchedInt(toInt())..other
operator fun WatchedInt.rangeTo(other: UShort) = this..WatchedInt(other.toInt())
operator fun UShort.rangeUntil(other: WatchedInt) = WatchedInt(toInt())..<other
operator fun WatchedInt.rangeUntil(other: UShort) = this..<WatchedInt(other.toInt())
operator fun UShort.compareTo(other: WatchedInt) = WatchedInt(toInt()).compareTo(other)
operator fun WatchedInt.compareTo(other: UShort) = this.compareTo(WatchedInt(other.toInt()))
//endregion

// (UInt, Long, and ULong don't necessarily fit in an Int)
