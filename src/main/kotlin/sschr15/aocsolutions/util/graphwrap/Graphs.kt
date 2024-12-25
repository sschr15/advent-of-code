package sschr15.aocsolutions.util.graphwrap

import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.util.VertexToIntegerMapping
import sschr15.aocsolutions.util.*
import org.jgrapht.Graph as JGraph

typealias Graph<T> = JGraph<T, *>
typealias UnweightedGraph<T> = JGraph<T, DefaultEdge>
typealias WeightedGraph<T> = JGraph<T, DefaultWeightedEdge>

/**
 * Creates and returns a new undirected, unweighted graph instance.
 *
 * @return A new instance of an undirected graph.
 */
fun <T> graphOf(): UnweightedGraph<T> = DefaultUndirectedGraph(DefaultEdge::class.java)

/**
 * Creates an undirected, unweighted graph with the specified vertices.
 *
 * @param vertices A variable number of vertices to be added to the graph.
 * @return A graph containing the specified vertices.
 */
fun <T> graphOf(vararg vertices: T): UnweightedGraph<T> =
    graphOf<T>().apply { vertices.forEach(::addVertex) }

/**
 * Creates and returns a new directed, unweighted graph instance with default edge implementation.
 *
 * @return A directed graph instance with default edge implementation.
 */
fun <T> directedGraphOf(): UnweightedGraph<T> = DefaultDirectedGraph(DefaultEdge::class.java)

/**
 * Creates a new directed, unweighted graph and adds the specified vertices to it.
 *
 * @param T the type of elements (vertices) in the graph
 * @param vertices the vertices to add to the created directed graph
 * @return a directed graph containing the specified vertices
 */
fun <T> directedGraphOf(vararg vertices: T): UnweightedGraph<T> =
    directedGraphOf<T>().apply { vertices.forEach(::addVertex) }

/**
 * Creates and returns a new directed, weighted graph.
 *
 * @return A weighted graph of type `T`, represented as a `DefaultDirectedGraph` using `DefaultWeightedEdge`.
 */
fun <T> weightedGraphOf(): WeightedGraph<T> = DefaultDirectedGraph(DefaultWeightedEdge::class.java)

/**
 * Creates a directed, weighted graph with the given vertices.
 *
 * @param T The type of the vertices in the graph.
 * @param vertices The vertices to be added to the graph.
 * @return A weighted graph containing the specified vertices.
 */
fun <T> weightedGraphOf(vararg vertices: T): WeightedGraph<T> =
    weightedGraphOf<T>().apply { vertices.forEach(::addVertex) }

/**
 * Creates a new weighted undirected graph instance.
 *
 * @return a newly constructed weighted, undirected graph of the specified type.
 */
fun <T> weightedUndirectedGraphOf(): WeightedGraph<T> = DefaultUndirectedGraph(DefaultWeightedEdge::class.java)

/**
 * Creates a weighted undirected graph and populates it with the specified vertices.
 *
 * @param vertices The vertices to be added to the graph.
 * @return A weighted undirected graph containing the provided vertices.
 */
fun <T> weightedUndirectedGraphOf(vararg vertices: T): WeightedGraph<T> =
    weightedUndirectedGraphOf<T>().apply { vertices.forEach(::addVertex) }

/**
 * Adds an edge with a specified weight between two vertices in the graph.
 *
 * @param from The starting vertex of the edge.
 * @param to The ending vertex of the edge.
 * @param weight The weight of the edge.
 */
fun <T> Graph<T>.addEdge(from: T, to: T, weight: Double) = Graphs.addEdge(this, from, to, weight)

/**
 * Adds two vertices to the graph if they do not already exist and connects them with an edge.
 *
 * @param from The source vertex to add and connect.
 * @param to The target vertex to add and connect.
 */
fun <T> Graph<T>.addAndConnectVertices(from: T, to: T) = Graphs.addEdgeWithVertices(this, from, to)

/**
 * Adds two vertices to the graph if they do not already exist and connects them with an edge of the given weight.
 *
 * @param from the starting vertex of the edge to be added.
 * @param to the ending vertex of the edge to be added.
 * @param weight the weight of the edge connecting the vertices.
 */
fun <T> Graph<T>.addAndConnectVertices(from: T, to: T, weight: Double) = Graphs.addEdgeWithVertices(this, from, to, weight)

/**
 * Copies all graph structure and data from another graph into the current graph.
 *
 * @param other The graph whose structure and data should be copied.
 * @return `true` if the graph was modified as a result of this operation.
 */
fun <T> Graph<T>.copyFrom(other: Graph<T>) = Graphs.addGraph(this.uncheckedCast(), other)

/**
 * Adds all vertices from a collection to the graph.
 *
 * @param vertices The collection of vertices to add to the graph.
 * @return `true` if the graph was modified as a result of this operation.
 */
fun <T> Graph<T>.addAllVertices(vertices: Collection<T>) = Graphs.addAllVertices(this, vertices)

/**
 * Retrieves the set of neighboring vertices for the given vertex in the graph.
 *
 * @param vertex The vertex whose neighbors are to be retrieved.
 * @return A set of neighboring vertices connected to the specified vertex.
 */
fun <T> Graph<T>.getNeighborsOf(vertex: T): Set<T> = Graphs.neighborSetOf(this, vertex)

/**
 * Retrieves the list of neighboring vertices for the specified vertex in the graph.
 *
 * @param vertex The vertex whose neighbors are to be retrieved.
 * @return A list of vertices that are neighbors of the specified vertex.
 */
fun <T> Graph<T>.getNeighborListOf(vertex: T): List<T> = Graphs.neighborListOf(this, vertex)

/**
 * Retrieves the list of incoming neighbors for a given vertex in the graph.
 *
 * @param vertex The vertex for which to find the incoming neighbors.
 * @return A list of vertices that have an outgoing edge directed to the specified vertex.
 */
fun <T> Graph<T>.getIncomingNeighborsOf(vertex: T): List<T> = Graphs.predecessorListOf(this, vertex)

/**
 * Retrieves the list of outgoing neighbors for the specified vertex in the graph.
 *
 * @param vertex the vertex whose outgoing neighbors are to be retrieved.
 * @return a list of outgoing neighbors of the specified vertex.
 */
fun <T> Graph<T>.getOutgoingNeighborsOf(vertex: T): List<T> = Graphs.successorListOf(this, vertex)

/**
 * Adds outgoing edges from the specified vertex to all vertices in the given collection in the current graph.
 *
 * @param vertex The vertex from which edges will be originating.
 * @param vertices A collection of vertices to which the edges will connect from the specified vertex.
 */
fun <T> Graph<T>.connectToAll(vertex: T, vertices: Collection<T>) = Graphs.addOutgoingEdges(this, vertex, vertices)

/**
 * Connects a given vertex in the graph to all specified vertices by adding incoming edges
 * from each of the vertices in the given collection to the specified vertex.
 *
 * @param vertex The vertex in the graph to which the incoming edges will be added.
 * @param vertices A collection of vertices from which edges to the specified vertex will be added.
 */
fun <T> Graph<T>.connectFromAll(vertex: T, vertices: Collection<T>) = Graphs.addIncomingEdges(this, vertex, vertices)

/**
 * Provides a mapping of each vertex in the graph to a unique integer index.
 *
 * This property allows efficient vertex indexing for algorithms that require
 * numerical representation of graph vertices, such as graph traversal or
 * shortest path algorithms. The mapping ensures that each vertex in the graph
 * is associated with a distinct integer.
 */
val <T> Graph<T>.vertexIndices: VertexToIntegerMapping<T>
    get() = Graphs.getVertexToIntegerMapping(this)
