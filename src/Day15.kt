fun main() {
    part1()
}

private fun part1() {
    val testInput = readInput("Day15_test")
    val testResult = testInput.totalRiskOfLowestRiskPath()
    check(testResult == 40)
    val input = readInput("Day15")
    val result = input.totalRiskOfLowestRiskPath()
    println("Part 1 result: $result")
}

private data class Matrix(val raw: List<List<Int>>) {
    private val lastIndexX = raw.lastIndex
    private val lastIndexY = raw[0].lastIndex

    fun totalRiskOfLowestRiskPath(): Int {
        var lowestTotalRiskByPath = dummyTotalRiskByPath()
        val startingPossibleResults = (0 to 0).neighbours().map { (x, y) -> setOf(0 to 0, x to y) to raw[x to y] }
        val possibleResults = ArrayDeque(startingPossibleResults)
        while (possibleResults.isNotEmpty()) {
            val (possibleBestPath, totalRisk) = possibleResults.removeLast()
            val (_, lowestTotalRisk) = lowestTotalRiskByPath
            possibleBestPath.last()
                .neighbours()
                .filter { it !in possibleBestPath }
                .forEach { (x, y) ->
                    val risk = totalRisk + raw[x to y]
                    if (risk >= lowestTotalRisk) {
                        return@forEach
                    }
                    val path = possibleBestPath + (x to y)
                    if (x == lastIndexX && y == lastIndexY) {
                        lowestTotalRiskByPath = path to risk
                        return@forEach
                    }
                    possibleResults.add(path to risk)
                }
        }
        val (_, lowestRisk) = lowestTotalRiskByPath
        return lowestRisk
    }

    private fun dummyTotalRiskByPath(): Pair<Set<Pair<Int, Int>>, Int> {
        val path =
            (raw.mapIndexed { index, _ -> index to 0 } + raw.last().mapIndexed { index, _ -> raw.lastIndex to index })
        val risk = path.sumOf { raw[it] }
        return path.toSet() to risk
    }

    private fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> {
        val (x, y) = this
        return (listOf(-1, 1).map { r -> (x + r) to y } + listOf(-1, 1).map { c -> x to (y + c) })
            .filter { (x, y) -> x > -1 && x <= lastIndexX && y > -1 && y <= lastIndexY }
    }

    private operator fun List<List<Int>>.get(pair: Pair<Int, Int>): Int = raw[pair.first][pair.second]
}

private fun readInput(file: String): Matrix = readStringInput(file)
    .map { it.toCharArray().map(Char::digitToInt) }
    .let { Matrix(it) }
