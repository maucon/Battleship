package de.twintorx.battleship.ui

import de.twintorx.battleship.connection.Client
import de.twintorx.battleship.connection.Server
import de.twintorx.battleship.game.board.Cell
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.game.board.TrackBoard
import de.twintorx.battleship.game.ship.Ship
import de.twintorx.battleship.ui.io.InputRegex
import de.twintorx.battleship.ui.io.PlayerMessage
import de.twintorx.battleship.ui.io.Writer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Point

class Player {
    private lateinit var client: Client
    private var gameBoard: GameBoard = GameBoard()
    private var trackBoard: TrackBoard = TrackBoard()

    fun connect() {
        // TODO print fullscreen recommendation
        Writer.clearConsole()
        Writer.print("\n ${PlayerMessage.WELCOME}")
        Writer.print("${PlayerMessage.WELCOME_INFO}")
        if (input(PlayerMessage.HOST_SERVER) { InputRegex.YES_OR_NO.matches(it) }.toLowerCase() == "y") {
            Writer.clearConsole()
            GlobalScope.launch {
                Server().start()
            }
            client = Client()
            client.tryConnect()

        } else {
            Writer.clearConsole()
            client = Client().also {
                while (!it.tryConnect(input(PlayerMessage.SERVER_IP))) {
                    continue
                }
            }
        }

        prepare()
        Writer.print(PlayerMessage.QUIT.toString())
    }

    private fun prepare() {
        Writer.clearConsole()
        val ships = Ship.getStandardShipSet()
        Writer.print(PlayerMessage.PLACE_SHIPS.toString())
        printBoards()

        while (ships.isNotEmpty()) {
            Writer.print("\n${PlayerMessage.CHOOSE_SHIP}")
            val option = input(ships.map {
                val name = it.value[0].type.value
                "[${it.key}] ${it.value.size}x$name${" " * (11 - name.length)}(Size:${it.value[0].size})\n"
            }.joinToString("")) {
                InputRegex.SELECT_SHIP.matches(it) && ships.containsKey(it.toInt())
            }.toInt()

            Writer.eraseLast(ships.size + 3)

            with(ships[option]!!) {
                placeShip(this[0]).run {
                    Writer.clearConsole()
                    Writer.print(PlayerMessage.PLACE_SHIPS.toString())
                    printBoards()
                }
                removeAt(0)

                if (isEmpty()) {
                    ships.remove(option)
                }
            }
        }
        Writer.print(PlayerMessage.WAITING_FOR_PLACEMENT.toString())
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
                Writer.print(PlayerMessage.HIT_SHIP.toString())
                shoot()
            }
            Move.SUNK -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                Writer.print(PlayerMessage.SUNK_SHIP.toString())
                shoot()
            }
            Move.GAME_OVER -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_SHIP).run { printBoards() }
                Writer.print(PlayerMessage.WIN.toString())
            }
            Move.NO_HIT -> {
                trackBoard.mark(point.x, point.y, Cell.HIT_NOTHING).run { printBoards() }
                Writer.print(PlayerMessage.HIT_NOTHING.toString())
                waitForTurn()
            }
            else -> {
                Writer.print(PlayerMessage.INVALID_MOVE.toString())
                shoot()
            }
        }
    }

    private fun updateGameBoard(shot: Point) {
        val move = gameBoard.hit(shot.x, shot.y).also { printBoards() }
        client.sendShotAnswer(move)

        when (move) {
            Move.HIT -> {
                Writer.print(PlayerMessage.OPPONENT_HIT.toString())
                waitForTurn()
            }
            Move.SUNK -> {
                Writer.print(PlayerMessage.OPPONENT_SUNK.toString())
                waitForTurn()
            }
            Move.GAME_OVER -> {
                Writer.print(PlayerMessage.LOSE.toString())
                client.disconnect()
            }
            Move.NO_HIT -> {
                Writer.print(PlayerMessage.OPPONENT_MISSED.toString())
                shoot()
            }
            else -> {
                waitForTurn()
            }
        }
    }

    private fun waitForTurn() {
        Writer.print(PlayerMessage.WAITING_FOR_TURN.toString())
        updateGameBoard(client.waitForIncomingShot())
    }

    private fun printBoards() {
        Writer.print("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (trackBoard.size * 3)}${" " * 7}${PlayerMessage.TRACK_BOARD}")
        val lines = (gameBoard.getLines() zip trackBoard.getLines())
        lines.forEach {
            Writer.print("${it.first}\t${it.second}")
        }
    }

    private fun input(msg: String, validationMethod: (String) -> (Boolean) = { true }): String {
        while (true) {
            Writer.print(msg)

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
