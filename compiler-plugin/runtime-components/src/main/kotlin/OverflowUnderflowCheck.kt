package sschr15.aoc.annotations

fun plus(a: Int, b: Int): Int {
    if (b < 0) return minus(a, -b)
    if (a > Int.MAX_VALUE - b) {
        throw ArithmeticException("Integer Overflow")
    } else if (a < Int.MIN_VALUE + b) {
        throw ArithmeticException("Integer Underflow")
    }
    return a + b
}

fun minus(a: Int, b: Int): Int {
    if (b < 0) return plus(a, -b)
    if (a < Int.MIN_VALUE + b) {
        throw ArithmeticException("Integer Underflow")
    } else if (a > Int.MAX_VALUE - b) {
        throw ArithmeticException("Integer Overflow")
    }
    return a - b
}

fun times(a: Int, b: Int): Int {
    if (b == 0) return 0
    if (a > Int.MAX_VALUE / b) {
        throw ArithmeticException("Integer Overflow")
    } else if (a < Int.MIN_VALUE / b) {
        throw ArithmeticException("Integer Underflow")
    }
    return a * b
}

fun inc(a: Int): Int {
    if (a == Int.MAX_VALUE) {
        throw ArithmeticException("Integer Overflow")
    }
    return a + 1
}

fun dec(a: Int): Int {
    if (a == Int.MIN_VALUE) {
        throw ArithmeticException("Integer Underflow")
    }
    return a - 1
}

fun plus(a: Long, b: Long): Long {
    if (b < 0) return minus(a, -b)
    if (a > Long.MAX_VALUE - b) {
        throw ArithmeticException("Long Overflow")
    } else if (a < Long.MIN_VALUE + b) {
        throw ArithmeticException("Long Underflow")
    }
    return a + b
}

fun minus(a: Long, b: Long): Long {
    if (b < 0) return plus(a, -b)
    if (a < Long.MIN_VALUE + b) {
        throw ArithmeticException("Long Underflow")
    } else if (a > Long.MAX_VALUE - b) {
        throw ArithmeticException("Long Overflow")
    }
    return a - b
}

fun times(a: Long, b: Long): Long {
    if (b == 0L) return 0
    if (a > Long.MAX_VALUE / b) {
        throw ArithmeticException("Long Overflow")
    } else if (a < Long.MIN_VALUE / b) {
        throw ArithmeticException("Long Underflow")
    }
    return a * b
}

fun inc(a: Long): Long {
    if (a == Long.MAX_VALUE) {
        throw ArithmeticException("Long Overflow")
    }
    return a + 1
}

fun dec(a: Long): Long {
    if (a == Long.MIN_VALUE) {
        throw ArithmeticException("Long Underflow")
    }
    return a - 1
}
