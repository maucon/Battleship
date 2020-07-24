package main.de.twintorx.battleship

import main.de.twintorx.battleship.console.Color
import main.de.twintorx.battleship.console.print
import main.de.twintorx.battleship.game.GameBoard

fun main() {
    Color.values().forEach {
        print("${it.name}: DESTROY", it)
    }

    val gameBoard = GameBoard(6)
    (0 until gameBoard.size).forEach {
        (0..2).forEach { _ -> gameBoard.hit(it, (0 until gameBoard.size).random()) }
    }

    print(gameBoard)
    (1..26).forEach {
        print(GameBoard(it))
    }
}
