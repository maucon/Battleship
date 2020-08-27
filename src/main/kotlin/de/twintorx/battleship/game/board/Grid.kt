package de.twintorx.battleship.game.board

import de.twintorx.battleship.game.cell.Cell
import java.io.Serializable

data class Grid(
        private val size: Int
): Serializable {

    val values: Array<Array<Cell>> = Array(size) { Array(size) { Cell() } }

    operator fun get(x: Int, y: Int) = values[size - y - 1][x]

    operator fun set(x: Int, y: Int, value: Cell) {
        values[size - y - 1][x] = value
    }
}
