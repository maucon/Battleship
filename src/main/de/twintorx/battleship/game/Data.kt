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
        private val value: String,
        private val shipType: ShipType = ShipType.NONE
) {
    WATER(" "),
    HIT_SHIP("${Color.RED}o${Color.RESET}"),
    HIT_NOTHING("x"),
    SHIP_CARRIER("░", ShipType.CARRIER),
    SHIP_BATTLESHIP("░", ShipType.BATTLESHIP),
    SHIP_CRUISER("░", ShipType.CRUISER),
    SHIP_SUBMARINE("░", ShipType.SUBMARINE),
    SHIP_DESTROYER("░", ShipType.DESTROYER);

    fun getString() = when (shipType.color) {
        null -> value
        else -> "${shipType.color}$value${Color.RESET}"
    }

    fun isShip() = when (this) {
        WATER, HIT_SHIP, HIT_NOTHING -> false
        else -> true
    }
}

enum class Move {
    INVALID,
    HIT,
    SUNK,
    NO_HIT,
    GAME_OVER
}

enum class ShipType(
        val value: String,
        val color: Color?
) {
    NONE("", null),
    CARRIER("Carrier", Color.YELLOW),
    BATTLESHIP("Battleship", Color.BLUE),
    CRUISER("Cruiser", Color.PURPLE),
    SUBMARINE("Submarine", Color.GREEN),
    DESTROYER("Destroyer", Color.RED)
}
