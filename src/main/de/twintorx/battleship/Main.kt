package main.de.twintorx.battleship

import main.de.twintorx.battleship.console.Color
import main.de.twintorx.battleship.console.print
import main.de.twintorx.battleship.game.GameBoard

fun main() {
    Color.values().forEach {
        print("$it: DESTROY", it)
    }

    val gameBoard = GameBoard()
    (0..9).forEach {
        gameBoard.hit(it, (0..9).random())
        gameBoard.hit(it, (0..9).random())
        gameBoard.hit(it, (0..9).random())
    }

    print(gameBoard)
}
