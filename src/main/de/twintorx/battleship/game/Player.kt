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
        println(PlayerMessage.PLACE_SHIPS)
        val ships = Ship.getStandardShipSet()

        while (ships.isNotEmpty()) {
            println(PlayerMessage.CHOOSE_SHIP)
            val option = input(ships.map { "${it.value.size}x${it.value[0].name}[${it.key}]" }
                    .joinToString(" ")) {
                InputRegex.SELECT_SHIP.matches(it) && ships[it.toInt()] != null
            }.toInt()

            with(ships[option]!!) {
                placeShip(this[0]).run { printBoards() }
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
            val placement = input(PlayerMessage.POSITION_SHIP) { InputRegex.PLACE_SHIP.matches(it) } // TODO change msg
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
        val position = input(PlayerMessage.SHOOT) { InputRegex.SHOOT_CELL.matches(it) }.toLowerCase() // TODO change msg
        val column = position[0].toInt() - 97 // 'a'.toInt()
        val line = position.substring(1).toInt() - 1

        val point = Point(column, line)
        val move = client.sendShot(point)

        updateTrackBoard(move, point)
    }

    private fun updateTrackBoard(move: Move, point: Point) {
        when (move) {
            Move.HIT -> {
                println(PlayerMessage.HIT_SHIP)
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                shoot()
            }
            Move.SUNK -> {
                println(PlayerMessage.SUNK_SHIP)
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                shoot()
            }
            Move.GAME_OVER -> {
                println(PlayerMessage.WIN)
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
            }
            Move.NO_HIT -> {
                println(PlayerMessage.HIT_NOTHING)
                trackBoard.mark(point.x, point.y, Cell.HIT_NOTHING).run { printBoards() }
                waitForTurn()
            }
            else -> {
                println(PlayerMessage.INVALID_MOVE)
                shoot()
            }
        }
    }

    private fun updateGameBoard(shot: Point) {
        val move = gameBoard.hit(shot.x, shot.y).also { printBoards() }
        client.sendShotAnswer(move)

        when (move) {
            Move.HIT -> {
                println(PlayerMessage.OPPONENT_HIT)
                waitForTurn()
            }
            Move.SUNK -> {
                println(PlayerMessage.OPPONENT_SUNK)
                waitForTurn()
            }
            Move.GAME_OVER -> {
                println(PlayerMessage.LOSE)
                client.disconnect()
            }
            Move.NO_HIT -> {
                println(PlayerMessage.OPPONENT_MISSED)
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

    private fun printBoards() {
        (trackBoard.getLines() zip gameBoard.getLines()).forEach { println(it.first + "\t" + it.second) }
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
