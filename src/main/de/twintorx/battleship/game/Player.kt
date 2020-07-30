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
        if (readLine()?.toLowerCase() == "y") {
            GlobalScope.launch {
                Server().start()
            }
            client = Client()
        } else {
            var address: String?
            do {
                println("Please enter the Server-Ip you want to connect to:")
                address = readLine()
            } while (address == null)
            client = Client(address)
        }
        prepare()
    }

    private fun prepare() {
        println("Please place your ships")
        //TODO place shits
        val turn = client.sendReadyGetTurn()
        if (turn) {
            shoot()
        } else {
            waitForTurn()
        }
    }

    private fun shoot() {
        println("Which cell do you want to shoot at?")
        var column: Int?
        do {
            println("Please enter a column:")
            column = readLine()?.get(0)?.toLowerCase()?.toInt()!! - 'a'.toInt()
        } while (column == null)

        var line: Int?
        do {
            println("Please enter a line:")
            line = readLine()?.toInt()
        } while (line == null)

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
                gameOver()
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
        val shot = client.waitForIncomingShot()
        updateGameBoard(shot)
    }

    private fun gameOver() {
        client.disconnect()
        println("Arrivederci")
    }
}

fun clearConsole() {
    print("\u001b[H\u001b[2J")
}
