fun main() {
    part1()
}

private fun part1() {
    val testInput = readInput("Day15_test")
    val testResult = testInput.totalRiskOfLowestRiskPath()
    check(testResult == 40L)
    val input = readInput("Day15")
    val result = input.totalRiskOfLowestRiskPath()
    println("Part 1 result: $result")
}

private data class Matrix(val raw: List<List<Int>>) {
    private val table: MutableMap<Pair<Int, Int>, DjikstraData> = raw.foldIndexed(mutableMapOf()) { x, table, columns ->
        columns.forEachIndexed { y, _ ->
            table[x to y] = DjikstraData(known = false, cost = Long.MAX_VALUE, path = null)
        }
        table
    }
    private val lastIndexX = raw.lastIndex
    private val lastIndexY = raw[0].lastIndex

    fun totalRiskOfLowestRiskPath(): Long {
        val start = 0 to 0
        table[start] = table[start]!!.copy(known = true, cost = 0)
        start.updateCosts(neighbours = start.neighbours())

        while (table.anyUnknown()) {
            val from = table.cheapestUnknownVertex()
            from.updateCosts(neighbours = from.neighbours())
            table[from] = table[from]!!.copy(known = true)
        }

        return table[lastIndexX to lastIndexY]!!.cost
    }

    private fun Pair<Int, Int>.updateCosts(neighbours: List<Pair<Int, Int>>) {
        val startCost = table[this]!!.cost
        neighbours.forEach { vertex ->
            val cost = vertex.cost().toLong() + startCost
            if (table[vertex]!!.cost > cost) {
                table[vertex] = table[vertex]!!.copy(cost = cost, path = this)
            }
        }
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

    private fun Map<Pair<Int, Int>, DjikstraData>.cheapestUnknownVertex(): Pair<Int, Int> {
        return entries.first { (_, data) -> !data.known }.key
    }

    private data class DjikstraData(
        val known: Boolean,
        val cost: Long,
        val path: Pair<Int, Int>?
    )

    private operator fun List<List<Int>>.get(pair: Pair<Int, Int>): Int = raw[pair.first][pair.second]
}

private fun readInput(file: String): Matrix = readStringInput(file)
    .map { it.toCharArray().map(Char::digitToInt) }
    .let { Matrix(it) }
