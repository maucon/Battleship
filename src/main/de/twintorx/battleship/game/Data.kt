package main.de.twintorx.battleship.game


enum class Color(
        private val ansi: String
) {
    RESET("\u001B[0m"),
    WHITE("\u001B[37m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    BLUE("\u001B[34m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m"),
    PURPLE("\u001B[35m"),
    BLACK("\u001B[30m");

    override fun toString(): String {
        return ansi
    }
}

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
    HIT_SHIP("${Color.GREEN}o${Color.RESET}"),
    HIT_NOTHING("${Color.RED}x${Color.RESET}"),
    SHIP("░")
}

enum class Move(
        val message: String
) {
    INVALID("Invalid move!"),
    HIT("You hit!"),
    SUNK("You've sunk an enemy ship!"),
    NO_HIT("You missed!"),
    GAME_OVER("You won!")
}
