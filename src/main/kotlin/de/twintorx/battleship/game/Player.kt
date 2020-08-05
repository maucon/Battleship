package de.twintorx.battleship.game

import de.twintorx.battleship.connection.Client
import de.twintorx.battleship.connection.Server
import de.twintorx.battleship.console.InputRegex
import de.twintorx.battleship.console.PlayerMessage
import de.twintorx.battleship.console.Writer
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.TrackBoard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Point

class Player {
    private lateinit var client: Client
    private var gameBoard: GameBoard = GameBoard()
    private var trackBoard: TrackBoard = TrackBoard()

    fun connect() {
        Writer.printColored(PlayerMessage.WELCOME.toString())
        if (input(PlayerMessage.HOST_SERVER) { InputRegex.YES_OR_NO.matches(it) }.toLowerCase() == "y") {

            GlobalScope.launch {
                Server().start()
            }
            client = Client()
            client.tryConnect()

        } else {
            client = Client().also {
                while (!it.tryConnect(input(PlayerMessage.SERVER_IP))) {
                    continue
                }
            }
        }

        prepare()
    }

    private fun prepare() {
        Writer.printColored(PlayerMessage.PLACE_SHIPS.toString())
        val ships = Ship.getStandardShipSet()

        while (ships.isNotEmpty()) {
            Writer.printColored("\n${PlayerMessage.CHOOSE_SHIP}")
            val option = input(ships.map { "[${it.key}] ${it.value.size}x${it.value[0].type.value}(Size:${it.value[0].size})\n" }
                    .joinToString("")) {
                InputRegex.SELECT_SHIP.matches(it) && ships[it.toInt()] != null
            }.toInt()

            with(ships[option]!!) {
                placeShip(this[0]).run { printBoards() }
                removeAt(0)

                if (isEmpty()) {
                    ships.remove(option)
                }
            }
        }
        Writer.printColored(PlayerMessage.WAITING_FOR_PLACEMENT.toString())
        if (client.sendReadyGetTurn()) shoot() else waitForTurn()
    }

    private fun placeShip(ship: Ship) {
        while (true) {
            val placement = input(PlayerMessage.POSITION_SHIP) { InputRegex.PLACE_SHIP.matches(it) }
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
        val position = input(PlayerMessage.SHOOT) { InputRegex.SHOOT_CELL.matches(it) }
                .toLowerCase()
        val column = position[0].toInt() - 97 // 'a'.toInt()
        val line = position.substring(1).toInt() - 1

        val point = Point(column, line)
        val move = client.sendShot(point)

        updateTrackBoard(move, point)
    }

    private fun updateTrackBoard(move: Move, point: Point) {
        when (move) {
            Move.HIT -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                Writer.printColored(PlayerMessage.HIT_SHIP.toString())
                shoot()
            }
            Move.SUNK -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                Writer.printColored(PlayerMessage.SUNK_SHIP.toString())
                shoot()
            }
            Move.GAME_OVER -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                Writer.printColored(PlayerMessage.WIN.toString())
            }
            Move.NO_HIT -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_NOTHING).run { printBoards() }
                Writer.printColored(PlayerMessage.HIT_NOTHING.toString())
                waitForTurn()
            }
            else -> {
                Writer.printColored(PlayerMessage.INVALID_MOVE.toString())
                shoot()
            }
        }
    }

    private fun updateGameBoard(shot: Point) {
        val move = gameBoard.hit(shot.x, shot.y).also { printBoards() }
        client.sendShotAnswer(move)

        when (move) {
            Move.HIT -> {
                Writer.printColored(PlayerMessage.OPPONENT_HIT.toString())
                waitForTurn()
            }
            Move.SUNK -> {
                Writer.printColored(PlayerMessage.OPPONENT_SUNK.toString())
                waitForTurn()
            }
            Move.GAME_OVER -> {
                Writer.printColored(PlayerMessage.LOSE.toString())
                client.disconnect()
            }
            Move.NO_HIT -> {
                Writer.printColored(PlayerMessage.OPPONENT_MISSED.toString())
                shoot()
            }
            else -> {
                waitForTurn()
            }
        }
    }

    private fun waitForTurn() {
        Writer.printColored(PlayerMessage.WAITING_FOR_TURN.toString())
        updateGameBoard(client.waitForIncomingShot())
    }

    private fun printBoards() {
        Writer.printColored("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (trackBoard.size * 3)}${" " * 7}${PlayerMessage.TRACK_BOARD}")
        val lines = (gameBoard.getLines() zip trackBoard.getLines())
        lines.forEach {
            Writer.printColored("${it.first}\t${it.second}")
        }
    }

    private fun input(msg: String, validationMethod: (String) -> (Boolean) = { true }): String {
        while (true) {
            Writer.printColored(msg)

            val line = readLine() ?: continue
            if (!validationMethod(line) or line.isEmpty()) continue

            return line
        }
    }

    private fun input(msg: PlayerMessage, validationMethod: (String) -> (Boolean) = { true }): String {
        return input(msg.toString(), validationMethod)
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(size: Int): String = this.repeat(size)
