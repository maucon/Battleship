package main.de.twintorx.battleship.game

import main.de.twintorx.battleship.console.Color

class GameBoard(
        val size: Int = 10
) {
    private val grid: Array<Array<Boolean>> = Array(size) { Array(size) { false } }

    fun hit(x: Int, y: Int) {
        grid[size - y - 1][x] = true
    }

    override fun toString(): String {
        val len = size - 1
        val sizeLength = size.toString().length
        val space = " " * sizeLength
        val table = arrayListOf(" $space┌${"───┬" * len}───┐")
        val div = " $space├${"───┼" * len}───┤"
        grid.withIndex().forEach {
            val index = size - it.index
            table add "${" " * (sizeLength - index.toString().length)}$index │" +
                    "${it.value.joinToString("│") { cell ->
                        " ${if (cell) "${Color.RED}x" else "${Color.GREEN} "}${Color.RESET} "
                    }}│" add div
        }
        table.removeLast()
        return (table add " $space└${"───┴" * len}───┘"
                add "   $space${(65..(64 + size)).map { it.toChar() }.joinToString("   ")}")
                .joinToString("\n")
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(i: Int): String = this.repeat(i)

private infix fun ArrayList<String>.add(element: String): ArrayList<String> {
    add(element)
    return this
}
