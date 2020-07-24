package main.de.twintorx.battleship.game

import main.de.twintorx.battleship.console.Color

class GameBoard(
        val size: Int = 10
) {
    private val grid: Array<Array<Boolean>> = Array(10) { Array(10) { false } }

    fun hit(x: Int, y: Int) {
        grid[size - y - 1][x] = true
    }

    override fun toString(): String {
        var print = ""

        grid.forEach {
            it.forEach { cell ->
                print += (if (cell) Color.RED.ansi + "1" else Color.GREEN.ansi + "0") + " "
            }
            print += "\n"
        }

        return print
    }
}
