import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.time.ExperimentalTime

fun main() {
    part1()
    part2PrepareData()
    part2()
}

private fun part1() {
    val testInput = readInput("Day15_test")
    val testResult = testInput.totalRiskOfLowestRiskPath()
    check(testResult == 40L)
    val input = readInput("Day15")
    val result = input.totalRiskOfLowestRiskPath()
    println("Part 1 result: $result")
}

private fun part2PrepareData() {
    val expectedInput = readInput("Day15_test2")
    val input = readInput("Day15_test").toPart2()
    check(expectedInput.raw == input.raw)
}

private fun part2() {
    val testInput = readInput("Day15_test").toPart2()
    val testResult = testInput.totalRiskOfLowestRiskPath()
    check(testResult == 315L)
    val input = readInput("Day15").toPart2()
    val result = input.totalRiskOfLowestRiskPath()
    println("Part 2 result: $result")
}

private data class Matrix(val raw: List<List<Int>>) {
    private val table: MutableMap<Pair<Int, Int>, DjikstraData> = raw.foldIndexed(mutableMapOf()) { x, table, columns ->
        columns.forEachIndexed { y, _ ->
            table[x to y] = DjikstraData(known = false, cost = Long.MAX_VALUE, path = null)
        }
        table
    }
    private val cheapestUnknownVertices: PriorityQueue<Pair<Pair<Int, Int>, Long>> =
        PriorityQueue { (_, cost1), (_, cost2) -> cost1.compareTo(cost2) }
    private val lastIndexX = raw.lastIndex
    private val lastIndexY = raw[0].lastIndex

    @OptIn(ExperimentalTime::class)
    fun totalRiskOfLowestRiskPath(): Long {
        val start = 0 to 0
        table[start] = table[start]!!.copy(cost = 0)
        start.updateCosts(neighbours = start.neighbours())

        while (cheapestUnknownVertices.isNotEmpty() || table.anyUnknown()) {
            val from = when (cheapestUnknownVertices.isNotEmpty()) {
                true -> cheapestUnknownVertices.poll().first
                false -> table.entries.first { (v, djikstraData) -> !djikstraData.known }.key
            }
            if (table[from]!!.known) {
                continue
            }
            val neighbours = from.neighbours()
            from.updateCosts(neighbours = neighbours)
        }

        return table[lastIndexX to lastIndexY]!!.cost
    }

    private fun Pair<Int, Int>.updateCosts(neighbours: List<Pair<Int, Int>>) {
        val startCost = table[this]!!.cost
        neighbours.forEach { vertex ->
            val cost = vertex.cost().toLong() + startCost
            val djikstraData = table[vertex]!!
            if (djikstraData.cost > cost) {
                cheapestUnknownVertices.add(vertex to cost)
                table[vertex] = djikstraData.copy(cost = cost, path = this)
            }
        }
        table[this] = table[this]!!.copy(known = true)
    }

    private fun Pair<Int, Int>.cost(): Int {
        val (x, y) = this
        return raw[x][y]
    }

    private fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> {
        val (x, y) = this
        return (listOf(-1, 1).map { r -> (x + r) to y } + listOf(-1, 1).map { c -> x to (y + c) })
            .filter { (x, y) -> x > -1 && x <= lastIndexX && y > -1 && y <= lastIndexY }
    }

    private fun Map<Pair<Int, Int>, DjikstraData>.anyUnknown() = !values.all { it.known }

    private data class DjikstraData(
        val known: Boolean,
        val cost: Long,
        val path: Pair<Int, Int>?
    )

    private operator fun List<List<Int>>.get(pair: Pair<Int, Int>): Int = raw[pair.first][pair.second]

    fun toPart2(): Matrix = (0 until (lastIndexX + 1) * 5).map { x ->
        (0 until (lastIndexY + 1) * 5).map { y ->
            val increase = x / (lastIndexX + 1) + y / (lastIndexY + 1)
            val originalValue = raw[x % (lastIndexX + 1)][y % (lastIndexX + 1)]
            val newValue = originalValue + increase
            if (newValue > 9) abs(9 - newValue) else newValue
        }
    }.let { Matrix(it) }
}

private fun readInput(file: String): Matrix = readStringInput(file)
    .map { it.toCharArray().map(Char::digitToInt) }
    .let { Matrix(it) }
