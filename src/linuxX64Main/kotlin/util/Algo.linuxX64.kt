package sschr15.aocsolutions.util

private class ValuedHeap<N>(private val comparator: Comparator<in N>) : Collection<N> {
    private val heap = mutableListOf<N>()

    override val size get() = heap.size
    override fun isEmpty() = heap.isEmpty()
    override fun contains(element: N) = heap.contains(element)
    override fun containsAll(elements: Collection<N>) = heap.containsAll(elements)
    override fun iterator(): Iterator<N> = heap.iterator()
    operator fun get(index: Int) = heap[index]

    fun add(element: N): Boolean {
        heap.add(element)
        heapifyUp(heap.lastIndex)
        return true
    }

    fun peek() = heap.first()
    fun poll(): N {
        if (heap.isEmpty()) throw NoSuchElementException("Heap is empty")
        val result = heap[0]
        heap[0] = heap.last()
        heap.removeLast()
        heapifyDown(0)
        return result
    }

    private fun heapifyUp(index: Int) {
        var i = index
        while (i > 0) {
            val parentIndex = (i - 1) shr 1
            val parent = heap[parentIndex]
            if (comparator.compare(heap[i], parent) >= 0) break
            heap[i] = parent
            i = parentIndex
        }
    }

    private fun heapifyDown(index: Int) {
        var i = index
        while (true) {
            val leftIndex = (i shl 1) + 1
            val rightIndex = leftIndex + 1
            if (leftIndex >= heap.size) break
            val left = heap[leftIndex]
            val right = if (rightIndex >= heap.size) null else heap[rightIndex]
            val child = if (right == null || comparator.compare(left, right) <= 0) left else right
            if (comparator.compare(heap[i], child) <= 0) break
            heap[i] = child
            i = leftIndex
        }
    }
}

actual class PriorityQueue<E : Any> actual constructor(comparator: Comparator<in E>) {
    private val heap = ValuedHeap(comparator)
    actual fun poll(): E = heap.poll()
    actual fun add(element: E): Boolean = heap.add(element)
    actual val size: Int get() = heap.size
    actual fun isEmpty(): Boolean = heap.isEmpty()
    actual fun peek(): E? = heap.peek()
}

actual fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
    abort: (T, cost: Int) -> Boolean,
): Map<T, Int> {
    val Null = Any() as (T & Any)
    val costs = mutableMapOf(start to 0)
    val queue = PriorityQueue(compareBy(costs::getValue))
    queue.add(start ?: Null)
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (abort(current, costs.getValue(current))) continue
        for (neighbor in getNeighbors(current)) {
            val newCost = costs.getValue(current) + getCost(neighbor)
            if (newCost < (costs[neighbor] ?: Int.MAX_VALUE)) {
                costs[neighbor] = newCost
                queue.add(neighbor ?: Null)
            }
        }
    }
    return costs
}

actual fun <T> dijkstra(
    start: T,
    getNeighbors: (T) -> List<T>,
    getCost: (T) -> Int,
): Map<T, Int> = dijkstra(start, getNeighbors, getCost) { _, _ -> false }
