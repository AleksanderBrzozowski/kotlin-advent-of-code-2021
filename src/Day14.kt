fun main() {
    part1()
    part2()
}

private fun part1() {
    val (testPolymerTemplate, testPairInsertionRules) = readInput("Day14_test")
    val testResult = testPolymerTemplate.apply(testPairInsertionRules, steps = 10)
    check(testResult.length == 3073)
    val (testLeastCommon, testMostCommon) = testResult.occurrences()
    check(testMostCommon - testLeastCommon == 1588L)
    val (polymerTemplate, pairInsertionRules) = readInput("Day14")
    val result = polymerTemplate.apply(pairInsertionRules, steps = 10)
    val (lestCommon, mostCommon) = result.occurrences()
    println("Subtraction of most common and least common is: ${mostCommon - lestCommon}")
}

private fun part2() {
    val (testPolymerTemplate, testPairInsertionRules) = readInput("Day14_test")
    val testResult = testPolymerTemplate.apply(testPairInsertionRules, steps = 40)
    val (testLeastCommon, testMostCommon) = testResult.occurrences()
    check(testMostCommon - testLeastCommon == 2188189693529L)
    val (polymerTemplate, pairInsertionRules) = readInput("Day14")
    val result = polymerTemplate.apply(pairInsertionRules, steps = 40)
    val (lestCommon, mostCommon) = result.occurrences()
    println("Subtraction of most common and least common is: ${mostCommon - lestCommon}")
}

private fun String.occurrences(): Pair<Long, Long> {
    val numberOfOccurrencesByLetter = groupingBy { it }.fold(0L) { acc, _ -> acc + 1 }
    val leastCommon = numberOfOccurrencesByLetter.minOf { (_, occurrences) -> occurrences }
    val mostCommon = numberOfOccurrencesByLetter.maxOf { (_, occurrences) -> occurrences }
    return leastCommon to mostCommon
}

private fun String.apply(insertionRules: Map<Pair<Char, Char>, Char>, steps: Int): String {
    return (1..steps).fold(this.map { it }) { polymer, _ ->
        polymer.windowed(2)
            .flatMapIndexed { index, (a, b) ->
                val result = insertionRules[a to b] ?: throw Error("No insertion rule found for $a$b")
                if (index != polymer.size - 2) listOf(a, result) else listOf(a, result, b)
            }
    }.joinToString(separator = "")
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
