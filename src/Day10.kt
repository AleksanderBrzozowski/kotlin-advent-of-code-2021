fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInput = readInput("Day10_test")
    val testResult = testInput.incorrectCharacters().sumOf { it.toIncorrectCharacterScore() }
    check(testResult == 26397)
    val input = readInput("Day10")
    val result = input.incorrectCharacters().sumOf { it.toIncorrectCharacterScore() }
    println("Total syntax error score: $result")
}

private fun part2() {
    val testInput = readInput("Day10_test")
    val testResult = testInput.closingCharacters().middleScore()
    check(testResult == 288957L)
    val input = readInput("Day10")
    val result = input.closingCharacters().middleScore()
    println("Middle score: $result")
}

private fun List<String>.incorrectCharacters(): List<Char> =
    mapNotNull { line -> line.firstIncorrectCharacter() }

private fun String.firstIncorrectCharacter(): Char? {
    val openingCharacters = ArrayDeque<Char>()
    forEach { character ->
        when {
            character.isOpening() -> openingCharacters.addLast(character)
            else -> {
                val lastOpeningCharacter = openingCharacters.removeLast()
                if (lastOpeningCharacter != character.toOpeningCharacter()) {
                    return character
                }
            }
        }
    }
    return null
}

private fun List<String>.closingCharacters(): List<List<Char>> =
    mapNotNull { line -> line.closingCharactersForIncompleteLine() }

private fun String.closingCharactersForIncompleteLine(): List<Char>? {
    val openingCharacters = ArrayDeque<Char>()
    forEach { character ->
        when {
            character.isOpening() -> openingCharacters.addLast(character)
            else -> {
                val lastOpeningCharacter = openingCharacters.removeLast()
                if (lastOpeningCharacter != character.toOpeningCharacter()) {
                    return null
                }
            }
        }
    }
    return openingCharacters.reversed().map { it.toClosingCharacter() }
}

private fun List<List<Char>>.middleScore(): Long {
    return map { characters ->
        characters.fold<Char, Long>(0) { score, character -> score * 5 + character.toClosingCharacterScore() }
    }
        .sorted()[size / 2]
}

private fun Char.isOpening() = this in OPENING_CHARACTERS
private fun Char.toOpeningCharacter() = when (this) {
    '}' -> '{'
    ']' -> '['
    '>' -> '<'
    ')' -> '('
    else -> throw Error("Bad character: $this")
}

private fun Char.toClosingCharacter() = when (this) {
    '{' -> '}'
    '[' -> ']'
    '<' -> '>'
    '(' -> ')'
    else -> throw Error("Bad character: $this")
}

private fun Char.toIncorrectCharacterScore(): Int = when (this) {
    '}' -> 1197
    ']' -> 57
    '>' -> 25137
    ')' -> 3
    else -> throw Error("Bad character: $this")
}

private fun Char.toClosingCharacterScore(): Int = when (this) {
    '}' -> 3
    ']' -> 2
    '>' -> 4
    ')' -> 1
    else -> throw Error("Bad character: $this")
}

private val OPENING_CHARACTERS: Set<Char> = setOf('{', '[', '<', '(')

private fun readInput(file: String): List<String> = readStringInput(file)
