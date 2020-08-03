package main.de.twintorx.battleship.game.board

import main.de.twintorx.battleship.game.Cell
import main.de.twintorx.battleship.game.Grid

open class TrackBoard(
        val size: Int = 10
) {
    protected val grid: Grid = Grid(size)

    fun mark(x: Int, y: Int, cell: Cell): Boolean {
        //fail when mark as water or ship or cell is already marked
        if (cell == Cell.WATER
                || cell == Cell.SHIP
                || grid[x, y] == Cell.HIT_NOTHING
                || grid[x, y] == Cell.HIT_SHIP)
            return false

        grid[x, y] = cell
        return true
    }

    fun getLines(): MutableList<String> {
        val len = size - 1
        val sizeLength = size.toString().length
        val space = " " * sizeLength
        val table = mutableListOf(" $space┌${"───┬" * len}───┐")
        val div = " $space├${"───┼" * len}───┤"

        grid.values.withIndex().forEach {
            val index = size - it.index
            val indexPadding = sizeLength - index.toString().length

            table.add("${" " * indexPadding}$index │" +
                    "${it.value.joinToString("│") { cell ->
                        " ${cell.value} "
                    }}│")
            table.add(div)
        }

        table.removeLast()
        table.add(" $space└${"───┴" * len}───┘")
        table.add("   $space${(65..(64 + size)).map { it.toChar() + " "}.joinToString("  ")}")

        return table
    }

    // ---------------- Extensions and Overloading ----------------
    private operator fun String.times(i: Int): String = this.repeat(i)
}
