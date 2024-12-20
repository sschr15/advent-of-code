package sschr15.aocsolutions.util

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue

typealias BigPoint = Pair<Long, Long>
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

/**
 * Like the other dijkstra, but with an abort function that can be used to stop the search early.
 */
fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
    abort: (T, cost: Int) -> Boolean,
): Map<T, Int> {
    data class NodeInAbort(val value: T, val cost: Int)

    val visited = mutableSetOf<T>()
    val costs = Object2IntOpenHashMap<T>()
    val queue = PriorityQueue<NodeInAbort>(compareBy { it.cost })
    queue.add(NodeInAbort(start, 0))
    costs.put(start, 0)

    while (queue.isNotEmpty()) {
        val (current, currentCost) = queue.poll()
        if (current in visited) continue
        visited.add(current)

        if (abort(current, currentCost)) return costs

        for (neighbor in getNeighbors(current)) {
            val newCost = currentCost + getCost(neighbor)
            if (newCost < costs.getOrDefault(neighbor as Any, Int.MAX_VALUE)) {
                costs.put(neighbor, newCost)
                queue.add(NodeInAbort(neighbor, newCost))
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

private fun <T> reconstructPath(cameFrom: Map<T, T>, current: T): List<T> {
    val path = mutableListOf(current)
    var node = current
    while (node in cameFrom) {
        node = cameFrom[node]!!
        path.add(node)
    }
    return path.reversed()
}

fun <T> Grid<T>.aStar(
    start: Point,
    end: Point,
    getCost: (current: Point, neighbor: Point) -> Int,
    heuristic: (current: Point, goal: Point) -> Int = AbstractPoint::manhattanDistance,
): List<Point>? {
    data class Node(val point: Point, val f: Int)

    val open = PriorityQueue<Node>(compareBy { it.f })
    val closed = mutableSetOf<Point>()
    val cameFrom = mutableMapOf<Point, Point>()
    val gScore = defaultMap(Int.MAX_VALUE, start to 0)
    val fScore = defaultMap(Int.MAX_VALUE, start to heuristic(start, end))

    open.add(Node(start, 0))

    while (open.isNotEmpty()) {
        val current = open.poll().point
        if (current == end) return reconstructPath(cameFrom, current)

        closed.add(current)

        for (neighbor in current.neighborsIn(this)) {
            if (neighbor in closed) continue

            val tentativeGScore = gScore[current] + getCost(current, neighbor)
            if (tentativeGScore < gScore[neighbor]) {
                cameFrom[neighbor] = current
                gScore[neighbor] = tentativeGScore
                fScore[neighbor] = tentativeGScore + heuristic(neighbor, end)
                open.add(Node(neighbor, fScore[neighbor]))
            }
        }
    }

    return null
}
