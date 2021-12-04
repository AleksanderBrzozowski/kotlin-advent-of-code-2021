fun main() {
    val testInputResult = readIntInput("Day01_test").measurementDepthIncreases()
    check(testInputResult == 3) { "Expect 3, found: $testInputResult" }

    val inputResult = readIntInput("Day01").measurementDepthIncreases()
    println("Result: $inputResult")
}

private fun List<Int>.measurementDepthIncreases() =
    withIndex()
        .count { (index, value) ->
            when {
                index == 0 -> false
                this[index - 1] < value -> true
                else -> false
            }
        }
