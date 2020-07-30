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

enum class Cell(
        val value: String
) {
    WATER(" "),
    HIT_SHIP("${Color.RED}o${Color.RESET}"),
    HIT_NOTHING("x"),
    SHIP("░")
}

enum class Move() {
    INVALID,
    HIT,
    SUNK,
    NO_HIT,
    GAME_OVER
}
