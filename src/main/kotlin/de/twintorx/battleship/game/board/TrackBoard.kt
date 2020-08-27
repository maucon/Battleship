package de.twintorx.battleship.game.board

import de.twintorx.battleship.game.cell.Cell
import de.twintorx.battleship.game.cell.Mark
import de.twintorx.battleship.ui.Color
import java.awt.Point
import java.io.Serializable

open class TrackBoard(
        val size: Int = 10
): Serializable {

    protected val grid = Grid(size)
    protected val lastPoint = Point(-1, -1)

    fun mark(x: Int, y: Int, mark: Mark): Boolean {
        //fail when mark as water or ship ; or cell is already marked
        return if (grid[x, y].mark == Mark.HIT_NOTHING
                || grid[x, y].mark == Mark.HIT_SHIP)
            false
        else {
            grid[x, y] = Cell(mark)
            lastPoint.move(x, y)
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
                    "${it.value.withIndex().map { (xIndex, value) ->
                        val markValue = value.mark.value
                        if (xIndex == lastPoint.x && index - 1 == lastPoint.y) Color.CYAN.paint(markValue)
                        else if (value.mark == Mark.SHIP) value.ship!!.color.paint(markValue)
                        else if (value.mark == Mark.HIT_SHIP) Color.RED.paint(markValue)
                        else markValue
                    }.joinToString("│") { str ->
                        " $str "
                    }}│")
            table.add(div)
        }

        table.removeAt(table.size - 1)
        table.add(" $space└${"───┴" * len}───┘")
        table.add("   $space${(65..(64 + size)).joinToString("  ") { it.toChar() + " " }} ")

        return table
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(i: Int): String = this.repeat(i)
