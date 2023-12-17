package sschr15.aocsolutions.util

import java.util.*

fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
): Map<T, Int> {
    val visited = mutableSetOf<T>()
    val costs = mutableMapOf<T, Int>()
    val queue = PriorityQueue<T>(compareBy { costs.getOrDefault(it, Int.MAX_VALUE) })
    queue.add(start)
    costs[start] = 0

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current in visited) continue
        visited.add(current)

        for (neighbor in getNeighbors(current)) {
            val newCost = costs[current]!! + getCost(neighbor)
            if (newCost < costs.getOrDefault(neighbor, Int.MAX_VALUE)) {
                costs[neighbor] = newCost
                queue.add(neighbor)
            }
        }
    }

    return costs
}
