package de.twintorx.battleship.ui

import de.twintorx.battleship.connection.Client
import de.twintorx.battleship.connection.Server
import de.twintorx.battleship.game.Ship
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.game.board.TrackBoard
import de.twintorx.battleship.game.cell.Mark
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
    private var remainingEnemyShips = 15
    private var remainingOwnShips = 15
    private var remainingEnemyHitPoints = 44
    private var remainingOwnHitPoints = 44

    fun connect() {
        Writer.clearConsole()
        Writer.print("${PlayerMessage.WELCOME}\n${PlayerMessage.WELCOME_INFO}\n")

        if (input("\n${PlayerMessage.HOST_SERVER}\n") { InputRegex.YES_OR_NO.matches(it) }.toLowerCase() == "y") {
            Writer.clearConsole()

            val port = input("${PlayerMessage.PORT}\n") { InputRegex.PORT.matches(it) }.run {
                if (isBlank()) 9999 else toInt()
            }
            Writer.clearConsole()
            GlobalScope.launch {
                Server(port).start()
            }
            client = Client()
            client.tryConnect(port = port)

        } else {
            Writer.clearConsole()
            client = Client().also {
                while (!it.tryConnect(input("${PlayerMessage.SERVER_IP}\n").also { Writer.clearConsole() },
                                input("${PlayerMessage.PORT}\n") { str -> InputRegex.PORT.matches(str) }.run {
                                    if (isBlank()) 9999 else toInt()
                                })) {
                    Writer.eraseLast(2)
                    continue
                }
            }
        }

        prepare()
        Writer.print("\n${PlayerMessage.QUIT}\n")
        waitForRestart()
    }

    private fun prepare() {
        Writer.clearConsole()
        val ships = Ship.getStandardShipSet()
        Writer.print("${PlayerMessage.PLACE_SHIPS}\n")
        printBoards(false)

        while (ships.isNotEmpty()) {
            Writer.print("\n${PlayerMessage.CHOOSE_SHIP}\n")
            val option = input(ships.map {
                val name = it.value[0].type
                "[${it.key}] ${it.value.size}x$name${" " * (11 - name.length)}(Size:${it.value[0].size})\n"
            }.joinToString("")) {
                InputRegex.SELECT_SHIP.matches(it) && ships.containsKey(it.toInt())
            }.toInt()

            Writer.eraseLast(ships.size + 2)

            with(ships[option]!!) {
                placeShip(this[0]).run {
                    Writer.clearConsole()
                    Writer.print("${PlayerMessage.PLACE_SHIPS}\n")
                    printBoards(false)
                }
                removeAt(0)

                if (isEmpty()) {
                    ships.remove(option)
                }
            }
        }
        Writer.print("\n${PlayerMessage.WAITING_FOR_PLACEMENT}\n")
        if (client.sendReadyGetTurn().also {
                    Writer.clearConsole()
                    printBoards()
                }) shoot() else waitForTurn()
    }

    private fun placeShip(ship: Ship) {
        while (true) {
            val placement = input("${PlayerMessage.POSITION_SHIP}\n") { InputRegex.PLACE_SHIP.matches(it) }
                    .toLowerCase()
            val startCol = placement[1].toInt() - 97 // 'a'.toInt()
            val startLine = placement.substring(2).toInt() - 1

            val points = hashSetOf<Point>().apply {
                when (placement[0] == 'h') {
                    true -> (startCol until startCol + ship.size).forEach { add(Point(it, startLine)) }
                    else -> (startLine until startLine + ship.size).forEach { add(Point(startCol, it)) }
                }
            }

            if (gameBoard.addShip(ship, points)) return else Writer.eraseLast(3)
        }
    }

    private fun shoot() {
        val position = input("\n${PlayerMessage.SHOOT}\n") { InputRegex.SHOOT_CELL.matches(it) }
                .toLowerCase()
        val column = position[0].toInt() - 97 // 'a'.toInt()
        val line = position.substring(1).toInt() - 1

        val point = Point(column, line)
        val move = client.sendShot(point)

        updateTrackBoard(move, point)
    }

    private fun updateTrackBoard(move: Move, point: Point) {
        val shotPosition = Color.CYAN.paint("[${(point.x + 97).toChar()}${point.y + 1}]!")

        when (move) {
            Move.HIT -> {
                Writer.clearConsole()
                remainingEnemyHitPoints--
                trackBoard.mark(point.x, point.y, Mark.HIT_SHIP).run { printBoards() }
                Writer.print("\n${PlayerMessage.HIT_SHIP} $shotPosition\n")
                shoot()
            }
            Move.SUNK -> {
                Writer.clearConsole()
                remainingEnemyHitPoints--
                remainingEnemyShips--
                trackBoard.mark(point.x, point.y, Mark.HIT_SHIP).run { printBoards() }
                Writer.print("\n${PlayerMessage.SUNK_SHIP} $shotPosition\n")
                shoot()
            }
            Move.GAME_OVER -> {
                Writer.clearConsole()
                remainingEnemyHitPoints = 0
                remainingEnemyShips = 0
                trackBoard.mark(point.x, point.y, Mark.HIT_SHIP).run { printBoards() }
                Writer.print("\n${PlayerMessage.WIN}\n")
            }
            Move.NO_HIT -> {
                Writer.clearConsole()
                trackBoard.mark(point.x, point.y, Mark.HIT_NOTHING).run { printBoards() }
                Writer.print("\n${PlayerMessage.HIT_NOTHING} $shotPosition\n")
                waitForTurn()
            }
            else -> {
                Writer.eraseLast(3)
                shoot()
            }
        }
    }

    private fun updateGameBoard(shot: Point) {
        val move = gameBoard.hit(shot.x, shot.y)
        val shotPosition = Color.CYAN.paint("[${(shot.x + 97).toChar()}${shot.y + 1}]!")

        client.sendShotAnswer(move)

        when (move) {
            Move.HIT -> {
                remainingOwnHitPoints--
                Writer.clearConsole().also {
                    printBoards()
                    Writer.print("\n${PlayerMessage.OPPONENT_HIT} $shotPosition\n")
                }
                waitForTurn()
            }
            Move.SUNK -> {
                remainingOwnHitPoints--
                remainingOwnShips--
                Writer.clearConsole().also {
                    printBoards()
                    Writer.print("\n${PlayerMessage.OPPONENT_SUNK} $shotPosition\n")
                }
                waitForTurn()
            }
            Move.GAME_OVER -> {
                remainingOwnHitPoints = 0
                remainingOwnShips = 0
                Writer.clearConsole().also {
                    printBoards()
                    Writer.print("\n${PlayerMessage.LOSE}\n")
                }
                client.disconnect()
            }
            Move.NO_HIT -> {
                Writer.clearConsole().also {
                    printBoards()
                    Writer.print("\n${PlayerMessage.OPPONENT_MISSED} $shotPosition\n")
                }
                shoot()
            }
            else -> {
                Writer.eraseLast(2)
                waitForTurn()
            }
        }
    }

    private fun waitForTurn() {
        Writer.print("\n${PlayerMessage.WAITING_FOR_TURN}\n")
        updateGameBoard(client.waitForIncomingShot())
    }

    private fun printBoards(inGame: Boolean = true) {

        with(Writer) {
            println("\n${PlayerMessage.AVAILABLE_SHIPS}")
            println(Ship.values().joinToString("   ") {
                "${it.color.paint(it.type)}(Size:${it.size})"
            })
            println("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (trackBoard.size * 3)}${" " * 7}${PlayerMessage.TRACK_BOARD}")

            val remainingEnemyHP = defaultValues(inGame, PlayerMessage.REMAINING_HIT_POINTS, Color.RED, remainingEnemyHitPoints.toString())
            val remainingOwnHP = defaultValues(inGame, PlayerMessage.REMAINING_HIT_POINTS, Color.GREEN, remainingOwnHitPoints.toString())
            val remainingEnemyShips = defaultValues(inGame, PlayerMessage.REMAINING_SHIPS, Color.RED, remainingEnemyShips.toString())
            val remainingOwnShips = defaultValues(inGame, PlayerMessage.REMAINING_SHIPS, Color.GREEN, remainingOwnShips.toString())

            val lines = (gameBoard.getLines() zip trackBoard.getLines())
            lines.withIndex().forEach { (index, value) ->
                when (index) {
                    3 -> println("${value.first}      ${value.second}     ${defaultValues(inGame, PlayerMessage.ENEMY)}")
                    5 -> println("${value.first}      ${value.second}     $remainingEnemyHP")
                    7 -> println("${value.first}      ${value.second}     $remainingEnemyShips")
                    13 -> println("${value.first}      ${value.second}     ${defaultValues(inGame, PlayerMessage.YOU)}")
                    15 -> println("${value.first}      ${value.second}     $remainingOwnHP")
                    17 -> println("${value.first}      ${value.second}     $remainingOwnShips")
                    else -> println("${value.first}      ${value.second}")
                }
            }
        }
    }

    private fun defaultValues(inGame: Boolean, playerMessage: PlayerMessage, color: Color = Color.WHITE, counter: String = ""): String =
            if (inGame) "$playerMessage ${color.paint(counter)}" else ""

    private fun input(msg: String, validationMethod: (String) -> (Boolean) = { true }): String {
        while (true) {
            Writer.print(msg)

            val line = readLine() ?: continue
            if (!validationMethod(line)) {
                val lines = msg.split("\n").size
                println(lines)
                Writer.eraseLast(lines + 1)
                continue
            }

            return line
        }
    }

    private fun waitForRestart() {
        Writer.println("\nPress Enter to play again...")
        readLine()
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(size: Int): String = this.repeat(size)
