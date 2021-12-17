fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInput = readInput("Day08_test")
    val testResult = testInput.countUniqueNumberOfSegmentsInOutputValues()
    check(testResult == 26)
    val input = readInput("Day08")
    val result = input.countUniqueNumberOfSegmentsInOutputValues()
    println("Count of unique number of segments in output values: $result")
}

private fun part2() {
    val testInput = readInput("Day08_test")
    val testResult = testInput.sumOfOutputs()
    check(testResult == 61229)
    val input = readInput("Day08")
    val result = input.sumOfOutputs()
    println("Sum of outputs: $result")
}

private fun List<Pair<List<String>, List<String>>>.countUniqueNumberOfSegmentsInOutputValues() =
    this.sumOf { (signalPatterns, outputValues) -> outputValues.count { it.hasUniqueNumberOfSegments() } }

private fun List<Pair<List<String>, List<String>>>.sumOfOutputs() =
    sumOf { (signalPatterns, outputValues) ->
        SevenSegmentDisplay(signalPatterns).outputAsNumber(outputValues)
    }

private fun String.hasUniqueNumberOfSegments() = when (this.length) {
    2, 3, 4, 7 -> true
    else -> false
}

private fun readInput(file: String): List<Pair<List<String>, List<String>>> {
    return readStringInput(file)
        .map { line ->
            val (signalPatterns, outputValues) = line.split(" | ")
            signalPatterns.split(" ") to outputValues.split(" ")
        }
}

class SevenSegmentDisplay(input: List<String>) {
    private val digitsBySegments: Map<Int, String>

    init {
        digitsBySegments = mapOf(
            1 to input.findBySegmentsNumber(2),
            4 to input.findBySegmentsNumber(4),
            7 to input.findBySegmentsNumber(3),
            8 to input.findBySegmentsNumber(7),
        )
    }

    fun outputAsNumber(output: List<String>): Int = output.joinToString(separator = "") { segments ->
        when (segments.length) {
            2 -> "1"
            4 -> "4"
            3 -> "7"
            7 -> "8"
            6 -> when {
                (segments - digitsBySegments[4]).length == 2 -> "9"
                (segments - digitsBySegments[1]).length == 4 -> "0"
                else -> "6"
            }
            5 -> when {
                (segments - digitsBySegments[1]).length == 3 -> "3"
                (segments - digitsBySegments[4]).length == 2 -> "5"
                else -> "2"
            }
            else -> throw Error("Unreachable")
        }
    }
        .toInt()

    private companion object {
        fun List<String>.findBySegmentsNumber(number: Int) = first { it.length == number }

        operator fun String?.minus(s: String?): String {
            check(this != null && s != null)
            return this.filterNot { s.contains(it) }
        }
    }
}
