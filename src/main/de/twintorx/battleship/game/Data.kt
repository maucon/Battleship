package main.de.twintorx.battleship.game

import main.de.twintorx.battleship.console.Color

class Grid(
        private val size: Int
) {
    val values: Array<Array<Cell>> = Array(size) { Array(size) { Cell.WATER } }

    operator fun get(x: Int, y: Int) = values[size - y - 1][x]

    operator fun set(x: Int, y: Int, value: Cell) {
        values[size - y - 1][x] = value
    }
}

enum class Cell(
        val value: String
) {
    WATER(" "),
    HIT_SHIP("${Color.RED}o${Color.RESET}"),
    HIT_NOTHING("x"),
    SHIP("░")
}

enum class Move {
    INVALID,
    HIT,
    SUNK,
    NO_HIT,
    GAME_OVER
}
