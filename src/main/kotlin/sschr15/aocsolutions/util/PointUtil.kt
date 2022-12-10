@file:OptIn(ExperimentalStdlibApi::class)

package sschr15.aocsolutions.util

import kotlin.math.*

/**
 * Get the Manhattan / taxicab distance between two points.
 */
fun AbstractPoint.manhattanDistance(other: AbstractPoint): Int = (x() - other.x()).absoluteValue + (y() - other.y()).absoluteValue

/**
 * Get the squared euclidean distance between two points.
 * This is faster than [distance] because square roots are expensive.
 */
fun AbstractPoint.distanceSquared(other: AbstractPoint): Double = (x() - other.x()).toDouble().pow(2) + (y() - other.y()).toDouble().pow(2)

/**
 * Get the euclidean distance between two points.
 * If you only need to compare distances or have predefined distances, use [distanceSquared] instead as it is faster.
 */
fun AbstractPoint.distance(other: AbstractPoint): Double = sqrt(distanceSquared(other))

/**
 * Get the angle between two points.
 * This is the angle from the origin to this point, and the angle from the origin to the other point.
 * The angle is in radians, and is in the range of `0` to `2 * PI`
 * (sometimes called ["tau"](https://www.youtube.com/watch?v=FtxmFlMLYRI)).
 */
fun AbstractPoint.angle(other: AbstractPoint): Double {
    var angle = atan2((other.y() - y()).toDouble(), (other.x() - x()).toDouble())
    if (angle < 0) angle += 2 * PI
    return angle
}

/**
 * Get the point 1 higher than this point.
 */
fun AbstractPoint.up() = Point(x(), y() - 1)

/**
 * Get the point 1 lower than this point.
 */
fun AbstractPoint.down() = Point(x(), y() + 1)

/**
 * Get the point 1 to the left of this point.
 */
fun AbstractPoint.left() = Point(x() - 1, y())

/**
 * Get the point 1 to the right of this point.
 */
fun AbstractPoint.right() = Point(x() + 1, y())

/**
 * Get the chess-board equivalent distance between two points.
 */
fun AbstractPoint.chessDistance(other: AbstractPoint): Int = max((x() - other.x()).absoluteValue, (y() - other.y()).absoluteValue)

fun AbstractPoint.toPoint() = Point(x(), y())

interface AbstractPoint {
    fun x(): Int
    fun y(): Int

    enum class Direction(val dx: Int, val dy: Int) {
        LTR(1, 0),
        RTL(-1, 0),
        TTB(0, 1),
        BTT(0, -1),
        BL_TR(1, 1),
        TR_BL(-1, -1),
        TL_BR(1, -1),
        BR_TL(-1, 1);

        fun next(point: AbstractPoint) = Point(point.x() + dx, point.y() + dy)
        fun next(n: Int, point: AbstractPoint) = Point(point.x() + dx * n, point.y() + dy * n)
    }
}

data class Point(val x: Int, val y: Int) : Comparable<Point>, AbstractPoint {
    override fun compareTo(other: Point): Int {
        return if (y == other.y) x.compareTo(other.x) else y.compareTo(other.y)
    }

    override fun x() = x

    override fun y() = y

    override fun equals(other: Any?): Boolean = when (other) {
        is AbstractPoint -> x == other.x() && y == other.y()
        is Pair<*, *> -> x == other.first && y == other.second
        else -> false
    }

    operator fun rangeTo(other: Point) = PointRange(this, other)
    operator fun rangeUntil(other: Point) = ExclusivePointRange(this, other)

    override fun hashCode() = 31 * x + y

    override fun toString(): String {
        return "($x, $y)"
    }

    companion object {
        val origin = Point(0, 0)
    }
}
data class MutablePoint(var x: Int, var y: Int) : AbstractPoint {
    override fun equals(other: Any?) = when (other) {
        is AbstractPoint -> x == other.x() && y == other.y()
        is Pair<*, *> -> x == other.first && y == other.second
        else -> false
    }

    override fun hashCode() = 31 * x + y

    override fun x() = x
    override fun y() = y

    override fun toString(): String {
        return "($x, $y)"
    }
}

data class PointRange(
    override val start: Point,
    override val endInclusive: Point
) : ClosedRange<Point>, Iterable<Point> {
    override fun iterator(): Iterator<Point> = PointIterator(start, endInclusive, true)
}

data class ExclusivePointRange(
    override val start: Point,
    override val endExclusive: Point
) : OpenEndRange<Point>, Iterable<Point> {
    override fun iterator(): Iterator<Point> = PointIterator(start, endExclusive, false)
}

private class PointIterator(start: Point, private val end: Point, private val includeEnd: Boolean) : Iterator<Point> {
    val direction = when {
        start.x == end.x && start.y == end.y -> AbstractPoint.Direction.LTR
        start.x <  end.x && start.y == end.y -> AbstractPoint.Direction.LTR
        start.x >  end.x && start.y == end.y -> AbstractPoint.Direction.RTL
        start.x == end.x && start.y <  end.y -> AbstractPoint.Direction.TTB
        start.x == end.x                     -> AbstractPoint.Direction.BTT
        start.x <  end.x && start.y <  end.y -> AbstractPoint.Direction.BL_TR
        start.x <  end.x                     -> AbstractPoint.Direction.TL_BR
        start.y <  end.y                     -> AbstractPoint.Direction.TR_BL
        else                                 -> AbstractPoint.Direction.BR_TL
    }

    private var working = Point(start.x, start.y)

    override fun hasNext() = if (includeEnd) direction.next(working) != end else working != end

    override fun next(): Point {
        if (!hasNext()) throw NoSuchElementException()
        val current = working
        working = direction.next(working)
        return current
    }
}

data class Line(val start: Point, val end: Point) : Iterable<Point> {
    val length = sqrt((start.x - end.x).toDouble().pow(2) + (start.y - end.y).toDouble().pow(2))
    val slope = (start.y - end.y).toDouble() / (start.x - end.x).toDouble()
    private val yIntercept = start.y - slope * start.x
    val wholeNumberPoints by lazy {
        if (slope.isFinite()) {
            // not a vertical line
            val min = kotlin.math.min(start.x, end.x)
            val max = kotlin.math.max(start.x, end.x)
            (min..max)
                .map { it to it * slope + yIntercept }
                .filter { (_, y) -> y.roundToInt().toDouble() == y }
                .map { (x, y) -> Point(x, y.roundToInt()) }
        } else {
            // vertical line
            val min = kotlin.math.min(start.y, end.y)
            val max = kotlin.math.max(start.y, end.y)
            (min..max)
                .map { y -> Point(start.x, y) }
        }
    }

    override fun iterator(): Iterator<Point> = wholeNumberPoints.iterator()

    companion object {
        operator fun invoke(x1: Int, y1: Int, x2: Int, y2: Int) = Line(Point(x1, y1), Point(x2, y2))
        operator fun invoke(points: Pair<Point, Point>) = Line(points.first, points.second)
    }
}
