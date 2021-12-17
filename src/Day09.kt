fun main() {
    part1()
    part2()
}

private fun part2() {
    val testInput = readInput("Day09_test")
    val testResult = testInput.threeLargestBasins().fold(1) { acc: Int, i: Int -> acc * i }
    check(testResult == 1134)
    val input = readInput("Day09")
    val result = input.threeLargestBasins().fold(1) { acc: Int, i: Int -> acc * i }
}

private fun part1() {
    val testInput = readInput("Day09_test")
    val testResult = testInput.lowPoints().riskLevel()
    check(testResult == 15)
    val input = readInput("Day09")
    val result = input.lowPoints().riskLevel()
    println("Sum of risk levels for low points is: $result")
}

private fun List<List<Int>>.lowPoints(): List<Int> =
    this.flatMapIndexed() { rowIndex: Int, row: List<Int> ->
        row.mapIndexedNotNull { columnIndex, location ->
            val isLowestPoint = adjacentLocations(rowIndex = rowIndex, columnIndex = columnIndex)
                .all { adjacentLocation -> location < adjacentLocation }
            if (isLowestPoint) location else null
        }
    }

private fun List<List<Int>>.threeLargestBasins(): List<Int> =
    this.flatMapIndexed() { rowIndex: Int, row: List<Int> ->
        row.mapIndexedNotNull { columnIndex, location ->
            val isLowestPoint = adjacentLocations(rowIndex = rowIndex, columnIndex = columnIndex)
                .all { adjacentLocation -> location < adjacentLocation }
            if (!isLowestPoint) {
                return@mapIndexedNotNull null
            }

            println("lowest point found")
            basinsLocationsPositions(rowIndex = rowIndex, columnIndex = columnIndex).size
        }
    }
        .sortedDescending()
        .subList(0, 3)

private fun List<List<Int>>.adjacentLocations(rowIndex: Int, columnIndex: Int): List<Int> =
    this.adjacentLocationsPositions(rowIndex, columnIndex).map { (r, c) -> this[r][c] }

private fun List<List<Int>>.adjacentLocationsPositions(rowIndex: Int, columnIndex: Int): List<Pair<Int, Int>> =
    this.adjacentLocationsPositions(rowIndex).map { it to columnIndex } +
        this[rowIndex].adjacentLocationsPositions(columnIndex).map { rowIndex to it }

private fun List<List<Int>>.basinsLocationsPositions(
    rowIndex: Int,
    columnIndex: Int,
    visitedPoints: Set<Pair<Int, Int>> = emptySet()
): Set<Pair<Int, Int>> {
    if (this[rowIndex][columnIndex] == 9) {
        return emptySet()
    }
    println("${rowIndex to columnIndex}, $visitedPoints")
    return adjacentLocationsPositions(rowIndex, columnIndex)
        .filterNot { visitedPoints.contains(it) }
        .flatMapTo(mutableSetOf()) { (r, c) ->
            println("flatMap -> ${r to c}, $visitedPoints")
            basinsLocationsPositions(
                rowIndex = r,
                columnIndex = c,
                visitedPoints = visitedPoints + (rowIndex to columnIndex)
            )
        } + setOf(rowIndex to columnIndex)
}

private fun List<Int>.riskLevel() = sumOf { it + 1 }

private fun <T> List<T>.adjacentLocationsPositions(index: Int): List<Int> = when {
    index == 0 -> listOf(index + 1)
    index + 1 == this.size -> listOf(index - 1)
    else -> listOf(index - 1, index + 1)
}

private fun readInput(file: String): List<List<Int>> {
    return readStringInput(file)
        .map { line -> line.map { it.digitToInt() } }
}
