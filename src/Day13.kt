import kotlin.math.abs

fun main() {
    part1()
    part2()
}

private fun part1() {
    val (testPoints, testFoldInstructions) = readInput("Day13_test")
    val testFirstFoldInstruction = testFoldInstructions.first()
    val testResultAfterFirstInstruction = testPoints.applyFoldInstruction(testFirstFoldInstruction)
    check(testResultAfterFirstInstruction.size == 17)

    val testSecondFoldInstruction = testFoldInstructions[1]
    val testResultAfterSecondInstruction =
        testResultAfterFirstInstruction.applyFoldInstruction(testSecondFoldInstruction)
    check(testResultAfterSecondInstruction.size == 16)

    val (points, foldInstructions) = readInput("Day13")
    val firsFoldInstruction = foldInstructions.first()
    val result = points.applyFoldInstruction(firsFoldInstruction)
    println("There are ${result.size} visible dots after completing the first fold instruction on transparent paper.")
}

private fun part2() {
    val (testPoints, testFoldInstructions) = readInput("Day13_test")
    val testResult =
        testFoldInstructions.fold(testPoints) { points, instruction -> points.applyFoldInstruction(instruction) }
    testResult.visualize()
    val (points, foldInstructions) = readInput("Day13")
    val result =
        foldInstructions.fold(points) { result, instruction -> result.applyFoldInstruction(instruction) }
    result.visualize()
}

private fun Set<Pair<Int, Int>>.applyFoldInstruction(instruction: Pair<Char, Int>): Set<Pair<Int, Int>> {
    val (fold, line) = instruction
    return when (fold) {
        'x' -> fold(line, isLeft = true)
        'y' -> fold(line, isLeft = false)
        else -> throw Error("Unknown fold: $fold")
    }
}

private fun Set<Pair<Int, Int>>.fold(line: Int, isLeft: Boolean): Set<Pair<Int, Int>> {
    val (resultingPoints, toFold) = filter { (x, y) -> if (isLeft) x != line else y != line }
        .partition { (x, y) -> if (isLeft) x < line else y < line }
    val foldedPoints = toFold.map { (x, y) -> if (isLeft) abs(x - 2 * line) to y else x to abs(y - 2 * line) }
    return (resultingPoints + foldedPoints).toSet()
}

private fun Iterable<Pair<Int, Int>>.visualize() {
    val (maxX) = maxByOrNull { (x, _) -> x }!!
    val (_, maxY) = maxByOrNull { (_, y) -> y }!!
    (0..maxY).joinToString(separator = "\n") { y ->
        (0..maxX).joinToString(separator = "") { x ->
            val isDot = (x to y) in this
            if (isDot) "#" else "."
        }
    }.apply { println(this) }
}

private fun readInput(file: String): Pair<Set<Pair<Int, Int>>, List<Pair<Char, Int>>> {
    val lines = readStringInput(file)
        .filter { it.isNotBlank() }
    val (foldInstructionsRaw, pointsRaw) = lines.partition { it.contains("fold") }
    val foldInstructions = foldInstructionsRaw.map { instruction ->
        val (fold, line) = instruction.split("=")
        fold.last() to line.toInt()
    }
    val points = pointsRaw.mapTo(hashSetOf()) {
        val (x, y) = it.split(",")
        x.toInt() to y.toInt()
    }
    return points to foldInstructions
}
