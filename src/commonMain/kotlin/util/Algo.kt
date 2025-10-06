package sschr15.aocsolutions.util

import kotlin.jvm.JvmName
import kotlin.math.absoluteValue

typealias BigPoint = Pair<Long, Long>
//typealias BiggerPoint = Pair<BigInteger, BigInteger>

fun shoelace(points: List<Point>): Int {
    var sum = 0
    for (i in points.indices) {
        if (i == points.lastIndex) break
        sum += points[i].x * points[i + 1].y
        sum -= points[i].y * points[i + 1].x
    }
    sum += points.last().x * points.first().y
    sum -= points.last().y * points.first().x
    return sum.absoluteValue
}

@JvmName("shoelaceBig")
fun shoelace(points: List<BigPoint>): Long {
    var sum = 0L
    for (i in points.indices) {
        if (i == points.lastIndex) break
        sum += points[i].first * points[i + 1].second
        sum -= points[i].second * points[i + 1].first
    }
    sum += points.last().first * points.first().second
    sum -= points.last().second * points.first().first
    return if (sum < 0) sum * -1 else sum
}

//@JvmName("shoelaceOhNoItIsCatastrophicallyLarge")
//fun shoelace(points: List<BiggerPoint>): BigInteger {
//    var sum = BigInteger.ZERO
//    for (i in points.indices) {
//        if (i == points.lastIndex) break
//        sum += points[i].first * points[i + 1].second
//        sum -= points[i].second * points[i + 1].first
//    }
//    sum += points.last().first * points.first().second
//    sum -= points.last().second * points.first().first
//    return sum.abs() / 2.toBigInteger()
//}

expect fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
): Map<T, Int>

expect fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
    abort: (T, cost: Int) -> Boolean = { _, _ -> false },
): Map<T, Int>

//expect fun <T> Grid<T>.aStar(
//    start: Point,
//    end: Point,
//    getCost: (current: Point, neighbor: Point) -> Int,
//    isValidPoint: (Point) -> Boolean = { true },
//    heuristic: (current: Point, goal: Point) -> Int = AbstractPoint::manhattanDistance,
//): List<Point>?
