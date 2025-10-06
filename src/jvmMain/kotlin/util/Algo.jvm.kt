package sschr15.aocsolutions.util

import it.unimi.dsi.fastutil.PriorityQueues
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue

actual fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
): Map<T, Int> {
    data class Node(val value: T, val cost: Int)

    val visited = mutableSetOf<T>()
    val costs = Object2IntOpenHashMap<T>()
    val queue = ObjectHeapPriorityQueue<Node>(compareBy { it.cost })
    queue.enqueue(Node(start, 0))
    costs.put(start, 0)

    while (!queue.isEmpty) {
        val (current, currentCost) = queue.dequeue()
        if (current in visited) continue
        visited.add(current)

        for (neighbor in getNeighbors(current)) {
            val newCost = currentCost + getCost(neighbor)
            if (newCost < costs.getOrDefault(neighbor as Any, Int.MAX_VALUE)) {
                costs.put(neighbor, newCost)
                queue.enqueue(Node(neighbor, newCost))
            }
        }
    }

    return costs
}

/**
 * Like the other dijkstra, but with an abort function that can be used to stop the search early.
 */
actual fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
    abort: (T, cost: Int) -> Boolean,
): Map<T, Int> {
    data class NodeInAbort(val value: T, val cost: Int)

    val visited = mutableSetOf<T>()
    val costs = Object2IntOpenHashMap<T>()
    val queue = ObjectHeapPriorityQueue<NodeInAbort>(compareBy { it.cost })
    queue.enqueue(NodeInAbort(start, 0))
    costs.put(start, 0)

    while (!queue.isEmpty) {
        val (current, currentCost) = queue.dequeue()
        if (current in visited) continue
        visited.add(current)

        if (abort(current, currentCost)) return costs

        for (neighbor in getNeighbors(current)) {
            val newCost = currentCost + getCost(neighbor)
            if (newCost < costs.getOrDefault(neighbor as Any, Int.MAX_VALUE)) {
                costs.put(neighbor, newCost)
                queue.enqueue(NodeInAbort(neighbor, newCost))
            }
        }
    }

    return costs
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

/* actual */ fun <T> Grid<T>.aStar(
    start: Point,
    end: Point,
    getCost: (current: Point, neighbor: Point) -> Int,
    isValidPoint: (Point) -> Boolean,
    heuristic: (current: Point, goal: Point) -> Int,
): List<Point>? {
    data class Node(val point: Point, val f: Int)

    val open = ObjectHeapPriorityQueue<Node>(compareBy { it.f })
    val closed = mutableSetOf<Point>()
    val cameFrom = mutableMapOf<Point, Point>()
    val gScore = defaultMap(Int.MAX_VALUE, start to 0)
    val fScore = defaultMap(Int.MAX_VALUE, start to heuristic(start, end))

    open.enqueue(Node(start, 0))

    while (!open.isEmpty) {
        val current = open.dequeue().point
        if (current == end) return reconstructPath(cameFrom, current)

        closed.add(current)

        for (neighbor in current.neighborsIn(this)) {
            if (neighbor in closed) continue
            if (!isValidPoint(neighbor)) continue

            val tentativeGScore = gScore[current] + getCost(current, neighbor)
            if (tentativeGScore < gScore[neighbor]) {
                cameFrom[neighbor] = current
                gScore[neighbor] = tentativeGScore
                fScore[neighbor] = tentativeGScore + heuristic(neighbor, end)
                open.enqueue(Node(neighbor, fScore[neighbor]))
            }
        }
    }

    return null
}
