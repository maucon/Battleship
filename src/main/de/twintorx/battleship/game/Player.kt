package main.de.twintorx.battleship.game

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server
import java.awt.Point

class Player {
    private lateinit var client: Client
    private var gameBoard: GameBoard = GameBoard()
    private var trackBoard: TrackBoard = TrackBoard()

    fun connect() {
        println("Do you want to host a server? [Y]/[N]")
        client = if (readLine()?.toLowerCase() == "y") {
            GlobalScope.launch {
                Server().start()
            }
            Client()

        } else Client(input("Please enter the Server-Ip you want to connect to:"))

        prepare()
    }

    private fun prepare() {
        println("Please place your ships")
        //TODO place shits
        if (client.sendReadyGetTurn()) shoot() else waitForTurn()
    }

    private fun shoot() {
        println("Which cell do you want to shoot at?")
        val column = input("Please enter a column:")[0].toInt() - 97 // 'a'.toInt()
        val line = input("Please enter a line:").toInt()

        val point = Point(column, line - 1)
        val move = client.sendShot(point)

        updateTrackBoard(move, point)
    }

    private fun updateTrackBoard(move: Move, point: Point) {
        when (move) {
            Move.HIT -> {
                println("You've hit a ship!")
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { println(trackBoard) }
                shoot()
            }
            Move.SUNK -> {
                println("You've sunk a ship!")
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { println(trackBoard) }
                shoot()
            }
            Move.GAME_OVER -> {
                println("You've sunk the last ship and won the game!")
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { println(trackBoard) }
            }
            Move.NO_HIT -> {
                println("You hit nothing")
                trackBoard.mark(point.x, point.y, Cell.HIT_NOTHING).run { println(trackBoard) }
                waitForTurn()
            }
            else -> {
                println("Your move was invalid.")
                shoot()
            }
        }
    }

    private fun updateGameBoard(shot: Point) {
        val move = gameBoard.hit(shot.x, shot.y).also { println(gameBoard) }
        client.sendShotAnswer(move)

        when (move) {
            Move.HIT -> {
                println("Your opponent hit!")
                waitForTurn()
            }
            Move.SUNK -> {
                println("Your opponent sunk one of your ships!")
                waitForTurn()
            }
            Move.GAME_OVER -> {
                println("You lost :(")
                client.disconnect()
            }
            Move.NO_HIT -> {
                println("Your opponent missed a shot")
                shoot()
            }
            else -> {
                waitForTurn()
            }
        }
    }

    private fun waitForTurn() {
        updateGameBoard(client.waitForIncomingShot())
    }

    private fun input(msg: String): String {
        println(msg)
        return readLine() ?: input(msg)
    }
}
