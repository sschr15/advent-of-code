@file:Suppress("unused")

package sschr15.aocsolutions.util.graphwrap

import org.jgrapht.GraphPath
import org.jgrapht.alg.interfaces.*
import org.jgrapht.alg.shortestpath.*
import org.jheaps.AddressableHeap
import org.jheaps.array.DaryArrayAddressableHeap
import org.jheaps.tree.PairingHeap
import java.util.concurrent.ThreadPoolExecutor
import org.jgrapht.Graph as JGraph
import org.jgrapht.alg.util.Pair as JPair

/**
 * A type alias representing a supplier function that creates an addressable heap.
 *
 * @param T The type of elements contained within the heap.
 * @return An instance of `AddressableHeap` with keys of type `Double` and values of type `T`.
 */
typealias HeapSupplier<T> = () -> AddressableHeap<Double, T>

/**
 * Computes all directed paths in the graph from the current instance of `JGraph`.
 *
 * @param validator An optional function to validate edges for inclusion in the paths. The function takes the current partial path
 *                  and an edge as parameters and returns `true` to include the edge or `false` to exclude it.
 *                  If no validator is provided, all edges will be considered valid for path traversal.
 */
fun <T, E> JGraph<T, E>.allDirectedPaths(
    validator: ((partialPath: GraphPath<T, E>, edge: E) -> Boolean)? = null
) = AllDirectedPaths(this, validator)

/**
 * Applies the A* shortest path algorithm to the current graph using the provided heuristic function.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param heuristic The admissible heuristic function used to guide the A* algorithm.
 *                   This function estimates the cost from any vertex to the goal.
 * @param heapSupplier A supplier to create the heap structure used by the algorithm.
 *                     Default is a pairing heap.
 * @return An implementation of the shortest path algorithm based on A* for the current graph.
 */
fun <T, E> JGraph<T, E>.aStar(
    heuristic: AStarAdmissibleHeuristic<T>,
    heapSupplier: HeapSupplier<T> = ::PairingHeap
): ShortestPathAlgorithm<T, E> = AStarShortestPath(this, heuristic, heapSupplier)

/**
 * Implements the Bellman-Ford algorithm to compute the shortest paths
 * from a source vertex to all other vertices in a graph.
 *
 * @param epsilon A small positive value used for numerical stability
 *                when performing floating-point comparisons. Default is 1e-9.
 * @return An instance of the shortest path algorithm result, encapsulating
 *         the shortest paths and their distances based on the Bellman-Ford algorithm.
 */
fun <T, E> JGraph<T, E>.bellmanFord(epsilon: Double = 1e-9): ShortestPathAlgorithm<T, E> =
    BellmanFordShortestPath(this, epsilon)

/**
 * Executes the Breadth-First Search (BFS) algorithm on the current graph instance.
 *
 * The method returns a BFS-based shortest path algorithm implementation that can be
 * used to compute shortest paths from a given source vertex to other vertices in the graph.
 *
 * @return An instance of `ShortestPathAlgorithm` utilizing the BFS approach for shortest path computation.
 */
fun <T, E> JGraph<T, E>.breadthFirstSearch(): ShortestPathAlgorithm<T, E> =
    BFSShortestPath(this)

/**
 * Computes and returns a K-Disjoint Shortest Path algorithm for the given graph.
 *
 * The Bhandari algorithm generates edge-disjoint or node-disjoint shortest paths
 * between source and target nodes in the graph. It relies on modifying the graph
 * and iteratively finding shortest paths using concepts like reverse edges, cost
 * adjustments, and residual graphs.
 *
 * @return The KShortestPathAlgorithm instance representing the Bhandari algorithm for
 *         finding k-shortest disjoint paths in the graph.
 */
fun <T, E> JGraph<T, E>.bhandari(): KShortestPathAlgorithm<T, E> =
    BhandariKDisjointShortestPaths(this)

/**
 * Performs the bidirectional A* algorithm on a graph to find the shortest path
 * between two vertices. This method utilizes an admissible heuristic to aid in the search.
 *
 * @param T The type of vertices in the graph.
 * @param E The type of edges in the graph.
 * @param heuristic The admissible heuristic function used to estimate the cost
 *                  from a vertex to the target. Must be non-overestimating (admissible).
 * @param heapSupplier A function that supplies a heap implementation used for managing priority queues.
 *                     Defaults to using a pairing heap.
 * @return An implementation of the shortest path algorithm configured for bidirectional A*.
 */
fun <T, E> JGraph<T, E>.bidirectionalAStar(
    heuristic: AStarAdmissibleHeuristic<T>,
    heapSupplier: HeapSupplier<T> = ::PairingHeap
): ShortestPathAlgorithm<T, E> = BidirectionalAStarShortestPath(this, heuristic, heapSupplier)

/**
 * Computes the shortest path in a graph using the Bidirectional Dijkstra's algorithm.
 *
 * @param radius The maximum radius or distance to consider while finding the shortest path.
 * Default is `Double.POSITIVE_INFINITY`, meaning there is no limit.
 * @param heapSupplier A supplier function that provides the heap (priority queue) used in the algorithm.
 * Uses a `PairingHeap` implementation by default.
 * @return An instance of `ShortestPathAlgorithm` that computes the shortest path using Bidirectional Dijkstra's algorithm.
 */
fun <T, E> JGraph<T, E>.bidirectionalDijkstra(
    radius: Double = Double.POSITIVE_INFINITY,
    heapSupplier: HeapSupplier<JPair<T, E>> = ::PairingHeap
): ShortestPathAlgorithm<T, E> = BidirectionalDijkstraShortestPath(this, radius, heapSupplier)

/**
 * Constructs a contraction hierarchy for the given graph and executor.
 *
 * @param T The type representing the vertices in the graph.
 * @param E The type representing the edges in the graph.
 * @param executor The thread pool executor used to perform parallel computations during the construction of the contraction hierarchy.
 * @return An instance of `ManyToManyShortestPathsAlgorithm` configured with the contraction hierarchy of the input graph.
 */
fun <T, E> JGraph<T, E>.contractionHierarchy(executor: ThreadPoolExecutor): ManyToManyShortestPathsAlgorithm<T, E> =
    CHManyToManyShortestPaths(this, executor)

/**
 * Constructs a bidirectional Dijkstra shortest path algorithm using contraction hierarchies for the given graph.
 *
 * @param executor The thread pool executor to be used for parallel processing in the algorithm.
 * @return An instance of the bidirectional Dijkstra shortest path algorithm designed for the given graph.
 */
fun <T, E> JGraph<T, E>.contractionHierarchyBidirectionalDijkstra(executor: ThreadPoolExecutor): ShortestPathAlgorithm<T, E> =
    ContractionHierarchyBidirectionalDijkstra(this, executor)

/**
 * Computes and returns a many-to-many shortest paths algorithm for the graph instance.
 * This method allows the configuration of the underlying shortest path algorithm to be used.
 *
 * @param algorithmSupplier A function that supplies the shortest path algorithm for the graph.
 *                          Defaults to `BidirectionalDijkstraShortestPath`.
 * @return An instance of `ManyToManyShortestPathsAlgorithm` for computing shortest paths
 *         between multiple source and target vertices in the graph.
 */
fun <T, E> JGraph<T, E>.manyToMany(
    algorithmSupplier: (JGraph<T, E>) -> ShortestPathAlgorithm<T, E> = ::BidirectionalDijkstraShortestPath
): ManyToManyShortestPathsAlgorithm<T, E> = DefaultManyToManyShortestPaths(this, algorithmSupplier)

/**
 * Computes the shortest paths in a weighted graph using the Delta-Stepping algorithm.
 *
 * This function utilizes the Delta-Stepping algorithm to find the shortest paths
 * from a source vertex to all other vertices in the graph. It is designed for
 * parallel execution, making use of a provided `ThreadPoolExecutor`.
 *
 * @param executor The `ThreadPoolExecutor` to be used for parallel processing.
 * @param delta A parameter that determines the bucket width for the algorithm. Smaller values increase precision but may decrease performance. Default is 0.0.
 * @param comparator An optional comparator for vertex prioritization. If null, a natural order will be used if applicable.
 * @return A `ShortestPathAlgorithm` instance that represents the computed shortest paths.
 */
fun <T, E> JGraph<T, E>.deltaStepping(
    executor: ThreadPoolExecutor,
    delta: Double = 0.0,
    comparator: Comparator<T>? = null
): ShortestPathAlgorithm<T, E> = DeltaSteppingShortestPath(this, delta, executor, comparator)

/**
 * Finds the shortest paths in the graph using Dijkstra's algorithm.
 *
 * @param radius The maximum cost or distance to explore for finding the shortest path. Default is `Double.POSITIVE_INFINITY`.
 * @param heapSupplier A supplier function for the heap implementation used during the algorithm. Defaults to `::PairingHeap`.
 * @return An instance of `ShortestPathAlgorithm<T, E>` representing the result of Dijkstra's shortest path calculations.
 */
fun <T, E> JGraph<T, E>.dijkstra(
    radius: Double = Double.POSITIVE_INFINITY,
    heapSupplier: HeapSupplier<JPair<T, E>> = ::PairingHeap
): ShortestPathAlgorithm<T, E> = DijkstraShortestPath(this, radius, heapSupplier)

/**
 * Computes many-to-many shortest paths in the graph using Dijkstra's algorithm.
 *
 * This method utilizes the Dijkstra algorithm to calculate the shortest paths between
 * multiple source vertices and multiple target vertices in the graph. It returns an
 * algorithmic object to retrieve the shortest paths between any specified pairs of
 * source and target vertices.
 *
 * @return An instance of ManyToManyShortestPathsAlgorithm that provides functionality to
 *         retrieve shortest paths between selected vertices in the graph.
 */
fun <T, E> JGraph<T, E>.dijkstraManyToMany(): ManyToManyShortestPathsAlgorithm<T, E> =
    DijkstraManyToManyShortestPaths(this)

/**
 * Computes the k-shortest paths in the graph using the Eppstein algorithm.
 *
 * The Eppstein algorithm efficiently finds k-shortest paths in weighted directed graphs
 * by constructing a data structure that represents all shortest paths in the graph.
 *
 * @return An instance of KShortestPathAlgorithm that can be used to retrieve k-shortest paths for the graph.
 */
fun <T, E> JGraph<T, E>.eppstein(): KShortestPathAlgorithm<T, E> =
    EppsteinKShortestPath(this)

/**
 * Computes the shortest paths between two vertices in a graph using Eppstein's algorithm,
 * returning an iterable of paths.
 *
 * @param from The starting vertex for the paths.
 * @param to The destination vertex for the paths.
 * @return An iterable of shortest paths represented as `GraphPath<T, E>` instances.
 */
fun <T, E> JGraph<T, E>.eppstein(from: T, to: T): Iterable<GraphPath<T, E>> =
    Iterable { EppsteinShortestPathIterator(this, from, to) }

/**
 * Computes the shortest paths between all pairs of vertices in the graph using the Floyd-Warshall algorithm.
 *
 * @receiver The graph on which the Floyd-Warshall algorithm will be executed.
 * @return An instance of `FloydWarshallShortestPaths` containing the shortest path information for all pairs of vertices in the graph.
 */
fun <T, E> JGraph<T, E>.floydWarshall(): FloydWarshallShortestPaths<T, E> =
    FloydWarshallShortestPaths(this)

/**
 * Provides a `GraphMeasurer` instance for the graph.
 *
 * This property allows for analyzing and measuring various graph properties,
 * such as diameter, radius, and eccentricity, using the `GraphMeasurer` utility.
 *
 * @receiver The graph on which metrics and measures are to be computed.
 * @return An instance of `GraphMeasurer` initialized with the current graph.
 */
val <T, E> JGraph<T, E>.measurer: GraphMeasurer<T, E>
    get() = GraphMeasurer(this)

/**
 * Constructs and returns a new `GraphMeasurer` for the current graph using the specified shortest path algorithm.
 *
 * @param shortestPathAlgorithm The algorithm to use for calculating shortest paths in the graph.
 * @return A new `GraphMeasurer` instance capable of measuring properties of the graph.
 */
fun <T, E> JGraph<T, E>.measurerWith(
    shortestPathAlgorithm: ShortestPathAlgorithm<T, E>
): GraphMeasurer<T, E> = GraphMeasurer(this, shortestPathAlgorithm)

/**
 * Computes the shortest paths from a source vertex to all other vertices in a graph using
 * Dijkstra's algorithm, optimized for graphs with integer vertices.
 *
 * @param heapSupplier A supplier function that provides a heap implementation for managing
 *        the priority queue used in Dijkstra's algorithm. The default is a 4-ary heap.
 * @return A shortest path algorithm instance initialized with the provided graph and heap supplier.
 */
fun <E> JGraph<Int, E>.intVertexDijkstra(
    heapSupplier: HeapSupplier<Int> = { DaryArrayAddressableHeap(4) }
): ShortestPathAlgorithm<Int, E> = IntVertexDijkstraShortestPath<E>(this, heapSupplier)

/**
 * Finds the shortest path between two vertices in the graph using Dijkstra's algorithm.
 *
 * @param from The starting vertex of the path.
 * @param to The target vertex of the path.
 * @return A `GraphPath` representing the shortest path between the `from` and `to` vertices,
 *         or `null` if no path exists.
 */
fun <E> JGraph<Int, E>.findPathBetween(from: Int, to: Int): GraphPath<Int, E>? =
    IntVertexDijkstraShortestPath.findPathBetween(this, from, to)

/**
 * Computes the shortest paths in the graph using Johnson's algorithm.
 *
 * Johnson's algorithm is used to find the shortest paths between all pairs of vertices
 * in a weighted graph. It is particularly useful for graphs with negative weight edges,
 * as long as there are no negative weight cycles.
 *
 * @return The shortest path algorithm result containing shortest path information for every pair of vertices.
 */
fun <T, E> JGraph<T, E>.johnson(): ShortestPathAlgorithm<T, E> =
    JohnsonShortestPaths(this)

/**
 * Computes the multi-objective shortest path algorithm for the given graph.
 *
 * @param edgeWeightFunction A function that provides an array of weights for a given edge.
 *                           This function is used to evaluate the weights of edges in the graph
 *                           for multi-objective path computation.
 * @return An instance of `MultiObjectiveShortestPathAlgorithm` initialized with the current graph
 *         and the specified edge weight function.
 */
fun <T, E> JGraph<T, E>.martin(
    edgeWeightFunction: (E) -> DoubleArray
): MultiObjectiveShortestPathAlgorithm<T, E> =
    MartinShortestPath(this, edgeWeightFunction)

/**
 * Computes the K-disjoint shortest paths in the graph using Suurballe's algorithm.
 *
 * Suurballe's algorithm is used to find two or more disjoint shortest paths
 * in a weighted graph. The paths are disjoint in terms of their edges, ensuring
 * they do not share any common edges. This method can be used for tasks such
 * as network resiliency and routing optimization.
 *
 * @return An instance of `KShortestPathAlgorithm` that computes the K-disjoint
 * shortest paths based on Suurballe's method.
 */
fun <T, E> JGraph<T, E>.suurballe(): KShortestPathAlgorithm<T, E> =
    SuurballeKDisjointShortestPaths(this)

/**
 * Applies the Transit Node Routing shortest path algorithm on this graph.
 *
 * This method creates an instance of the TransitNodeRoutingShortestPath algorithm,
 * which is optimized for finding shortest paths in large graphs using transit nodes.
 *
 * @param executor The thread pool executor used to manage concurrent tasks during the computation.
 * @return A ShortestPathAlgorithm instance configured for this graph and the provided executor.
 */
fun <T, E> JGraph<T, E>.transitNodeRouting(executor: ThreadPoolExecutor): ShortestPathAlgorithm<T, E> =
    TransitNodeRoutingShortestPath(this, executor)

/**
 * Finds and returns the center of the graph.
 *
 * The graph center is defined as the set of vertices that minimizes the
 * maximum distance to all other vertices in the graph. In other words,
 * it is the set of vertices with the smallest eccentricity in the graph.
 *
 * @return A set of vertices that forms the center of the graph.
 */
fun <T, E> JGraph<T, E>.findGraphCenter(): Set<T> =
    TreeMeasurer(this).graphCenter

/**
 * Finds the k-shortest paths in the graph using Yen's algorithm.
 *
 * @param validator An optional path validator to check the validity of candidate paths.
 * @return An implementation of the `KShortestPathAlgorithm` for the current graph.
 */
fun <T, E> JGraph<T, E>.yen(
    validator: PathValidator<T, E>? = null,
): KShortestPathAlgorithm<T, E> = YenKShortestPath(this, validator)

/**
 * Finds the k shortest paths from one vertex to another in a graph using Yen's algorithm.
 *
 * @param from The starting vertex of the paths.
 * @param to The destination vertex of the paths.
 * @param validator An optional path validator that can filter or validate paths.
 * @param heapSupplier A function to supply a heap implementation for managing paths during the algorithm execution.
 * @return An iterable collection of the k shortest paths, represented as graph paths.
 */
fun <T, E> JGraph<T, E>.yen(
    from: T,
    to: T,
    validator: PathValidator<T, E>? = null,
    heapSupplier: HeapSupplier<JPair<GraphPath<T, E>, Boolean>> = ::PairingHeap
): Iterable<GraphPath<T, E>> = Iterable { YenShortestPathIterator(this, from, to, heapSupplier, validator) }
