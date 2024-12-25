@file:Suppress("UNCHECKED_CAST", "unused")

package sschr15.aocsolutions.util.graphwrap

import org.jgrapht.GraphPath
import org.jgrapht.alg.clique.BronKerboschCliqueFinder
import org.jgrapht.alg.clustering.GirvanNewmanClustering
import org.jgrapht.alg.clustering.KSpanningTreeClustering
import org.jgrapht.alg.clustering.LabelPropagationClustering
import org.jgrapht.alg.clustering.UndirectedModularityMeasurer
import org.jgrapht.alg.color.BrownBacktrackColoring
import org.jgrapht.alg.color.ColorRefinementAlgorithm
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector
import org.jgrapht.alg.cycle.*
import org.jgrapht.alg.decomposition.DulmageMendelsohnDecomposition
import org.jgrapht.alg.decomposition.HeavyPathDecomposition
import org.jgrapht.alg.densesubgraph.GoldbergMaximumDensitySubgraphAlgorithm
import org.jgrapht.alg.flow.PushRelabelMFImpl
import org.jgrapht.alg.independentset.ChordalGraphIndependentSetFinder
import org.jgrapht.alg.interfaces.*
import org.jgrapht.alg.lca.EulerTourRMQLCAFinder
import org.jgrapht.alg.linkprediction.CommonNeighborsLinkPrediction
import org.jgrapht.alg.scoring.EdgeBetweennessCentrality
import org.jgrapht.alg.similarity.ZhangShashaTreeEditDistance
import org.jgrapht.alg.spanning.BoruvkaMinimumSpanningTree
import org.jgrapht.alg.tour.NearestNeighborHeuristicTSP
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.GraphWalk
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.reflect.KClass
import org.jgrapht.Graph as JGraph
import org.jgrapht.alg.util.Pair as JPair

/**
 * Identifies all maximal cliques within the graph.
 *
 * A maximal clique is a subset of vertices wherein every pair of vertices is connected,
 * and no additional vertices can be added to the subset without losing this property.
 *
 * @return An iterable collection of sets, where each set represents a maximal clique in the graph.
 */
fun <T, E> JGraph<T, E>.maximalCliques(): Iterable<Set<T>> = BronKerboschCliqueFinder(this)

/**
 * Identifies and retrieves all maximal cliques in the graph using the specified
 * maximal clique enumeration algorithm.
 *
 * @param finder The class of the algorithm to be used for finding maximal cliques.
 *               Defaults to `BronKerboschCliqueFinder::class`.
 * @return An iterable collection of sets where each set represents a maximal clique
 *         in the graph.
 */
fun <T, E> JGraph<T, E>.maximalCliques(
    finder: KClass<out MaximalCliqueEnumerationAlgorithm<*, *>> = BronKerboschCliqueFinder::class
): Iterable<Set<T>> {
    val algo = finder.java.getConstructor(Graph::class.java).newInstance(this)
    return algo as MaximalCliqueEnumerationAlgorithm<T, E>
}

/**
 * Performs clustering on the graph using the Girvan-Newman Clustering algorithm.
 *
 * @param k The desired number of clusters to form.
 * @param ignoreOverflow A flag indicating whether to ignore edge betweenness overflow during computation.
 *                       If set to false, an exception will be thrown on overflow.
 * @param startVertices A set of vertices to treat as the starting points for the clustering algorithm.
 *                      If null, the algorithm will choose starting points automatically.
 * @return An instance of `ClusteringAlgorithm<T>` representing the result of the clustering.
 */
fun <T, E> JGraph<T, E>.cluster(
    k: Int,
    ignoreOverflow: Boolean = false,
    startVertices: Set<T>? = null,
): ClusteringAlgorithm<T> =
    GirvanNewmanClustering(this, k, if (ignoreOverflow) EdgeBetweennessCentrality.OverflowStrategy.IGNORE_OVERFLOW else EdgeBetweennessCentrality.OverflowStrategy.THROW_EXCEPTION_ON_OVERFLOW, startVertices)

/**
 * Performs clustering on the graph using the K-spanning tree clustering algorithm.
 *
 * @param k The number of clusters to create, represented as an integer.
 * @return An instance of `ClusteringAlgorithm<T>` that represents the resulting clusters of the graph.
 */
fun <T, E> JGraph<T, E>.clusterWithSpanningTree(k: Int): ClusteringAlgorithm<T> =
    KSpanningTreeClustering(this, k)

/**
 * Applies the Label Propagation Clustering algorithm to a graph and returns the resulting clusters.
 *
 * This method partitions the vertices of the graph into clusters by utilizing the label propagation
 * algorithm. The algorithm iteratively updates vertex labels based on the labels of their neighboring
 * vertices until a stable clustering is achieved or the maximum number of iterations is reached. An
 * optional random generator can be provided for randomization in the clustering process.
 *
 * @param maxIterations The maximum number of iterations to perform. If set to 0, the algorithm
 * will run until convergence without an iteration limit.
 * @param random An optional `Random` instance for introducing randomness in tie-breaking during
 * the clustering process. If `null`, a default non-randomized approach will be used.
 * @return A `ClusteringAlgorithm` instance that contains the resulting clusters of vertices.
 */
fun <T, E> JGraph<T, E>.clusterWithLabelPropagation(
    maxIterations: Int = 0,
    random: Random? = null,
): ClusteringAlgorithm<T> {
    if (random == null) return LabelPropagationClustering(this, maxIterations)
    return LabelPropagationClustering(this, maxIterations, random.asJavaRandom())
}

/**
 * Performs clustering on a graph using the Label Propagation Algorithm (LPA).
 * This algorithm assigns labels to vertices and propagates them throughout the graph,
 * grouping vertices into clusters by converging on shared labels.
 *
 * @param maxIterations The maximum number of iterations to perform. If set to `0`, the algorithm runs until convergence. Default is `0`.
 * @param random An optional random number generator to introduce stochastic behavior in the propagation process. If `null`, a default configuration is used.
 * @return An implementation of `ClusteringAlgorithm` representing the clusters formed in the graph.
 */
fun <T, E> JGraph<T, E>.clusterWithLabelPropagation(
    maxIterations: Int = 0,
    random: java.util.Random? = null,
): ClusteringAlgorithm<T> {
    if (random == null) return LabelPropagationClustering(this, maxIterations)
    return LabelPropagationClustering(this, maxIterations, random)
}

/**
 * Creates a modularity measurer for the current undirected graph.
 *
 * @return An instance of `UndirectedModularityMeasurer` pre-configured with the current graph.
 */
fun <T, E> JGraph<T, E>.createModularityMeasurer(): UndirectedModularityMeasurer<T, E> = UndirectedModularityMeasurer(this)

/**
 * Assigns colors to the vertices of the graph such that no two adjacent vertices share the same color.
 *
 * This method applies the Brown backtracking coloring algorithm to determine a valid coloring
 * of the graph's vertices with a minimal number of distinct colors, adhering to graph coloring constraints.
 *
 * @return An object containing the vertex-to-color mapping and the number of colors used for the graph coloring.
 */
fun <T, E> JGraph<T, E>.coloring(): VertexColoringAlgorithm.Coloring<T> =
    BrownBacktrackColoring(this).coloring

/**
 * Applies a vertex coloring algorithm to the graph and returns the coloring result.
 *
 * @param algorithm The class of the vertex coloring algorithm to use. Defaults to `BrownBacktrackColoring`.
 * @return The result of the vertex coloring, represented as a `VertexColoringAlgorithm.Coloring` object.
 */
fun <T, E> JGraph<T, E>.coloring(
    algorithm: KClass<out VertexColoringAlgorithm<*>> = BrownBacktrackColoring::class
): VertexColoringAlgorithm.Coloring<T> {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    return algo.coloring as VertexColoringAlgorithm.Coloring<T>
}

/**
 * Performs a color refinement algorithm on the graph to compute a vertex coloring.
 * This method refines vertex colors iteratively until the color distribution stabilizes.
 * If an initial coloring is provided, it will be used as the starting point for refinement.
 * Otherwise, a default initial coloring will be generated.
 *
 * @param alpha An optional initial vertex coloring to start the refinement process.
 *              If null, a default initial coloring will be used.
 * @return A refined vertex coloring of the graph.
 */
fun <T, E> JGraph<T, E>.colorRefinement(
    alpha: VertexColoringAlgorithm.Coloring<T>? = null,
): VertexColoringAlgorithm.Coloring<T> = if (alpha == null) ColorRefinementAlgorithm(this).coloring else ColorRefinementAlgorithm(this, alpha).coloring

/**
 * Computes the connectivity information of the graph using a ConnectivityInspector.
 *
 * @return A ConnectivityInspector instance for analyzing the connectivity of the graph.
 */
fun <T, E> JGraph<T, E>.connectivity(): ConnectivityInspector<T, E> = ConnectivityInspector(this)

/**
 * Computes the strong connectivity of a directed graph.
 *
 * The method utilizes the KosarajuStrongConnectivityInspector to determine the strongly connected
 * components of the graph by identifying subgraphs where every vertex is reachable from
 * every other vertex within the same subgraph.
 *
 * @return An instance of StrongConnectivityAlgorithm, which provides access to the strongly
 * connected components of the graph.
 */
fun <T, E> JGraph<T, E>.strongConnectivity(): StrongConnectivityAlgorithm<T, E> = KosarajuStrongConnectivityInspector(this)

/**
 * Identifies the strongly connected components of the graph using the specified algorithm.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param algorithm The algorithm class to be used for strong connectivity inspection.
 *                  Defaults to `KosarajuStrongConnectivityInspector`.
 * @return An instance of `StrongConnectivityAlgorithm` that can be used to retrieve
 *         strongly connected components of the graph.
 */
fun <T, E> JGraph<T, E>.strongConnectivity(
    algorithm: KClass<out StrongConnectivityAlgorithm<*, *>> = KosarajuStrongConnectivityInspector::class
): StrongConnectivityAlgorithm<T, E> {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    return algo as StrongConnectivityAlgorithm<T, E>
}

/**
 * Finds a local augmentation cycle within the graph using the specified parameters.
 *
 * @param lengthBound The maximum length of the cycle to be considered.
 * @param labelMap A mutable map used for labeling vertices during the augmentation process. Defaults to an empty map.
 * @param returnBestInsteadOfFirst A flag indicating whether to return the best cycle found instead of the first one that meets the criteria. Defaults to `false`.
 * @return A `GraphWalk` instance representing the local augmentation cycle found.
 */
fun <T, E> JGraph<T, E>.calculateLocalAugmentationCycle(
    lengthBound: Int,
    labelMap: MutableMap<T, Int> = mutableMapOf(),
    returnBestInsteadOfFirst: Boolean = false,
): GraphWalk<T, E> = AhujaOrlinSharmaCyclicExchangeLocalAugmentation(this, lengthBound, labelMap, returnBestInsteadOfFirst).localAugmentationCycle

/**
 * Determines whether the current graph satisfies the Berge property.
 *
 * The Berge property is a characteristic of certain graphs used in graph theory,
 * particularly in the study of perfect graphs. This method checks if the current
 * graph, when treated as undirected, adheres to this property.
 *
 * @return `true` if the graph satisfies the Berge property, `false` otherwise.
 */
fun <T, E> JGraph<T, E>.calculateBergeRecognition(): Boolean =
    BergeGraphInspector<T, E>().isBerge(this, false)

/**
 * Identifies and retrieves a "Berge hole" in the graph, if the graph satisfies Berge's properties.
 *
 * @return A `GraphPath` representing the identified Berge hole if the graph satisfies Berge's properties; null otherwise.
 */
fun <T, E> JGraph<T, E>.getBergeHole(): GraphPath<T, E>? {
    val inspector = BergeGraphInspector<T, E>()
    if (!(inspector.isBerge(this, true))) return null
    return inspector.certificate
}

/**
 * Solves the Chinese Postman Problem (CPP) for the current graph.
 *
 * The method computes an optimal path that visits every edge of the graph at least once,
 * possibly including repeated edges, aiming to minimize the traversal cost.
 * The graph is treated as the context for the CPP solution.
 *
 * @return A `GraphPath` representing the optimal traversal path for the Chinese Postman Problem.
 */
fun <T, E> JGraph<T, E>.solveCPP(): GraphPath<T, E> = ChinesePostman<T, E>().getCPPSolution(this)

/**
 * Computes the minimal vertex separators of the current graph along with their multiplicities.
 * A minimal separator is a minimal set of vertices that disconnect the graph when removed,
 * effectively partitioning it into two or more disjoint subgraphs.
 *
 * @return A map where keys are sets of vertices representing the minimal separators
 *         and values are integers representing their multiplicities.
 */
fun <T, E> JGraph<T, E>.getMinimalSeparators(): Map<Set<T>, Int> =
    ChordalGraphMinimalVertexSeparatorFinder(this).minimalSeparatorsWithMultiplicities

/**
 * Inspects the chordality of the current graph instance.
 *
 * This method analyzes the graph to determine its chordality properties,
 * such as whether the graph is chordal (perfect elimination ordering exists)
 * or contains specific chordless substructures.
 *
 * @return A `ChordalityInspector` instance containing information about the chordal properties of the graph.
 */
fun <T, E> JGraph<T, E>.chordalityInspector(): ChordalityInspector<T, E> = ChordalityInspector(this)

/**
 * Inspects the weak chordality of the graph, providing an analysis tool for determining
 * whether the graph contains any weakly chordal substructures. Weak chordality is a property
 * of a graph where every induced cycle of the graph with at least five vertices has a chord.
 *
 * @return An instance of WeakChordalityInspector configured for the graph.
 */
fun <T, E> JGraph<T, E>.weakChordalityInspector(): WeakChordalityInspector<T, E> = WeakChordalityInspector(this)

/**
 * Detects cycles in the graph and provides tools to analyze them.
 *
 * @return A `CycleDetector` instance initialized with the current graph,
 *         capable of detecting cycles and analyzing their properties.
 */
fun <T, E> JGraph<T, E>.cycleDetector(): CycleDetector<T, E> = CycleDetector(this)

/**
 * Converts a simple cycle, represented as a list of edges, into a graph path in the context of the current graph.
 *
 * @param cycle The list of edges forming the simple cycle.
 * @return A `GraphPath` representing the same path as the provided simple cycle.
 */
fun <T, E> JGraph<T, E>.simpleCycleToGraphPath(cycle: List<E>): GraphPath<T, E> =
    Cycles.simpleCycleToGraphPath(this, cycle)

/**
 * Finds all directed simple cycles in the graph and applies a specified action to each cycle.
 * A simple cycle is a cycle that does not repeat vertices except for the starting and ending vertex.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param action A function that will be executed for each found directed simple cycle.
 *               The function receives a list of vertices representing the cycle in the order they appear.
 */
fun <T, E> JGraph<T, E>.forEachDirectedSimpleCycle(action: (List<T>) -> Unit) =
    HawickJamesSimpleCycles(this).findSimpleCycles(action)

/**
 * Iterates over each directed simple cycle in the given graph and applies the provided action to it.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param algorithm The algorithm class used to find directed simple cycles. Defaults to `HawickJamesSimpleCycles`.
 * @param action The action to apply to each directed simple cycle found. The cycle is represented as a list of vertices.
 */
fun <T, E> JGraph<T, E>.forEachDirectedSimpleCycle(
    algorithm: KClass<out DirectedSimpleCycles<*, *>> = HawickJamesSimpleCycles::class,
    action: (List<T>) -> Unit
) {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    algo as DirectedSimpleCycles<T, E>
    algo.findSimpleCycles(action)
}

/**
 * Finds all directed simple cycles in the graph.
 *
 * A directed simple cycle is a cycle in the graph where each vertex appears only once
 * (except for the starting and ending vertex, which is the same) and all edges respect the
 * directionality of the graph.
 *
 * @return A list of directed simple cycles, where each cycle is represented as a list of vertices.
 */
fun <T, E> JGraph<T, E>.findDirectedSimpleCycles(): List<List<T>> = HawickJamesSimpleCycles(this).findSimpleCycles()

/**
 * Finds all directed simple cycles in a graph using the specified algorithm.
 *
 * A directed simple cycle is a closed trajectory in the graph such that each vertex is visited at most once,
 * except that the starting and ending vertices are the same.
 *
 * @param T The type representing the vertices of the graph.
 * @param E The type representing the edges of the graph.
 * @param algorithm The algorithm class used to compute the directed simple cycles. It defaults to `HawickJamesSimpleCycles`.
 * @return A list of directed simple cycles in the graph, where each cycle is represented as a list of vertices.
 */
fun <T, E> JGraph<T, E>.findDirectedSimpleCycles(
    algorithm: KClass<out DirectedSimpleCycles<*, *>> = HawickJamesSimpleCycles::class
): List<List<T>> {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    algo as DirectedSimpleCycles<T, E>
    return algo.findSimpleCycles()
}

/**
 * Finds an Eulerian cycle in the current graph, if it exists.
 *
 * An Eulerian cycle is a closed path in a graph where every edge is visited exactly once.
 * For an Eulerian cycle to exist, the graph must be connected (if undirected) or strongly
 * connected (if directed), and all vertices must have an even degree (for an undirected graph)
 * or equal in-degree and out-degree (for a directed graph).
 *
 * @return The Eulerian cycle as a `GraphPath` if the graph is Eulerian, otherwise `null`.
 */
fun <T, E> JGraph<T, E>.findEulerianCycle(): GraphPath<T, E>? =
    HierholzerEulerianCycle<T, E>().takeIf { it.isEulerian(this) }?.getEulerianCycle(this)

/**
 * Finds the minimum mean cycle in a directed graph using Howard's algorithm.
 *
 * @param maximumIterations The maximum number of iterations to allow during the computation (default is Int.MAX_VALUE).
 * @param strongConnectivityAlgorithm The algorithm used to find strongly connected components of the graph (default is GabowStrongConnectivityInspector).
 * @param toleranceEpsilon A small positive value used as a tolerance threshold in floating-point calculations (default is 1e-9).
 * @return A pair containing the minimum mean cycle as a GraphPath<T, E> and its mean weight as a Double, or null if no cycle exists.
 */
fun <T, E> JGraph<T, E>.findMinimumMeanCycle(
    maximumIterations: Int = Int.MAX_VALUE,
    strongConnectivityAlgorithm: StrongConnectivityAlgorithm<T, E> = GabowStrongConnectivityInspector(this),
    toleranceEpsilon: Double = 1e-9,
): Pair<GraphPath<T, E>, Double>? {
    val algo = HowardMinimumMeanCycle(this)
    val cycle = algo.cycle
    if (cycle == null) return null
    return cycle to cycle.weight / cycle.length
}

/**
 * Computes the cycle basis of the graph using the Paton algorithm and returns it as a CycleBasis object.
 *
 * @return A CycleBasis object containing the cycle basis of the graph, where each cycle is represented
 *         as a collection of edges.
 */
fun <T, E> JGraph<T, E>.findCycleBasis(): CycleBasisAlgorithm.CycleBasis<T, E> =
    PatonCycleBase(this).cycleBasis

/**
 * Computes the cycle basis of the graph using the specified cycle basis algorithm.
 *
 * @param algorithm The class of the algorithm to use for computing the cycle basis. By default,
 * PatonCycleBase is used.
 * @return The cycle basis of the graph as computed by the specified algorithm.
 */
fun <T, E> JGraph<T, E>.findCycleBasis(
    algorithm: KClass<out CycleBasisAlgorithm<*, *>> = PatonCycleBase::class
): CycleBasisAlgorithm.CycleBasis<T, E> {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    algo as CycleBasisAlgorithm<T, E>
    return algo.cycleBasis
}

/**
 * Computes the Dulmage-Mendelsohn decomposition of a bipartite graph.
 *
 * The graph is required to be bipartite, with the two partitions specified by the arguments `partition1` and `partition2`.
 * The decomposition identifies different regions in the graph, such as matched, unmatched, and over-constrained regions.
 * It is useful in various applications such as structural analysis and matching problems.
 *
 * @param partition1 The set of vertices in the first partition of the bipartite graph.
 * @param partition2 The set of vertices in the second partition of the bipartite graph.
 * @param fine A boolean specifying whether a finer decomposition granularity should be computed.
 * @return A `DulmageMendelsohnDecomposition.Decomposition` object containing the decomposition results for the graph.
 */
fun <T, E> JGraph<T, E>.dulmageMendelsohnDecomposition(
    partition1: Set<T>,
    partition2: Set<T>,
    fine: Boolean
): DulmageMendelsohnDecomposition.Decomposition<T, E> =
    DulmageMendelsohnDecomposition(this, partition1, partition2).getDecomposition(fine)

/**
 * Performs the Dulmage-Mendelsohn decomposition on the current graph.
 *
 * This method partitions the graph into distinct components based on the
 * specified partitioning conditions and optionally refines the decomposition.
 * The Dulmage-Mendelsohn decomposition is often used in bipartite matching
 * and combinatorial optimization to analyze graph structure.
 *
 * @param fine If true, refines the decomposition for more detailed results.
 * @param partitionBlock A function to determine the partition of each vertex.
 *     It receives a vertex as input and returns true if the vertex belongs
 *     to the first partition, otherwise false.
 * @return A `DulmageMendelsohnDecomposition.Decomposition` instance representing
 *     the result of the decomposition, including the partitioned sets and other details.
 */
inline fun <T, E> JGraph<T, E>.dulmageMendelsohnDecomposition(
    fine: Boolean,
    partitionBlock: (T) -> Boolean
): DulmageMendelsohnDecomposition.Decomposition<T, E> {
    val sets = vertexSet().partition { partitionBlock(it) }
    return DulmageMendelsohnDecomposition(this, sets.first.toSet(), sets.second.toSet()).getDecomposition(fine)
}

/**
 * Computes the Dulmage-Mendelsohn decomposition of the bipartite graph.
 *
 * This method partitions the vertices of the graph into distinct structural components
 * based on the given sets of partitions and matching algorithm. The Dulmage-Mendelsohn
 * decomposition is particularly useful in analyzing the structural properties of bipartite
 * graphs and identifying connected components, unmatched vertices, and more.
 *
 * @param partition1 The first partition of the bipartite graph's vertices.
 * @param partition2 The second partition of the bipartite graph's vertices.
 * @param matchingAlgorithm The algorithm used for computing a maximum matching on the graph.
 * @param fine Flag indicating whether to compute a finer decomposition or not.
 * @return A decomposition of the bipartite graph into structural components.
 */
fun <T, E> JGraph<T, E>.dulmageMendelsohnDecomposition(
    partition1: Set<T>,
    partition2: Set<T>,
    matchingAlgorithm: MatchingAlgorithm<T, E>,
    fine: Boolean,
): DulmageMendelsohnDecomposition.Decomposition<T, E> =
    DulmageMendelsohnDecomposition(this, partition1, partition2).decompose(matchingAlgorithm.matching, fine)

/**
 * Computes the Dulmage-Mendelsohn Decomposition of a bipartite graph.
 *
 * This method partitions the vertices of the graph into subsets based on the matching provided by the specified
 * matching algorithm. It identifies the decomposition structure of the graph into tight, loose, and unmatched
 * components. The decomposition can be refined based on the `fine` parameter and uses the `partitionBlock`
 * function to partition the vertices into two sets.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param matchingAlgorithm The algorithm used to compute the matching for the graph.
 * @param fine A boolean flag indicating whether to perform a finer decomposition.
 * @param partitionBlock A lambda function that determines the initial partitioning of the vertices.
 *                      Returns `true` for vertices in the first partition and `false` for vertices in the second.
 * @return A `DulmageMendelsohnDecomposition.Decomposition` object that represents the decomposition of the graph.
 */
inline fun <T, E> JGraph<T, E>.dulmageMendelsohnDecomposition(
    matchingAlgorithm: MatchingAlgorithm<T, E>,
    fine: Boolean,
    partitionBlock: (T) -> Boolean,
): DulmageMendelsohnDecomposition.Decomposition<T, E> {
    val sets = vertexSet().partition { partitionBlock(it) }
    return DulmageMendelsohnDecomposition(this, sets.first.toSet(), sets.second.toSet()).decompose(matchingAlgorithm.matching, fine)
}

/**
 * Performs a heavy path decomposition on a graph starting from the specified root.
 * Heavy path decomposition is a technique used in graph theory to decompose a tree
 * into heavy and light paths for efficient query and update operations in algorithms
 * like range queries on trees.
 *
 * @param root The root vertex of the graph from which the decomposition begins.
 * @return An instance of `HeavyPathDecomposition<T, E>` representing the decomposition
 *         structure of the graph.
 */
fun <T, E> JGraph<T, E>.heavyPathDecomposition(root: T): HeavyPathDecomposition<T, E> = HeavyPathDecomposition(this, root)

/**
 * Performs a heavy path decomposition on the graph starting from the specified root vertices.
 * Heavy path decomposition is a technique used to decompose a tree or graph into paths to enable
 * efficient range queries and updates in various graph-related algorithms.
 *
 * @param roots The set of root vertices of the graph from which the decomposition begins.
 * @return The result of the heavy path decomposition, represented as an instance of `HeavyPathDecomposition`.
 */
fun <T, E> JGraph<T, E>.heavyPathDecomposition(roots: Set<T>): HeavyPathDecomposition<T, E> = HeavyPathDecomposition(this, roots)

/**
 * Performs heavy-path decomposition on the given graph, starting from the specified root vertices.
 *
 * Heavy-path decomposition is a technique used to divide a tree or graph into heavy and light edges
 * to optimize certain operations, such as finding paths or performing dynamic programming on trees.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param roots The root vertices from which the heavy-path decomposition starts.
 *              If no roots are specified, the decomposition starts from all vertices considered as potential roots.
 * @return A `HeavyPathDecomposition` object representing the decomposition of the graph.
 */
fun <T, E> JGraph<T, E>.heavyPathDecomposition(vararg roots: T): HeavyPathDecomposition<T, E> = HeavyPathDecomposition(this, roots.toSet())

/**
 * Computes the maximum density subgraph of the current graph using the specified parameters.
 *
 * @param start The starting vertex for identifying the maximum density subgraph.
 * @param end The ending vertex for identifying the maximum density subgraph.
 * @param epsilon Used to determine the precision of the density computation.
 * @param minStCutAlgorithmCtor A function to create the minimum s-t cut algorithm instance.
 *                              Defaults to PushRelabelMFImpl.
 * @return An instance of `MaximumDensitySubgraphAlgorithm` representing the maximum density subgraph of the graph.
 */
fun <T, E> JGraph<T, E>.maximumDensitySubgraph(
    start: T,
    end: T,
    epsilon: Double,
    minStCutAlgorithmCtor: (JGraph<T, DefaultWeightedEdge>) -> MinimumSTCutAlgorithm<T, DefaultWeightedEdge> = ::PushRelabelMFImpl
): MaximumDensitySubgraphAlgorithm<T, E> =
    GoldbergMaximumDensitySubgraphAlgorithm(this, start, end, epsilon, minStCutAlgorithmCtor)

/**
 * Finds a maximum independent set in a chordal graph using the specified iteration order.
 * This function assumes the graph is chordal. An independent set is a set of vertices in a graph
 * such that no two vertices in the set are adjacent.
 *
 * @param iterationOrder The iteration order to use for chordal graph processing. The default is
 * `ChordalityInspector.IterationOrder.MCS`.
 * @return The maximum independent set found in the chordal graph.
 */
fun <T, E> JGraph<T, E>.findChordalIndependentSet(
    iterationOrder: ChordalityInspector.IterationOrder = ChordalityInspector.IterationOrder.MCS
): IndependentSetAlgorithm.IndependentSet<T> =
    ChordalGraphIndependentSetFinder(this, iterationOrder).independentSet

/**
 * Computes the lowest common ancestor in a graph using an efficient algorithm.
 *
 * @param T The type of the vertices in the graph.
 * @param E The type of the edges in the graph.
 * @param root The root vertex of the graph from which the algorithm will operate.
 * @return An instance of a lowest common ancestor finder for the given graph and root.
 */
fun <T, E> JGraph<T, E>.lowestCommonAncestorAlgorithm(root: T): LowestCommonAncestorAlgorithm<T> =
    EulerTourRMQLCAFinder(this, root)

/**
 * Computes the lowest common ancestor (LCA) for a given set of root vertices in a graph.
 * This method uses an Euler Tour technique with a Range Minimum Query (RMQ) implementation
 * to efficiently determine LCAs in the graph.
 *
 * @param roots A set of root vertices in the graph for which the LCA will be computed.
 * @return An implementation of the `LowestCommonAncestorAlgorithm` for the provided graph and roots.
 */
fun <T, E> JGraph<T, E>.lowestCommonAncestorAlgorithm(roots: Set<T>): LowestCommonAncestorAlgorithm<T> =
    EulerTourRMQLCAFinder(this, roots)

/**
 * Predicts the likelihood of a link existing between two vertices in a graph
 * based on the common neighbors link prediction algorithm.
 *
 * @param first The first vertex.
 * @param second The second vertex.
 * @return A double value representing the predicted likelihood of a link between the two vertices.
 */
fun <T, E> JGraph<T, E>.predictLink(
    first: T,
    second: T
): Double = CommonNeighborsLinkPrediction(this).predict(first, second)

/**
 * Predicts the likelihood of a link (edge) existing or being formed between two vertices in a graph
 * using a specified link prediction algorithm.
 *
 * @param first The first vertex in the graph.
 * @param second The second vertex in the graph.
 * @param algorithm The link prediction algorithm to use for prediction. Defaults to `CommonNeighborsLinkPrediction`.
 * @return A `Double` representing the predicted likelihood of a link between the two vertices.
 */
fun <T, E> JGraph<T, E>.predictLink(
    first: T,
    second: T,
    algorithm: KClass<out LinkPredictionAlgorithm<*, *>> = CommonNeighborsLinkPrediction::class
): Double {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    algo as LinkPredictionAlgorithm<T, E>
    return algo.predict(first, second)
}

/**
 * Predicts the likelihood of potential links between pairs of vertices in the graph.
 *
 * The method utilizes the Common Neighbors Link Prediction algorithm to estimate
 * the likelihood of connections between given vertex pairs. Each predicted result
 * is represented as a `Triple` containing the source vertex, target vertex,
 * and the computed prediction score.
 *
 * @param T the type of the vertices in the graph
 * @param E the type of the edges in the graph
 * @param pairs a variable number of pairs, where each pair consists of two vertices
 * representing a potential connection to be predicted
 * @return a list of triples where each triple contains two vertices and a prediction score
 * representing the likelihood of a connection between those vertices
 */
fun <T, E> JGraph<T, E>.predictLinks(
    vararg pairs: Pair<T, T>
): List<Triple<T, T, Double>> = CommonNeighborsLinkPrediction(this)
    .predict(pairs.map { (a, b) -> JPair(a, b) })
    .map { Triple(it.first, it.second, it.third) }

/**
 * Predicts the likelihood of links (connections) between pairs of vertices in a graph
 * using a specified link prediction algorithm.
 *
 * @param T The type of vertices in the graph.
 * @param E The type of edges in the graph.
 * @param pairs A variable number of vertex pairs for which link predictions will be made.
 * @param algorithm The link prediction algorithm to be used. Defaults to `CommonNeighborsLinkPrediction`.
 * @return A list of triples where each triple contains two vertices (representing a pair)
 *         and a double value representing the predicted likelihood for a link between them.
 */
fun <T, E> JGraph<T, E>.predictLinks(
    vararg pairs: Pair<T, T>,
    algorithm: KClass<out LinkPredictionAlgorithm<*, *>> = CommonNeighborsLinkPrediction::class
): List<Triple<T, T, Double>> {
    val algo = algorithm.java.getConstructor(Graph::class.java).newInstance(this)
    algo as LinkPredictionAlgorithm<T, E>
    return algo
        .predict(pairs.map { (a, b) -> JPair(a, b) })
        .map { Triple(it.first, it.second, it.third) }
}

/**
 * Computes the tree edit distance between two rooted trees represented as graphs, and returns
 * both the calculated edit distance and the list of edit operations required to transform one tree into the other.
 *
 * @param myRoot The root vertex of the tree in the current graph from which the computation starts.
 * @param other The other graph representing a tree to compare with.
 * @param otherRoot The root vertex of the tree in the `other` graph.
 * @param insertionCost A function returning the cost of inserting a vertex into the tree, defaults to 1.0 for all vertices.
 * @param removalCost A function returning the cost of removing a vertex from the tree, defaults to 1.0 for all vertices.
 * @param changeCost A function returning the cost of changing one vertex into another, defaults to 1.0.
 * @return A pair where the first element is the calculated tree edit distance (as a `Double`) and the
 *         second element is a list of edit operations required to transform one tree into the other.
 */
fun <T, E> JGraph<T, E>.calculateTreeEditDistance(
    myRoot: T,
    other: JGraph<T, E>,
    otherRoot: T,
    insertionCost: (T) -> Double = { 1.0 },
    removalCost: (T) -> Double = { 1.0 },
    changeCost: (T, T) -> Double = { _, _, -> 1.0 }
): Pair<Double, List<ZhangShashaTreeEditDistance.EditOperation<T>>> {
    val algo = ZhangShashaTreeEditDistance(this, myRoot, other, otherRoot, insertionCost, removalCost, changeCost)
    return algo.distance to algo.editOperationLists
}

/**
 * Computes the Minimum Spanning Tree (MST) of a graph using Borůvka's algorithm.
 *
 * @return The computed Minimum Spanning Tree represented as a result of type `SpanningTreeAlgorithm.SpanningTree<E>`.
 */
fun <T, E> JGraph<T, E>.findMinimumSpanningTree(): SpanningTreeAlgorithm.SpanningTree<E> =
    BoruvkaMinimumSpanningTree(this).spanningTree

/**
 * Finds and returns an approximation of the Traveling Salesman Problem (TSP) solution
 * for the current graph using the nearest neighbor heuristic.
 *
 * @return A `GraphPath` representing an approximate TSP tour through the graph.
 */
fun <T, E> JGraph<T, E>.findTravellingSalesman(): GraphPath<T, E> =
    NearestNeighborHeuristicTSP<T, E>().getTour(this)

/**
 * Finds the optimal or approximate traveling salesman path for the current graph
 * using the specified Hamiltonian cycle algorithm.
 *
 * @param algorithm The class of the HamiltonianCycleAlgorithm to be used for computing the path.
 *                  If not specified, defaults to `NearestNeighborHeuristicTSP`.
 * @return A `GraphPath` representing the computed traveling salesman tour.
 */
fun <T, E> JGraph<T, E>.findTravellingSalesman(
    algorithm: KClass<out HamiltonianCycleAlgorithm<*, *>> = NearestNeighborHeuristicTSP::class
): GraphPath<T, E> {
    val algo = algorithm.java.getConstructor().newInstance()
    algo as HamiltonianCycleAlgorithm<T, E>
    return algo.getTour(this)
}
