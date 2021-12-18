fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInput = readInput("Day11_test")
    val testResult = DumboOctopus(testInput).flashesAfterNumberOfSteps(100)
    check(testResult == 1656)
    val input = readInput("Day11")
    val result = DumboOctopus(input).flashesAfterNumberOfSteps(100)
    println("Total flashes after 100 steps: $result")
}

private fun part2() {
    val testInput = readInput("Day11_test")
    val testResult = DumboOctopus(testInput).stepInWhichOctopusesFlashSimultaneously()
    check(testResult == 195)
    val input = readInput("Day11")
    val result = DumboOctopus(input).stepInWhichOctopusesFlashSimultaneously()
    println("First step in which octopuses flash simultaneously: $result")
}

private typealias Matrix = MutableList<MutableList<Int>>

private class DumboOctopus(input: List<List<Int>>) {

    private val octopuses: Matrix = input.map { it.toMutableList() }.toMutableList()

    fun flashesAfterNumberOfSteps(steps: Int): Int {
        var countOfFlashedOctopuses = 0
        repeat(steps) {
            var positionsOfOctopusesThatFlashed = octopuses.increaseEnergyLevelOfEachOctopus()
            while (positionsOfOctopusesThatFlashed.isNotEmpty()) {
                countOfFlashedOctopuses += positionsOfOctopusesThatFlashed.size
                positionsOfOctopusesThatFlashed =
                    octopuses.increaseEnergyLevelOfAdjacent(positionsOfOctopusesThatFlashed)
            }
        }
        return countOfFlashedOctopuses
    }

    fun stepInWhichOctopusesFlashSimultaneously(): Int {
        val numberOfOctopuses = octopuses.sumOf { it.size }
        repeat(Int.MAX_VALUE) { step ->
            val numberOfFlashes = flashesAfterNumberOfSteps(1)
            if (numberOfOctopuses == numberOfFlashes) {
                return step + 1
            }
        }
        throw Error("Unreachable")
    }

    private fun Matrix.increaseEnergyLevelOfEachOctopus(): List<Pair<Int, Int>> =
        octopuses.flatMapIndexed { rowIndex, rows ->
            rows.mapIndexedNotNull { columnIndex, _ ->
                val hasFlashed = octopuses.increaseEnergyLevel(rowIndex to columnIndex)
                if (hasFlashed) rowIndex to columnIndex else null
            }
        }

    private fun Matrix.increaseEnergyLevelOfAdjacent(positionsOfOctopusesThatFlashedInPreviousStep: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return positionsOfOctopusesThatFlashedInPreviousStep.flatMap { (row, column) ->
            val positionsToIncreaseEnergyLevel = octopuses.adjacentThatNotFlashedInCurrentStep(row to column)
            positionsToIncreaseEnergyLevel.mapNotNull { position ->
                val hasFlashed = octopuses.increaseEnergyLevel(position)
                if (hasFlashed) position else null
            }
        }
    }

    // returns if it flashed
    private fun Matrix.increaseEnergyLevel(position: Pair<Int, Int>): Boolean {
        if (this[position] == 9) {
            this[position] = 0
            return true
        }
        this[position] = this[position] + 1
        return false
    }

    private fun Matrix.adjacentThatNotFlashedInCurrentStep(position: Pair<Int, Int>): List<Pair<Int, Int>> {
        val (r, c) = position
        return (-1..1).flatMap { row -> (-1..1).map { column -> (r + row) to (c + column) } }
            .filter { it.isNotOutOfBounds() && it != position && this[it] != 0 }
    }

    private fun Pair<Int, Int>.isNotOutOfBounds(): Boolean {
        val (row, column) = this
        return row > -1 && column > -1 && row < octopuses.size && column < octopuses[0].size
    }

    private operator fun Matrix.get(position: Pair<Int, Int>): Int {
        val (row, column) = position
        return this[row][column]
    }

    private operator fun Matrix.set(position: Pair<Int, Int>, value: Int) {
        val (row, column) = position
        this[row][column] = value
    }
}

private fun readInput(file: String): List<List<Int>> = readStringInput(file)
    .map { line -> line.map { it.digitToInt() } }
