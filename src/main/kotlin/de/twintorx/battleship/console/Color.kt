package de.twintorx.battleship.console

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
