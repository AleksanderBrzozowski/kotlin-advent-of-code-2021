import kotlin.math.abs

fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInput = readInput("Day07_test")
    val (testPosition, testCost) = testInput.determineCheapestAlignment(CONSTANT_RATE)
    check(testPosition == 2) { "Position: $testPosition" }
    check(testCost == 37) { "Cost: $testCost" }
    val input = readInput("Day07")
    val (position, cost) = input.determineCheapestAlignment(CONSTANT_RATE)
    println("Position: $position, cost: $cost")
}

private fun part2() {
    val testInput = readInput("Day07_test")
    val (testPosition, testCost) = testInput.determineCheapestAlignment(INCREASED_USAGE_RATE)
    check(testPosition == 5) { "Position: $testPosition" }
    check(testCost == 168) { "Cost: $testCost" }
    val input = readInput("Day07")
    val (position, cost) = input.determineCheapestAlignment(INCREASED_USAGE_RATE)
    println("Position: $position, cost: $cost")
}

private interface BurnModel {
    fun cost(currentPosition: Int, newPosition: Int): Int
}

private val CONSTANT_RATE: BurnModel = object : BurnModel {
    override fun cost(currentPosition: Int, newPosition: Int): Int = abs(newPosition - currentPosition)
}

private val INCREASED_USAGE_RATE: BurnModel = object : BurnModel {
    override fun cost(currentPosition: Int, newPosition: Int): Int {
        val constantRateCost = CONSTANT_RATE.cost(currentPosition, newPosition)
        // formula based on arithmetic series. sum = ((a1 + an) / 2) * an, where a1 = 1, an = constant rate
        // the calculation is optimised for cost - it doesn't use BigDecimals
        return when {
            constantRateCost == 0 -> 0
            constantRateCost == 1 -> 1
            constantRateCost % 2 == 0 -> (constantRateCost / 2) * (1 + constantRateCost)
            else -> ((1 + constantRateCost) / 2) * constantRateCost
        }
    }
}

// position <-> cost
private fun List<Int>.determineCheapestAlignment(burnModel: BurnModel): Pair<Int, Int> {
    val min = this.minOrNull()!!
    val max = this.maxOrNull()!!
    return min.rangeTo(max)
        .map { alignmentPosition ->
            alignmentPosition to this.sumOf { position -> burnModel.cost(position, alignmentPosition) }
        }
        .minByOrNull { (_, cost) -> cost }!!
}

private fun readInput(file: String): List<Int> {
    return readStringInput(file)
        .first()
        .split(",")
        .map { it.toInt() }
}
