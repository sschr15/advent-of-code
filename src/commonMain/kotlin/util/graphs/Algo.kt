package sschr15.aocsolutions.util.graphs

expect class GraphPath<N : Any> : Iterable<N> {
    val length: Int
    val totalWeight: Double
    override fun iterator(): Iterator<N>
}

expect class AStar<N : Any> {
    fun findPath(start: N, end: N): GraphPath<N>?
}

//expect fun <N : Any> Graph<N>.aStar(): AStar<N>
expect fun <N : Any> Graph<N>.aStar(heuristic: (N, N) -> Double): AStar<N>

expect class MaximalCliques<N : Any> : Iterable<Set<N>> {
    val count: Int
    override fun iterator(): Iterator<Set<N>>
}

expect fun <N : Any> Graph<N>.maxCliques(): MaximalCliques<N>
