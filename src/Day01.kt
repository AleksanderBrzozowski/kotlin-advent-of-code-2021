fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInputResult = readIntInput("Day01_test").measurementDepthIncreases()
    check(testInputResult == 3) { "Expect 3, found: $testInputResult" }

    val inputResult = readIntInput("Day01").measurementDepthIncreases()
    println("Result 1: $inputResult")
}

private fun part2() {
    val testInputResult = readIntInput("Day01_test").measureWindowedDepthIncreases()
    check(testInputResult == 2) { "Expected: 2, found: $testInputResult" }
    val inputResult = readIntInput("Day01").measureWindowedDepthIncreases()
    println("Result 2: $inputResult")
}

private fun List<Int>.measurementDepthIncreases(): Int =
    windowed(2)
        .count { (prev, current) -> prev < current }

private fun List<Int>.measureWindowedDepthIncreases(): Int =
    windowed(3) { measurements -> measurements.sum() }
        .windowed(2)
        .count { (prev, current) -> prev < current }
