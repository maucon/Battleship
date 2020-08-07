package de.twintorx.battleship.game.board

import de.twintorx.battleship.game.cell.Cell
import de.twintorx.battleship.game.cell.Mark

open class TrackBoard(
        val size: Int = 10
) {
    protected val grid: Grid = Grid(size)

    fun mark(x: Int, y: Int, mark: Mark): Boolean {
        //fail when mark as water or ship ; or cell is already marked
        return if (mark == Mark.WATER
                || mark == Mark.SHIP
                || grid[x, y].mark == Mark.HIT_NOTHING
                || grid[x, y].mark == Mark.HIT_SHIP)
            false
        else {
            grid[x, y] = Cell(mark)
            true
        }
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
                    "${it.value.map { cell ->
                        if (cell.mark == Mark.SHIP) cell.ship!!.color.paint(cell.mark.value)
                        else cell.mark.value
                    }.joinToString("│") { str ->
                        " $str "
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
