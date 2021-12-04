import Command.DOWN
import Command.FORWARD
import Command.UP

fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInputResult = readInput("Day02_test").part1MultiplicationOfPositionAndDepth()
    check(testInputResult == 120) { "Expected: 120, Found: $testInputResult" }
    val inputResult = readInput("Day02").part1MultiplicationOfPositionAndDepth()
    println("Result 1: $inputResult")
}

private fun part2() {
    val testInputResult = readInput("Day02_test").part2MultiplicationOfPositionAndDepth()
    check(testInputResult == 540) { "Expected: 540, Found: $testInputResult" }
    val inputResult = readInput("Day02").part2MultiplicationOfPositionAndDepth()
    println("Result 2: $inputResult")
}

private fun List<Pair<Command, Int>>.part1MultiplicationOfPositionAndDepth() =
    fold(0 to 0) { (horizontalPosition, depth), (command, change) ->
        when (command) {
            DOWN -> horizontalPosition to depth + change
            UP -> horizontalPosition to depth - change
            FORWARD -> horizontalPosition + change to depth
        }
    }
        .let { (horizontalPosition, depth) -> horizontalPosition * depth }

private fun List<Pair<Command, Int>>.part2MultiplicationOfPositionAndDepth() =
    fold(Triple(0, 0, 0)) { (horizontalPosition, depth, aim), (command, change) ->
        when (command) {
            DOWN -> Triple(horizontalPosition, depth, aim + change)
            UP -> Triple(horizontalPosition, depth, aim - change)
            FORWARD -> Triple(horizontalPosition + change, depth + aim * change, aim)
        }
    }
        .let { (horizontalPosition, depth, _) -> horizontalPosition * depth }

private fun readInput(file: String): List<Pair<Command, Int>> {
    return readStringInput(file)
        .map {
            val (command, change) = it.split(" ")
            Command.valueOf(command.uppercase()) to change.toInt()
        }
}

private enum class Command { DOWN, UP, FORWARD }
