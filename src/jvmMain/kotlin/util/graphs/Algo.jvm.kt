package sschr15.aocsolutions.util.graphs

import org.jgrapht.alg.clique.BronKerboschCliqueFinder
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.GraphPath as JGraphPath

actual class GraphPath<N : Any>(private val path: JGraphPath<N, Any>) : JGraphPath<N, Any> by path, Iterable<N> {
    @get:JvmName("getLengthButNotAsAnOverride")
    actual val length = path.length
    actual val totalWeight = path.weight

    actual override fun iterator(): Iterator<N> = path.vertexList.iterator()

    // delegate default methods too
    override fun getLength(): Int = path.length
    override fun getEdgeList(): List<Any> = path.edgeList
    override fun getVertexList(): List<N> = path.vertexList
}

actual class AStar<N : Any>(path: AStarShortestPath<N, Any>) : ShortestPathAlgorithm<N, Any> by path {
    actual fun findPath(start: N, end: N): GraphPath<N>? {
        return getPath(start, end)?.let(::GraphPath)
    }
}

//actual fun <N : Any> Graph<N>.aStar(): AStar<N> = AStar(AStarShortestPath(this, ALTAdmissibleHeuristic(this, vertexSet())))
actual fun <N : Any> Graph<N>.aStar(heuristic: (N, N) -> Double): AStar<N> =
    AStar(AStarShortestPath(this, AStarAdmissibleHeuristic(heuristic)))

actual class MaximalCliques<N : Any>(private val cliqueFinder: BronKerboschCliqueFinder<N, Any>) : Iterable<Set<N>> {
    actual val count: Int get() = cliqueFinder.count()
    actual override fun iterator(): Iterator<Set<N>> = cliqueFinder.iterator()
}

actual fun <N : Any> Graph<N>.maxCliques(): MaximalCliques<N> = MaximalCliques(BronKerboschCliqueFinder(this))
