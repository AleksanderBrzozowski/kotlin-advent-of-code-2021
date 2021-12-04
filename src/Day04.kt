fun main() {
    part1()
    part2()
}

private fun part1() {
    val testInput = readStringInput("Day04_test")
    val testNumbersToDraw = testInput.numbersToDraw()
    val testBingoBoards = testInput.bingoBoards()
    val (winningTestBingoBoard, lastTestDrawnNumber) = testBingoBoards.firstWin(testNumbersToDraw)
    check(winningTestBingoBoard.sumOfUnmarkedNumbers == 188)
    check(lastTestDrawnNumber == 24)

    val input = readStringInput("Day04")
    val numbersToDraw = input.numbersToDraw()
    val bingoBoards = input.bingoBoards()
    val (winningBingoBoard, lastDrawnNumbers) = bingoBoards.firstWin(numbersToDraw)
    println("Winning bingo board score: ${winningBingoBoard.sumOfUnmarkedNumbers * lastDrawnNumbers}")
}

private fun part2() {
    val testInput = readStringInput("Day04_test")
    val testNumbersToDraw = testInput.numbersToDraw()
    val testBingoBoards = testInput.bingoBoards()
    val (lastWinningTestBingoBoard, lastTestDrawnNumber) = testBingoBoards.lastWin(testNumbersToDraw)
    check(lastWinningTestBingoBoard.sumOfUnmarkedNumbers == 148)
    check(lastTestDrawnNumber == 13)

    val input = readStringInput("Day04")
    val numbersToDraw = input.numbersToDraw()
    val bingoBoards = input.bingoBoards()
    val (lastWinningBingoBoard, lastDrawnNumbers) = bingoBoards.lastWin(numbersToDraw)
    println("Last winning: bingo board score: ${lastWinningBingoBoard.sumOfUnmarkedNumbers * lastDrawnNumbers}")
}

private fun List<BingoBoard>.firstWin(numbersToDraw: List<Int>): Pair<BingoBoard, Int> {
    var boards = this
    numbersToDraw.forEach { numberToDraw ->
        boards = boards
            .map { board ->
                val boardAfterDraw = board.draw(numberToDraw)
                if (boardAfterDraw.isWin) {
                    return boardAfterDraw to numberToDraw
                }
                boardAfterDraw
            }
    }
    throw IllegalStateException("There is no winning board :(")
}

private fun List<BingoBoard>.lastWin(numbersToDraw: List<Int>): Pair<BingoBoard, Int> {
    var boards = this
    numbersToDraw.forEach { numberToDraw ->
        boards = boards
            .mapNotNull { board ->
                val boardAfterDraw = board.draw(numberToDraw)
                if (boards.size == 1 && boardAfterDraw.isWin) {
                    return boardAfterDraw to numberToDraw
                }
                if (boardAfterDraw.isWin) {
                    return@mapNotNull null
                }
                boardAfterDraw
            }
    }
    throw IllegalStateException("There is no winning board :(")
}

private fun List<String>.numbersToDraw(): List<Int> = first()
    .split(",")
    .map { it.toInt() }

private fun List<String>.bingoBoards(): List<BingoBoard> = drop(1) // drop header with numbers to draw
    .chunked(6)
    .map { rows ->
        rows
            .drop(1) // drop new line which acts as a divider
            .map { row ->
                row.split(" ", "  ").mapNotNull { if (it.isBlank()) null else it.toInt() }
            }
            .let { BingoBoard.of(it) }
    }

private class BingoBoard private constructor(private val rows: List<List<Pair<Int, Boolean>>>) {

    val isWin: Boolean by lazy { rows.isWin() || columns().isWin() }
    val sumOfUnmarkedNumbers: Int by lazy {
        rows.sumOf { row ->
            row.sumOf { (number, marked) -> if (marked) 0 else number }
        }
    }

    private fun columns(): Iterable<List<Pair<Int, Boolean>>> = sequence {
        repeat(rows.size) { index ->
            yield(rows.map { it[index] })
        }
    }.asIterable()

    private fun Iterable<List<Pair<Int, Boolean>>>.isWin() = this.any { it.all { (_, marked) -> marked } }

    fun draw(numberToDraw: Int): BingoBoard {
        val newRows = rows.map { row ->
            row.map { (number, marked) ->
                number to (marked || number == numberToDraw)
            }
        }
        return BingoBoard(newRows)
    }

    companion object {
        fun of(board: List<List<Int>>): BingoBoard {
            return BingoBoard(board.map { row -> row.map { column -> column to false } })
        }
    }
}
