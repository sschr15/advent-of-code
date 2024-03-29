package sschr15.aocsolutions.util

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import sschr15.aocsolutions.util.watched.WatchedLong
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue

typealias BigPoint = Pair<WatchedLong, WatchedLong>
typealias BiggerPoint = Pair<BigInteger, BigInteger>

fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
): Map<T, Int> {
    data class Node(val value: T, val cost: Int)

    val visited = mutableSetOf<T>()
    val costs = Object2IntOpenHashMap<T>()
    val queue = PriorityQueue<Node>(compareBy { it.cost })
    queue.add(Node(start, 0))
    costs.put(start, 0)

    while (queue.isNotEmpty()) {
        val (current, currentCost) = queue.poll()
        if (current in visited) continue
        visited.add(current)

        for (neighbor in getNeighbors(current)) {
            val newCost = currentCost + getCost(neighbor)
            if (newCost < costs.getOrDefault(neighbor as Any, Int.MAX_VALUE)) {
                costs.put(neighbor, newCost)
                queue.add(Node(neighbor, newCost))
            }
        }
    }

    return costs
}

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
fun shoelace(points: List<BigPoint>): WatchedLong {
    var sum = WatchedLong(0)
    for (i in points.indices) {
        if (i == points.lastIndex) break
        sum += points[i].first * points[i + 1].second
        sum -= points[i].second * points[i + 1].first
    }
    sum += points.last().first * points.first().second
    sum -= points.last().second * points.first().first
    return if (sum < 0) sum * -1 else sum
}

@JvmName("shoelaceOhNoItIsCatastrophicallyLarge")
fun shoelace(points: List<BiggerPoint>): BigInteger {
    var sum = BigInteger.ZERO
    for (i in points.indices) {
        if (i == points.lastIndex) break
        sum += points[i].first * points[i + 1].second
        sum -= points[i].second * points[i + 1].first
    }
    sum += points.last().first * points.first().second
    sum -= points.last().second * points.first().first
    return sum.abs() / 2.toBigInteger()
}
