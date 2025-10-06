package sschr15.aocsolutions.util.graphs

import kotlinx.cinterop.*
import lib.igraph.*
import kotlin.sequences.iterator

actual class GraphPath<N : Any>(
    actual val length: Int,
    actual val totalWeight: Double,
    vertices: List<N>
) : Iterable<N> by vertices

actual class AStar<N : Any>(private val graph: Graph<N>, private val heuristic: (N, N) -> Double) {
    private val heuristicRef = StableRef.create { from: Long, to: Long ->
        heuristic(graph.idToNodeMap[from.toInt()], graph.idToNodeMap[to.toInt()])
    }

    actual fun findPath(start: N, end: N): GraphPath<N>? = memScoped {
        val vertices = alloc<igraph_vector_int_t>()
        igraph_vector_int_init(vertices.ptr, 0)

        val edges = if (graph.isWeighted) {
            alloc<igraph_vector_int_t>().also { igraph_vector_int_init(it.ptr, 0) }
        } else null

        try {
            val start = graph.nodeToIdMap[start] ?: return null
            val end = graph.nodeToIdMap[end] ?: return null
            igraph_get_shortest_path_astar(
                graph.backing.ptr,
                vertices.ptr, edges?.ptr,
                start, end,
                graph.edgeWeights?.ptr,
                mode = IGRAPH_OUT,
                heuristic = staticCFunction { result: CPointer<DoubleVar>?, from: Long, to: Long, extra: CPointer<*>? ->
                    val actualHeuristic = extra!!.asStableRef<(Long, Long) -> Double>()
                    val estimatedWeight = actualHeuristic.get()(from, to)
                    result!![0] = estimatedWeight
                    0u
                },
                extra = heuristicRef.asCPointer()
            )

            val totalWeight = if (graph.isWeighted) {
                var sum = 0.0
                for (i in 0..<igraph_vector_int_size(edges!!.ptr)) {
                    sum += igraph_vector_get(graph.edgeWeights!!.ptr, i)
                }
                sum
            } else igraph_vector_int_size(vertices.ptr).toDouble() - 1

            GraphPath(
                igraph_vector_int_size(vertices.ptr).toInt(),
                totalWeight,
                List(igraph_vector_int_size(vertices.ptr).toInt()) {
                    graph.idToNodeMap[igraph_vector_int_get(vertices.ptr, it.toLong()).toInt()]
                }
            )
        } finally {
            igraph_vector_int_destroy(vertices.ptr)
            edges?.ptr?.let(::igraph_vector_int_destroy)
        }
    }
}

actual fun <N : Any> Graph<N>.aStar(heuristic: (N, N) -> Double): AStar<N> = AStar(this, heuristic)

actual class MaximalCliques<N : Any>(private val cliques: List<Set<N>>) : Iterable<Set<N>> by cliques {
    actual val count = cliques.size
}

actual fun <N : Any> Graph<N>.maxCliques(): MaximalCliques<N> {
    val cliques = mutableListOf<Set<N>>()
    memScoped {
        igraph_maximal_cliques_callback(
            backing.ptr,
            min_size = 0L,
            max_size = 0L,
            cliquehandler_fn = staticCFunction { vectorPointer, arg ->
                if (vectorPointer == null || arg == null) return@staticCFunction 0u
                val consumer = arg.asStableRef<(Sequence<Long>) -> Unit>().get()
                consumer(sequence {
                    for (i in 0..<igraph_vector_int_size(vectorPointer)) {
                        yield(igraph_vector_int_get(vectorPointer, i))
                    }
                })
                0u
            },
            arg = StableRef.create { itr: Sequence<Long> ->
                cliques.add(itr.map { idToNodeMap[it.toInt()] }.toSet())
            }.asCPointer()
        )
    }
    return MaximalCliques(cliques)
}
