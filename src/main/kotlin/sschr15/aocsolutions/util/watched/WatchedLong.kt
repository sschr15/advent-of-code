package sschr15.aocsolutions.util.watched

/**
 * A wrapper around [Long] that throws an exception if an operation would cause an overflow or underflow.
 */
@JvmInline
value class WatchedLong(val value: Long) {
    operator fun compareTo(other: WatchedLong) = value.compareTo(other.value)

    operator fun plus(other: WatchedLong): WatchedLong {
        require(value < Long.MAX_VALUE - other.value) { "Longeger Overflow" }
        require(value > Long.MIN_VALUE + other.value) { "Longeger Underflow" }
        return WatchedLong(value + other.value)
    }

    operator fun minus(other: WatchedLong): WatchedLong {
        require(value > Long.MIN_VALUE + other.value) { "Longeger Underflow" }
        require(value < Long.MAX_VALUE - other.value) { "Longeger Overflow" }
        return WatchedLong(value - other.value)
    }

    operator fun times(other: WatchedLong): WatchedLong {
        require(value < Long.MAX_VALUE / other.value) { "Longeger Overflow" }
        require(value > Long.MIN_VALUE / other.value) { "Longeger Underflow" }
        return WatchedLong(value * other.value)
    }

    // operations incapable of overflow or underflow
    operator fun div(other: WatchedLong) = WatchedLong(value / other.value)
    operator fun rem(other: WatchedLong) = WatchedLong(value % other.value)
    operator fun rangeTo(other: WatchedLong) = value..other.value
    operator fun rangeUntil(other: WatchedLong) = value..<other.value // new as of kt 1.8 if I remember correctly

    // unary operators
    operator fun unaryPlus() = this
    operator fun unaryMinus() = WatchedLong(-value)
    operator fun inc() = this + WatchedLong(1)
    operator fun dec() = this - WatchedLong(1)

    // compat with Long
    operator fun compareTo(other: Long) = value.compareTo(other)
    operator fun plus(other: Long) = this + WatchedLong(other)
    operator fun minus(other: Long) = this - WatchedLong(other)
    operator fun times(other: Long) = this * WatchedLong(other)
    operator fun div(other: Long) = this / WatchedLong(other)
    operator fun rem(other: Long) = this % WatchedLong(other)
    operator fun rangeTo(other: Long) = this..WatchedLong(other)
    operator fun rangeUntil(other: Long) = this..<WatchedLong(other)

    override fun toString() = value.toString()
}

fun Long.watched() = WatchedLong(this)
fun String.toWatchedLong(radix: Int = 10) = WatchedLong(toLong(radix))
fun String.toWatchedLongOrNull(radix: Int = 10) = toLongOrNull(radix)?.let(::WatchedLong)

fun WatchedInt.toLong() = WatchedLong(value.toLong())
fun WatchedLong.toInt() = WatchedInt(value.toInt()) // may be lossy, watch out

operator fun Long.plus(other: WatchedLong) = WatchedLong(this) + other
operator fun Long.minus(other: WatchedLong) = WatchedLong(this) - other
operator fun Long.times(other: WatchedLong) = WatchedLong(this) * other
operator fun Long.div(other: WatchedLong) = WatchedLong(this) / other
operator fun Long.rem(other: WatchedLong) = WatchedLong(this) % other
operator fun Long.rangeTo(other: WatchedLong) = WatchedLong(this)..other
operator fun Long.rangeUntil(other: WatchedLong) = WatchedLong(this)..<other

//region: Bitwise operators (no need to check for overflow - purely here for completeness)
infix fun WatchedLong.and(other: WatchedLong) = WatchedLong(value and other.value)
infix fun WatchedLong.or(other: WatchedLong) = WatchedLong(value or other.value)
infix fun WatchedLong.xor(other: WatchedLong) = WatchedLong(value xor other.value)
infix fun WatchedLong.shl(other: WatchedInt) = WatchedLong(value shl other.value)
infix fun WatchedLong.shr(other: WatchedInt) = WatchedLong(value shr other.value)
infix fun WatchedLong.ushr(other: WatchedInt) = WatchedLong(value ushr other.value)

infix fun WatchedLong.and(other: Long) = WatchedLong(value and other)
infix fun WatchedLong.or(other: Long) = WatchedLong(value or other)
infix fun WatchedLong.xor(other: Long) = WatchedLong(value xor other)
infix fun WatchedLong.shl(other: Int) = WatchedLong(value shl other)
infix fun WatchedLong.shr(other: Int) = WatchedLong(value shr other)
infix fun WatchedLong.ushr(other: Int) = WatchedLong(value ushr other)
//endregion

//region: Byte
operator fun Byte.plus(other: WatchedLong) = WatchedLong(toLong()) + other
operator fun WatchedLong.plus(other: Byte) = this + WatchedLong(other.toLong())
operator fun Byte.minus(other: WatchedLong) = WatchedLong(toLong()) - other
operator fun WatchedLong.minus(other: Byte) = this - WatchedLong(other.toLong())
operator fun Byte.times(other: WatchedLong) = WatchedLong(toLong()) * other
operator fun WatchedLong.times(other: Byte) = this * WatchedLong(other.toLong())
operator fun Byte.div(other: WatchedLong) = WatchedLong(toLong()) / other
operator fun WatchedLong.div(other: Byte) = this / WatchedLong(other.toLong())
operator fun Byte.rem(other: WatchedLong) = WatchedLong(toLong()) % other
operator fun WatchedLong.rem(other: Byte) = this % WatchedLong(other.toLong())
operator fun Byte.rangeTo(other: WatchedLong) = WatchedLong(toLong())..other
operator fun WatchedLong.rangeTo(other: Byte) = this..WatchedLong(other.toLong())
operator fun Byte.rangeUntil(other: WatchedLong) = WatchedLong(toLong())..<other
operator fun WatchedLong.rangeUntil(other: Byte) = this..<WatchedLong(other.toLong())
operator fun Byte.compareTo(other: WatchedLong) = WatchedLong(toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: Byte) = this.compareTo(WatchedLong(other.toLong()))

operator fun UByte.plus(other: WatchedLong) = WatchedLong(toLong()) + other
operator fun WatchedLong.plus(other: UByte) = this + WatchedLong(other.toLong())
operator fun UByte.minus(other: WatchedLong) = WatchedLong(toLong()) - other
operator fun WatchedLong.minus(other: UByte) = this - WatchedLong(other.toLong())
operator fun UByte.times(other: WatchedLong) = WatchedLong(toLong()) * other
operator fun WatchedLong.times(other: UByte) = this * WatchedLong(other.toLong())
operator fun UByte.div(other: WatchedLong) = WatchedLong(toLong()) / other
operator fun WatchedLong.div(other: UByte) = this / WatchedLong(other.toLong())
operator fun UByte.rem(other: WatchedLong) = WatchedLong(toLong()) % other
operator fun WatchedLong.rem(other: UByte) = this % WatchedLong(other.toLong())
operator fun UByte.rangeTo(other: WatchedLong) = WatchedLong(toLong())..other
operator fun WatchedLong.rangeTo(other: UByte) = this..WatchedLong(other.toLong())
operator fun UByte.rangeUntil(other: WatchedLong) = WatchedLong(toLong())..<other
operator fun WatchedLong.rangeUntil(other: UByte) = this..<WatchedLong(other.toLong())
operator fun UByte.compareTo(other: WatchedLong) = WatchedLong(toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: UByte) = this.compareTo(WatchedLong(other.toLong()))
//endregion

//region: Short
operator fun Short.plus(other: WatchedLong) = WatchedLong(toLong()) + other
operator fun WatchedLong.plus(other: Short) = this + WatchedLong(other.toLong())
operator fun Short.minus(other: WatchedLong) = WatchedLong(toLong()) - other
operator fun WatchedLong.minus(other: Short) = this - WatchedLong(other.toLong())
operator fun Short.times(other: WatchedLong) = WatchedLong(toLong()) * other
operator fun WatchedLong.times(other: Short) = this * WatchedLong(other.toLong())
operator fun Short.div(other: WatchedLong) = WatchedLong(toLong()) / other
operator fun WatchedLong.div(other: Short) = this / WatchedLong(other.toLong())
operator fun Short.rem(other: WatchedLong) = WatchedLong(toLong()) % other
operator fun WatchedLong.rem(other: Short) = this % WatchedLong(other.toLong())
operator fun Short.rangeTo(other: WatchedLong) = WatchedLong(toLong())..other
operator fun WatchedLong.rangeTo(other: Short) = this..WatchedLong(other.toLong())
operator fun Short.rangeUntil(other: WatchedLong) = WatchedLong(toLong())..<other
operator fun WatchedLong.rangeUntil(other: Short) = this..<WatchedLong(other.toLong())
operator fun Short.compareTo(other: WatchedLong) = WatchedLong(toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: Short) = this.compareTo(WatchedLong(other.toLong()))

operator fun UShort.plus(other: WatchedLong) = WatchedLong(toLong()) + other
operator fun WatchedLong.plus(other: UShort) = this + WatchedLong(other.toLong())
operator fun UShort.minus(other: WatchedLong) = WatchedLong(toLong()) - other
operator fun WatchedLong.minus(other: UShort) = this - WatchedLong(other.toLong())
operator fun UShort.times(other: WatchedLong) = WatchedLong(toLong()) * other
operator fun WatchedLong.times(other: UShort) = this * WatchedLong(other.toLong())
operator fun UShort.div(other: WatchedLong) = WatchedLong(toLong()) / other
operator fun WatchedLong.div(other: UShort) = this / WatchedLong(other.toLong())
operator fun UShort.rem(other: WatchedLong) = WatchedLong(toLong()) % other
operator fun WatchedLong.rem(other: UShort) = this % WatchedLong(other.toLong())
operator fun UShort.rangeTo(other: WatchedLong) = WatchedLong(toLong())..other
operator fun WatchedLong.rangeTo(other: UShort) = this..WatchedLong(other.toLong())
operator fun UShort.rangeUntil(other: WatchedLong) = WatchedLong(toLong())..<other
operator fun WatchedLong.rangeUntil(other: UShort) = this..<WatchedLong(other.toLong())
operator fun UShort.compareTo(other: WatchedLong) = WatchedLong(toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: UShort) = this.compareTo(WatchedLong(other.toLong()))
//endregion

//region: Int
operator fun Int.plus(other: WatchedLong) = WatchedLong(toLong()) + other
operator fun WatchedLong.plus(other: Int) = this + WatchedLong(other.toLong())
operator fun Int.minus(other: WatchedLong) = WatchedLong(toLong()) - other
operator fun WatchedLong.minus(other: Int) = this - WatchedLong(other.toLong())
operator fun Int.times(other: WatchedLong) = WatchedLong(toLong()) * other
operator fun WatchedLong.times(other: Int) = this * WatchedLong(other.toLong())
operator fun Int.div(other: WatchedLong) = WatchedLong(toLong()) / other
operator fun WatchedLong.div(other: Int) = this / WatchedLong(other.toLong())
operator fun Int.rem(other: WatchedLong) = WatchedLong(toLong()) % other
operator fun WatchedLong.rem(other: Int) = this % WatchedLong(other.toLong())
operator fun Int.rangeTo(other: WatchedLong) = WatchedLong(toLong())..other
operator fun WatchedLong.rangeTo(other: Int) = this..WatchedLong(other.toLong())
operator fun Int.rangeUntil(other: WatchedLong) = WatchedLong(toLong())..<other
operator fun WatchedLong.rangeUntil(other: Int) = this..<WatchedLong(other.toLong())
operator fun Int.compareTo(other: WatchedLong) = WatchedLong(toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: Int) = this.compareTo(WatchedLong(other.toLong()))

operator fun UInt.plus(other: WatchedLong) = WatchedLong(toLong()) + other
operator fun WatchedLong.plus(other: UInt) = this + WatchedLong(other.toLong())
operator fun UInt.minus(other: WatchedLong) = WatchedLong(toLong()) - other
operator fun WatchedLong.minus(other: UInt) = this - WatchedLong(other.toLong())
operator fun UInt.times(other: WatchedLong) = WatchedLong(toLong()) * other
operator fun WatchedLong.times(other: UInt) = this * WatchedLong(other.toLong())
operator fun UInt.div(other: WatchedLong) = WatchedLong(toLong()) / other
operator fun WatchedLong.div(other: UInt) = this / WatchedLong(other.toLong())
operator fun UInt.rem(other: WatchedLong) = WatchedLong(toLong()) % other
operator fun WatchedLong.rem(other: UInt) = this % WatchedLong(other.toLong())
operator fun UInt.rangeTo(other: WatchedLong) = WatchedLong(toLong())..other
operator fun WatchedLong.rangeTo(other: UInt) = this..WatchedLong(other.toLong())
operator fun UInt.rangeUntil(other: WatchedLong) = WatchedLong(toLong())..<other
operator fun WatchedLong.rangeUntil(other: UInt) = this..<WatchedLong(other.toLong())
operator fun UInt.compareTo(other: WatchedLong) = WatchedLong(toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: UInt) = this.compareTo(WatchedLong(other.toLong()))

operator fun WatchedInt.plus(other: WatchedLong) = WatchedLong(value.toLong()) + other
operator fun WatchedLong.plus(other: WatchedInt) = this + WatchedLong(other.value.toLong())
operator fun WatchedInt.minus(other: WatchedLong) = WatchedLong(value.toLong()) - other
operator fun WatchedLong.minus(other: WatchedInt) = this - WatchedLong(other.value.toLong())
operator fun WatchedInt.times(other: WatchedLong) = WatchedLong(value.toLong()) * other
operator fun WatchedLong.times(other: WatchedInt) = this * WatchedLong(other.value.toLong())
operator fun WatchedInt.div(other: WatchedLong) = WatchedLong(value.toLong()) / other
operator fun WatchedLong.div(other: WatchedInt) = this / WatchedLong(other.value.toLong())
operator fun WatchedInt.rem(other: WatchedLong) = WatchedLong(value.toLong()) % other
operator fun WatchedLong.rem(other: WatchedInt) = this % WatchedLong(other.value.toLong())
operator fun WatchedInt.rangeTo(other: WatchedLong) = WatchedLong(value.toLong())..other
operator fun WatchedLong.rangeTo(other: WatchedInt) = this..WatchedLong(other.value.toLong())
operator fun WatchedInt.rangeUntil(other: WatchedLong) = WatchedLong(value.toLong())..<other
operator fun WatchedLong.rangeUntil(other: WatchedInt) = this..<WatchedLong(other.value.toLong())
operator fun WatchedInt.compareTo(other: WatchedLong) = WatchedLong(value.toLong()).compareTo(other)
operator fun WatchedLong.compareTo(other: WatchedInt) = this.compareTo(WatchedLong(other.value.toLong()))
//endregion

//region: WatchedInt -> WatchedLong because more needed space
operator fun WatchedInt.plus(other: UInt) = WatchedLong(value.toLong()) + WatchedLong(other.toLong())
operator fun UInt.plus(other: WatchedInt) = WatchedLong(toLong()) + WatchedLong(other.value.toLong())
operator fun WatchedInt.minus(other: UInt) = WatchedLong(value.toLong()) - WatchedLong(other.toLong())
operator fun UInt.minus(other: WatchedInt) = WatchedLong(toLong()) - WatchedLong(other.value.toLong())
operator fun WatchedInt.times(other: UInt) = WatchedLong(value.toLong()) * WatchedLong(other.toLong())
operator fun UInt.times(other: WatchedInt) = WatchedLong(toLong()) * WatchedLong(other.value.toLong())
operator fun WatchedInt.div(other: UInt) = WatchedLong(value.toLong()) / WatchedLong(other.toLong())
operator fun UInt.div(other: WatchedInt) = WatchedLong(toLong()) / WatchedLong(other.value.toLong())
operator fun WatchedInt.rem(other: UInt) = WatchedLong(value.toLong()) % WatchedLong(other.toLong())
operator fun UInt.rem(other: WatchedInt) = WatchedLong(toLong()) % WatchedLong(other.value.toLong())
operator fun WatchedInt.rangeTo(other: UInt) = WatchedLong(value.toLong())..WatchedLong(other.toLong())
operator fun UInt.rangeTo(other: WatchedInt) = WatchedLong(toLong())..WatchedLong(other.value.toLong())
operator fun WatchedInt.rangeUntil(other: UInt) = WatchedLong(value.toLong())..WatchedLong(other.toLong())
operator fun UInt.rangeUntil(other: WatchedInt) = WatchedLong(toLong())..WatchedLong(other.value.toLong())

operator fun WatchedInt.plus(other: Long) = WatchedLong(value.toLong()) + WatchedLong(other)
operator fun Long.plus(other: WatchedInt) = WatchedLong(this) + WatchedLong(other.value.toLong())
operator fun WatchedInt.minus(other: Long) = WatchedLong(value.toLong()) - WatchedLong(other)
operator fun Long.minus(other: WatchedInt) = WatchedLong(this) - WatchedLong(other.value.toLong())
operator fun WatchedInt.times(other: Long) = WatchedLong(value.toLong()) * WatchedLong(other)
operator fun Long.times(other: WatchedInt) = WatchedLong(this) * WatchedLong(other.value.toLong())
operator fun WatchedInt.div(other: Long) = WatchedLong(value.toLong()) / WatchedLong(other)
operator fun Long.div(other: WatchedInt) = WatchedLong(this) / WatchedLong(other.value.toLong())
operator fun WatchedInt.rem(other: Long) = WatchedLong(value.toLong()) % WatchedLong(other)
operator fun Long.rem(other: WatchedInt) = WatchedLong(this) % WatchedLong(other.value.toLong())
operator fun WatchedInt.rangeTo(other: Long) = WatchedLong(value.toLong())..WatchedLong(other)
operator fun Long.rangeTo(other: WatchedInt) = WatchedLong(this)..WatchedLong(other.value.toLong())
operator fun WatchedInt.rangeUntil(other: Long) = WatchedLong(value.toLong())..WatchedLong(other)
operator fun Long.rangeUntil(other: WatchedInt) = WatchedLong(this)..WatchedLong(other.value.toLong())
//endregion

// can't do ULong because it doesn't necessarily fit in a Long
