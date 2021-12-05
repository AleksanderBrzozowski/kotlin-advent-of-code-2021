import kotlin.math.abs

fun main() {
    part1()
    part2()
}

private fun part1() {
    val testLineSegments = readStringInput("Day05_test").toLineSegments()
    val testOverlappingPoints = testLineSegments
        .filterIsInstance<StraightLineSegment>()
        .overlappingPoints()
    check(testOverlappingPoints.size == 5) { "Points: $testOverlappingPoints" }
    val lineSegments = readStringInput("Day05").toLineSegments()
    val overlappingPoints = lineSegments
        .filterIsInstance<StraightLineSegment>()
        .overlappingPoints()
    println("Number of overlapping points (straight lines): ${overlappingPoints.size}")
}

private fun part2() {
    val testLineSegments = readStringInput("Day05_test").toLineSegments()
    val testOverlappingPoints = testLineSegments.overlappingPoints()
    check(testOverlappingPoints.size == 12) { "Points: $testOverlappingPoints" }
    val lineSegments = readStringInput("Day05").toLineSegments()
    val overlappingPoints = lineSegments.overlappingPoints()
    println("Number of overlapping points (straight lines and diagonal lines): ${overlappingPoints.size}")
}

private fun List<LineSegment>.overlappingPoints() = flatMap { it.pointsBetween() }
    .groupingBy { it }.eachCount()
    .filter { (_, occurrences) -> occurrences > 1 }

private sealed class LineSegment {
    abstract fun pointsBetween(): List<Point>
}

private data class StraightLineSegment(val start: Point, val end: Point) : LineSegment() {

    init {
        check(start.x == end.x || start.y == end.y) { "Cannot build line segment from $start to $end" }
    }

    override fun pointsBetween(): List<Point> {
        val pointsOnYAxis = rangeBetween { x }.map { x -> Point(x, start.y) }
        val pointsOnXAxis = rangeBetween { y }.map { y -> Point(start.x, y) }
        return pointsOnYAxis + pointsOnXAxis
    }

    private fun rangeBetween(axisSelector: Point.() -> Int): IntRange {
        val start = axisSelector(start)
        val end = axisSelector(end)
        return when {
            end > start -> start.rangeTo(end)
            end == start -> IntRange.EMPTY
            else -> end.rangeTo(start)
        }
    }
}

private data class DiagonalLineSegment(val start: Point, val end: Point) : LineSegment() {

    init {
        check(abs(start.x - end.x) == abs(start.y - end.y)) { "Cannot build line segment from $start to $end" }
    }

    override fun pointsBetween(): List<Point> {
        val (pointWithMinX, secondPoint) = if (start.x < end.x) start to end else end to start
        val isRaising = secondPoint.y - pointWithMinX.y > 0
        return (pointWithMinX.x).rangeTo(secondPoint.x)
            .mapIndexed { index, x ->
                val y = if (isRaising) pointWithMinX.y + index else pointWithMinX.y - index
                Point(x, y)
            }
    }

    private fun minMax(axisSelector: Point.() -> Int): Pair<Int, Int> {
        val start = axisSelector(start)
        val end = axisSelector(end)
        return if (start > end) end to start else start to end
    }
}

private fun List<String>.toLineSegments(): List<LineSegment> =
    mapNotNull { rows ->
        val (start, end) = rows.split(" -> ")
            .map { points ->
                val (x, y) = points.split(",")
                Point(x.toInt(), y.toInt())
            }
        when {
            start.x == end.x || start.y == end.y -> StraightLineSegment(start, end)
            else -> DiagonalLineSegment(start, end)
        }
    }

private data class Point(val x: Int, val y: Int)
