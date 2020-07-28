package main.de.twintorx.battleship.game

import main.de.twintorx.battleship.console.Color
import java.util.*
import kotlin.collections.ArrayList

open class TrackBoard(
        private val size: Int
) {
    protected val grid: Grid = Grid(size)

    open fun hit(x: Int, y: Int) {
        //TODO: check if shot hit other players ships
        grid[x, y] = if (grid[x, y] == Cell.SHIP) Cell.HIT else Cell.FIRED
    }

    override fun toString(): String {
        val len = size - 1
        val sizeLength = size.toString().length
        val space = " " * sizeLength
        val table = arrayListOf(" $space┌${"───┬" * len}───┐")
        val div = " $space├${"───┼" * len}───┤"
        grid.values.withIndex().forEach {
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

class Grid(
        private val size: Int
) {
    val values: Array<Array<Cell>> = Array(size) { Array(size) { Cell.EMPTY } }

    operator fun get(x: Int, y: Int) = values[size - y - 1][x]

    operator fun set(x: Int, y: Int, value: Cell) {
        values[size - y - 1][x] = value
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
