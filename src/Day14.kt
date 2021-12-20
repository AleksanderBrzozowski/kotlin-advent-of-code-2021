fun main() {
    part1()
    part2()
}

private fun part1() {
    val (testPolymerTemplate, testPairInsertionRules) = readInput("Day14_test")
    val testResult = testPolymerTemplate.apply(testPairInsertionRules, steps = 10)
    val (testLeastCommon, testMostCommon) = testResult.occurrences(testPolymerTemplate)
    check(testMostCommon - testLeastCommon == 1588L)
    val (polymerTemplate, pairInsertionRules) = readInput("Day14")
    val result = polymerTemplate.apply(pairInsertionRules, steps = 10)
    val (leastCommon, mostCommon) = result.occurrences(polymerTemplate)
    println("Subtraction of most common and least common is: ${mostCommon - leastCommon}")
    check(mostCommon - leastCommon == 2584L)
}

private fun part2() {
    val (testPolymerTemplate, testPairInsertionRules) = readInput("Day14_test")
    val testResult = testPolymerTemplate.apply(testPairInsertionRules, steps = 40)
    val (testLeastCommon, testMostCommon) = testResult.occurrences(testPolymerTemplate)
    check(testMostCommon - testLeastCommon == 2188189693529L)
    val (polymerTemplate, pairInsertionRules) = readInput("Day14")
    val result = polymerTemplate.apply(pairInsertionRules, steps = 40)
    val (lestCommon, mostCommon) = result.occurrences(polymerTemplate)
    println("Subtraction of most common and least common is: ${mostCommon - lestCommon}")
}

private fun Map<Pair<Char, Char>, Long>.occurrences(polymer: String): Pair<Long, Long> {
    var letterOccurrences = this
        .flatMap { (pair, occurrences) ->
            val (a, _) = pair
            listOf(a to occurrences)
        }
        .groupingBy { (letter, _) -> letter }
        .fold(0L) { totalOccurrences, (_, occurrences) -> totalOccurrences + occurrences }
    // ugly hack, after 10 steps and after 40 steps, we need to add + 1 to last letter (because it is not counted)
    letterOccurrences = letterOccurrences + (polymer.last() to (letterOccurrences[polymer.last()]!! + 1))
    val leastCommon = letterOccurrences.minOf { (_, occurrences) -> occurrences }
    val mostCommon = letterOccurrences.maxOf { (_, occurrences) -> occurrences }
    return leastCommon to mostCommon
}

private fun String.apply(insertionRules: Map<Pair<Char, Char>, Char>, steps: Int): Map<Pair<Char, Char>, Long> {
    val numberOfOccurrencesByPair: MutableMap<Pair<Char, Char>, Long> = this
        .map { it }
        .windowed(2)
        .groupingBy { (a, b) -> a to b }
        .foldTo(mutableMapOf(), 0L) { accumulator, _ -> accumulator + 1 }

    repeat(steps) {
        numberOfOccurrencesByPair
            .toMap()
            .forEach { (a, b), occurrences ->
                if (occurrences < 1) return@forEach
                val result = insertionRules[a to b] ?: throw Error("No insertion rule found for $a$b")
                numberOfOccurrencesByPair[a to b] = numberOfOccurrencesByPair[a to b]!! - occurrences
                numberOfOccurrencesByPair[a to result] = (numberOfOccurrencesByPair[a to result] ?: 0) + occurrences
                numberOfOccurrencesByPair[result to b] = (numberOfOccurrencesByPair[result to b] ?: 0) + occurrences
            }
    }

    return numberOfOccurrencesByPair
}

private fun readInput(file: String): Pair<String, Map<Pair<Char, Char>, Char>> {
    val (polymerTemplateRaw, pairInsertionRulesRaw) = readStringInput(file)
        .filter { it.isNotBlank() }
        .withIndex()
        .partition { (index) -> index == 0 }
    val polymerTemplate = polymerTemplateRaw.first().value
    val pairInsertionRules = pairInsertionRulesRaw.associate { (_, value) ->
        val (pair, result) = value.split(" -> ")
        val (v1, v2) = pair.toCharArray()
        (v1 to v2) to result.first()
    }
    return polymerTemplate to pairInsertionRules
}
