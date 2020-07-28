package main.de.twintorx.battleship.game

import main.de.twintorx.battleship.console.Color

open class TrackBoard(
        private val size: Int
) {
    protected val grid: Array<Array<Cell>> = Array(size) { Array(size) { Cell.EMPTY } }

    open fun hit(x: Int, y: Int) {
        //TODO: check if shot hit other players ships
        setGridCell(x, y, if (getGridCell(x, y) == Cell.SHIP) Cell.HIT else Cell.FIRED)
    }

    protected fun setGridCell(x: Int, y: Int, cell: Cell) {
        grid[size - y - 1][x] = cell
    }

    protected fun getGridCell(x: Int, y: Int): Cell = grid[size - y - 1][x]

    override fun toString(): String {
        val len = size - 1
        val sizeLength = size.toString().length
        val space = " " * sizeLength
        val table = arrayListOf(" $space┌${"───┬" * len}───┐")
        val div = " $space├${"───┼" * len}───┤"
        grid.withIndex().forEach {
            val index = size - it.index
            table add "${" " * (sizeLength - index.toString().length)}$index │" +
                    "${it.value.joinToString("│") { cell ->
                        " ${cell.value} "
                    }}│" add div
        }
        table.removeLast()
        return (table add " $space└${"───┴" * len}───┘"
                add "   $space${(65..(64 + size)).map { it.toChar() }.joinToString("   ")}")
                .joinToString("\n")
    }
}

enum class Cell(
        val value: String
) {
    EMPTY(" "),
    HIT("${Color.GREEN}o${Color.RESET}"),
    FIRED("${Color.RED}x${Color.RESET}"),
    SHIP("░")
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(i: Int): String = this.repeat(i)

private infix fun ArrayList<String>.add(element: String): ArrayList<String> {
    add(element)
    return this
}
