package main.de.twintorx.battleship

import main.de.twintorx.battleship.console.print
import main.de.twintorx.battleship.game.GameBoard
import main.de.twintorx.battleship.game.Ship
import java.awt.Point

fun main() {
    val gameBoard = GameBoard(10)
    gameBoard.addShip(Ship("1", 3), arrayListOf(Point(0, 0), Point(1, 0), Point(2, 0)))
    gameBoard.addShip(Ship("2", 3), arrayListOf(Point(4, 3), Point(4, 2), Point(4, 1)))

    (0 until gameBoard.size).forEach {
        (0..5).forEach { _ -> gameBoard.hit(it, (0 until gameBoard.size).random()) }
    }
    print(gameBoard)
}
