package main.de.twintorx.battleship.game

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server
import main.de.twintorx.battleship.console.InputRegex
import main.de.twintorx.battleship.console.PlayerMessage
import main.de.twintorx.battleship.game.board.GameBoard
import main.de.twintorx.battleship.game.board.TrackBoard
import java.awt.Point

class Player {
    private lateinit var client: Client
    private var gameBoard: GameBoard = GameBoard()
    private var trackBoard: TrackBoard = TrackBoard()

    fun connect() {
        client = if (input(PlayerMessage.HOST_SERVER) { InputRegex.YES_OR_NO.matches(it) }.toLowerCase() == "y") {

            GlobalScope.launch {
                Server().start()
            }
            Client()

        } else Client(input(PlayerMessage.SERVER_IP))

        prepare()
    }

    private fun prepare() {
        println("Please place your ships.")
        val ships = Ship.getStandardShipSet()

        while (ships.isNotEmpty()) {
            println("Choose your ship:")
            val option = input(ships.map { "${it.value.size}x${it.value[0].name}[${it.key}]" }
                    .joinToString(" ")) {
                InputRegex.SELECT_SHIP.matches(it) && ships[it.toInt()] != null
            }.toInt()

            with(ships[option]!!) {
                placeShip(this[0]).run { println(gameBoard) }
                removeFirst()

                if (isEmpty()) {
                    ships.remove(option)
                }
            }
        }
        if (client.sendReadyGetTurn()) shoot() else waitForTurn()
    }

    private fun placeShip(ship: Ship) {
        while (true) {
            val placement = input("Pls enter ship position e.g: ha1 :") { InputRegex.PLACE_SHIP.matches(it) }
                    .toLowerCase()
            val startCol = placement[1].toInt() - 97 // 'a'.toInt()
            val startLine = placement.substring(2).toInt() - 1

            val points = hashSetOf<Point>().apply {
                when (placement[0] == 'h') {
                    true -> (startCol until startCol + ship.size).forEach { add(Point(it, startLine)) }
                    else -> (startLine until startLine + ship.size).forEach { add(Point(startCol, it)) }
                }
            }

            if (gameBoard.addShip(ship, points)) return
        }
    }

    private fun shoot() {
        val position = input("Which cell do you want to shoot at e.g: A1 ?") { InputRegex.SHOOT_CELL.matches(it) }
        val column = position[0].toInt() - 97 // 'a'.toInt()
        val line = position.substring(1).toInt() - 1

        val point = Point(column, line)
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

    private fun input(msg: String, validationMethod: (String) -> (Boolean) = { true }): String {
        while (true) {
            println(msg)

            val line = readLine() ?: continue
            if (!validationMethod(line) or line.isEmpty()) continue

            return line
        }
    }

    private fun input(msg: PlayerMessage, validationMethod: (String) -> (Boolean) = { true }): String {
        return input(msg.toString(), validationMethod)
    }
}
