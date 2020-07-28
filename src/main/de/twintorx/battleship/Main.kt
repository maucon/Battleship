package main.de.twintorx.battleship

import main.de.twintorx.battleship.game.GameBoard
import main.de.twintorx.battleship.game.Ship
import main.de.twintorx.battleship.game.TrackBoard
import java.awt.Point

fun main() {
    val size = 10
    val gameBoard = GameBoard(size)
    val trackBoard = TrackBoard(size)

    println(gameBoard.addShip(Ship("meddl", 4), hashSetOf(Point(0, 1), Point(0, 2), Point(0, 3), Point(0, 4))))
    println(gameBoard.addShip(Ship("medd2", 2), hashSetOf(Point(5, 7), Point(5, 8))))
    println(gameBoard.addShip(Ship("medd3", 3), hashSetOf(Point(4, 2), Point(5, 2), Point(6, 2))))
    println(gameBoard.addShip(Ship("medd4", 2), hashSetOf(Point(1, 1), Point(1, 1))))

    println("$gameBoard\n\nTracking:\n$trackBoard")

    println(gameBoard.hit(0, 1))
    println(gameBoard.hit(0, 2))
    println(gameBoard.hit(0, 3))
    println(gameBoard.hit(0, 4))

    println(gameBoard.hit(5, 7))
    println(gameBoard.hit(5, 8))

    println(gameBoard.hit(6, 2))
    println(gameBoard.hit(5, 2))
    println(gameBoard.hit(4, 2))

    println("$gameBoard\n\nTracking:\n$trackBoard")
}
