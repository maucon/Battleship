package de.twintorx.battleship.game.board

import de.twintorx.battleship.game.Cell
import de.twintorx.battleship.game.Grid

open class TrackBoard(
        val size: Int = 10
) {
    protected val grid: Grid = Grid(size)

    fun mark(x: Int, y: Int, cell: Cell): Boolean {
        //fail when mark as water or ship or cell is already marked
        if (cell == Cell.WATER
                || cell.isShip()
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
                        " $cell "
                    }}│")
            table.add(div)
        }

        table.removeAt(table.size - 1)
        table.add(" $space└${"───┴" * len}───┘")
        table.add("   $space${(65..(64 + size)).joinToString("  ") { it.toChar() + " " }}")

        return table
    }

}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(i: Int): String = this.repeat(i)
