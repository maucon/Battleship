package main.de.twintorx.battleship

import main.de.twintorx.battleship.game.GameBoard
import main.de.twintorx.battleship.game.Ship
import main.de.twintorx.battleship.game.TrackBoard
import java.awt.Point

fun main() {
    val size = 10
    val gameBoard = GameBoard(size)
    val trackBoard = TrackBoard(size)

    gameBoard.addShip(Ship("meddl", 4), arrayListOf(Point(0, 1), Point(0, 2), Point(0, 3), Point(0, 4)))
    gameBoard.addShip(Ship("medd2", 2), arrayListOf(Point(5, 7), Point(5, 8)))
    gameBoard.addShip(Ship("medd3", 3), arrayListOf(Point(4, 2), Point(5, 2), Point(6, 2)))

    println("$gameBoard\n\nTracking:\n$trackBoard")

    println(gameBoard.hit(0, 1))
    println(gameBoard.hit(0, 2))
    println(gameBoard.hit(0, 3))
    println(gameBoard.hit(0, 4))

    println("$gameBoard\n\nTracking:\n$trackBoard")

    println(gameBoard.hit(0, 0))

    println("$gameBoard\n\nTracking:\n$trackBoard")
}
