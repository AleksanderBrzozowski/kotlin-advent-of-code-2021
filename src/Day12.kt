import Graph.VertexType.BIG_CAVE
import Graph.VertexType.END
import Graph.VertexType.SMALL_CAVE
import Graph.VertexType.START

fun main() {
    part1()
}

private fun part1() {
    val testInput = readGraph("Day12_test")
    val testResult = testInput.numberOfPathsThatVisitSmallCavesAtMostOnce()
    check(testResult == 10)
    val testInput1 = readGraph("Day12_test1")
    val testResult1 = testInput1.numberOfPathsThatVisitSmallCavesAtMostOnce()
    check(testResult1 == 19)
    val testInput2 = readGraph("Day12_test2")
    val testResult2 = testInput2.numberOfPathsThatVisitSmallCavesAtMostOnce()
    check(testResult2 == 226)
    val input = readGraph("Day12")
    val result = input.numberOfPathsThatVisitSmallCavesAtMostOnce()
    println("Number of paths that visit small caves at most once: $result")
}

private fun readGraph(file: String): Graph = readStringInput(file)
    .map { val (start, end) = it.split("-"); start to end }
    .let { Graph(it) }

private typealias AdjacentByPoints = Map<String, List<String>>

class Graph(input: List<Pair<String, String>>) {
    private val adjacentByPoints: AdjacentByPoints =
        input.fold(emptyMap()) { adjacentByPoints, (v1, v2) ->
            val v1Adjacent = (adjacentByPoints[v1] ?: emptyList()) + v2
            val v2Adjacent = (adjacentByPoints[v2] ?: emptyList()) + v1
            adjacentByPoints + mapOf(v1 to v1Adjacent, v2 to v2Adjacent)
        }

    fun numberOfPathsThatVisitSmallCavesAtMostOnce(): Int {
        val (start, startAdjacent) = adjacentByPoints.start()
        val visitedSmallCavesByMaybePaths =
            startAdjacent.mapTo(ArrayDeque()) { point ->
                val visitedSmallCaves = if (point.toVertexType() == SMALL_CAVE) setOf(point) else emptySet()
                listOf(start, point) to visitedSmallCaves
            }
        val paths = mutableListOf<List<String>>()

        var visitedSmallCavesByMaybePath = visitedSmallCavesByMaybePaths.removeLastOrNull()

        while (visitedSmallCavesByMaybePath != null) {
            val (maybePath, visitedSmallCaves) = visitedSmallCavesByMaybePath
            val last = maybePath.last()
            when (last.toVertexType()) {
                START, END -> throw Error("last vertex should not be $last. Path: $maybePath")
                else -> {
                    val adjacent = adjacentByPoints[last] ?: throw Error("No point $last found")
                    for (adjacentPoint in adjacent) {
                        val adjacentPointType = adjacentPoint.toVertexType()
                        if (adjacentPointType == START) {
                            continue
                        }
                        if (adjacentPointType == SMALL_CAVE && adjacentPoint in visitedSmallCaves) {
                            continue
                        }
                        if (adjacentPointType == END) {
                            paths.add(maybePath + adjacentPoint)
                            continue
                        }
                        val newVisitedSmallCaves =
                            if (adjacentPointType == SMALL_CAVE) visitedSmallCaves + adjacentPoint else visitedSmallCaves
                        val newMaybePath = maybePath + adjacentPoint
                        visitedSmallCavesByMaybePaths.addLast(newMaybePath to newVisitedSmallCaves)
                    }
                }
            }
            visitedSmallCavesByMaybePath = visitedSmallCavesByMaybePaths.removeLastOrNull()
        }

        return paths.size
    }

    private fun AdjacentByPoints.start(): Pair<String, List<String>> =
        entries
            .first { (point, _) -> point.toVertexType() == START }
            .toPair()

    private fun String.toVertexType(): VertexType = when (this) {
        "start" -> START
        "end" -> END
        else -> when {
            this.all { it.isLowerCase() } -> SMALL_CAVE
            else -> BIG_CAVE
        }
    }

    enum class VertexType {
        START, END, SMALL_CAVE, BIG_CAVE
    }
}
