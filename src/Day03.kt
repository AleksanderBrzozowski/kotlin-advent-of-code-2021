import BitCriteriaMode.LEAST_COMMON
import BitCriteriaMode.MOST_COMMON

fun main() {
    part1()
    part2()
}

private fun part2() {
    val testInput = readStringInput("Day03_test")
    val testOxygenGeneratorRating = testInput.rating(MOST_COMMON)
    val testCo2ScrubberRating = testInput.rating(LEAST_COMMON)
    check(testOxygenGeneratorRating == 23) { "Expected: 23, Found: $testOxygenGeneratorRating" }
    check(testCo2ScrubberRating == 10) { "Expected: 10, Found: $testCo2ScrubberRating" }
    val input = readStringInput("Day03")
    val oxygenGeneratorRating = input.rating(MOST_COMMON)
    val co2ScrubberRating = input.rating(LEAST_COMMON)
    val lifeSupportRating = oxygenGeneratorRating * co2ScrubberRating
    println("Life support rating: $lifeSupportRating")
}

private fun List<String>.rating(mode: BitCriteriaMode): Int {
    var input = this
    val numberOfBitsInRow = input.first().length
    repeat(numberOfBitsInRow) { index ->
        if (input.size == 1) {
            return input.first().toInt(2)
        }
        val moreCommonBit = input.column(index).bitCriteria(mode)
        input = input.filter { it[index] == moreCommonBit }
    }
    check(input.size == 1)
    return input.first().toInt(2)
}

private fun List<String>.column(index: Int): List<Char> = map { it[index] }

private enum class BitCriteriaMode() { LEAST_COMMON, MOST_COMMON }

private fun List<Char>.bitCriteria(mode: BitCriteriaMode): Char {
    val numberOfZeros = count { it == '0' }
    val halfSize = size / 2
    return when (mode) {
        LEAST_COMMON -> when {
            numberOfZeros <= halfSize -> '0'
            else -> '1'
        }
        MOST_COMMON -> when {
            numberOfZeros > halfSize -> '0'
            else -> '1'
        }
    }
}

private fun part1() {
    val testConsumption = readStringInput("Day03_test").readConsumption()
    check(testConsumption == 198)
    val consumption = readStringInput("Day03").readConsumption()
    println("Consumption: $consumption")
}

private fun List<String>.readConsumption(): Int {
    val gamma = toGamma()
    val epsilon = gamma.toEpsilon()
    return gamma.toInt() * epsilon.toInt()
}

private fun List<String>.toGamma(): Gamma {
    val numberOfRows = size
    val numberOfBitsInRow = first().length
    val gamma = fold(MutableList(numberOfBitsInRow) { 0 }) { numberOfZerosInColumn, value ->
        value.mapIndexedTo(mutableListOf()) { index, bit ->
            when (bit) {
                '0' -> numberOfZerosInColumn[index] + 1
                '1' -> numberOfZerosInColumn[index]
                else -> throw IllegalArgumentException("Unknown bit: $bit")
            }
        }
    }
        .joinToString(separator = "") { if (it > numberOfRows / 2) "0" else "1" }
    return Gamma(gamma)
}

@JvmInline
private value class Gamma(val raw: String) {
    fun toEpsilon(): Epsilon = buildString {
        raw.forEach {
            when (it) {
                '0' -> append('1')
                else -> append('0')
            }
        }
    }.let { Epsilon(it) }

    fun toInt(): Int = Integer.parseInt(raw, 2)
}

@JvmInline
private value class Epsilon(val raw: String) {
    fun toInt(): Int = Integer.parseInt(raw, 2)
}
