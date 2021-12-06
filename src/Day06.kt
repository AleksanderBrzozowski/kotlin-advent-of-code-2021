fun main() {
    part1()
    part2()
}

private fun part2() {
    val testFishTimers = fishTimers("Day06_test")
    val testFishTimersSimulation = testFishTimers.numberOfFishAfter(numberOfDays = 256)
    check(testFishTimersSimulation == 26984457539) { "At the end of simulation there were: $testFishTimersSimulation fish" }
    val fishTimers = fishTimers("Day06")
    val fishTimersSimulation = fishTimers.numberOfFishAfter(numberOfDays = 256)
    println("Fish after 256 days: $fishTimersSimulation")
}

private fun part1() {
    val testFishTimers = fishTimers("Day06_test")
    val testFishTimersSimulation = testFishTimers.naiveSimulation(numberOfDays = 80)
    check(testFishTimersSimulation.size == 5934) { "At the end of simulation there were: ${testFishTimersSimulation.size} fish" }

    val fishTimers = fishTimers("Day06")
    val fishTimersSimulation = fishTimers.naiveSimulation(numberOfDays = 80)
    println("Fish after 80 days: ${fishTimersSimulation.size}")
}

private fun fishTimers(file: String): List<Int> =
    readStringInput(file)
        .first()
        .split(",")
        .map { it.toInt() }

private fun List<Int>.naiveSimulation(numberOfDays: Int): List<Int> {
    return 1.rangeTo(numberOfDays)
        .fold(this) { fishTimers, _ ->
            fishTimers.flatMap { fishTimer ->
                when (fishTimer) {
                    0 -> listOf(6, 8)
                    else -> listOf(fishTimer - 1)
                }
            }
        }
}

private fun List<Int>.numberOfFishAfter(numberOfDays: Int): Long {
    val initialFishTimers = 0.rangeTo(8)
        .map { timer -> this.count { it == timer }.toLong() }

    return 1.rangeTo(numberOfDays)
        .fold(initialFishTimers) { fishTimers, _ ->
            fishTimers.mapIndexed { index, _ ->
                when (index) {
                    6 -> fishTimers[7] + fishTimers[0]
                    8 -> fishTimers[0]
                    else -> fishTimers[index + 1]
                }
            }
        }
        .sum()
}
