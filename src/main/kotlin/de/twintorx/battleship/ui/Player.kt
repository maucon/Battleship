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
        quit()
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
                Writer.print("\n${PlayerMessage.SUNK_SHIP}\n $shotPosition")
                shoot()
            }
            Move.GAME_OVER -> {
                Writer.clearConsole()
                remainingEnemyHitPoints--
                remainingEnemyShips--
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

        if (move != Move.INVALID) {
            Writer.clearConsole()
            printBoards()
        } else Writer.eraseLast(2)

        client.sendShotAnswer(move)

        when (move) {
            Move.HIT -> {
                Writer.print("\n${PlayerMessage.OPPONENT_HIT} $shotPosition\n")
                remainingOwnHitPoints--
                waitForTurn()
            }
            Move.SUNK -> {
                Writer.print("\n${PlayerMessage.OPPONENT_SUNK} $shotPosition\n")
                remainingOwnHitPoints--
                remainingOwnShips--
                waitForTurn()
            }
            Move.GAME_OVER -> {
                Writer.print("\n${PlayerMessage.LOSE}\n")
                remainingOwnHitPoints--
                remainingOwnShips--
                client.disconnect()
            }
            Move.NO_HIT -> {
                Writer.print("\n${PlayerMessage.OPPONENT_MISSED} $shotPosition\n")
                shoot()
            }
            else -> {
                waitForTurn()
            }
        }
    }

    private fun waitForTurn() {
        Writer.print("\n${PlayerMessage.WAITING_FOR_TURN}\n")
        updateGameBoard(client.waitForIncomingShot())
    }

    private fun printBoards(inGame: Boolean = true) {
        Writer.println("\n${PlayerMessage.AVAILABLE_SHIPS}")
        var line = ""
        Ship.values().forEach { line += "${it.color.paint(it.type)}(Size:${it.size})   " }
        Writer.println(line)
        Writer.println("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (trackBoard.size * 3)}${" " * 7}${PlayerMessage.TRACK_BOARD}")

        val remEnemyHitPoints = defaultValues(inGame, PlayerMessage.REMAINING_HIT_POINTS, Color.MAGENTA, remainingEnemyHitPoints.toString())
        val remOwnHitPoints = defaultValues(inGame, PlayerMessage.REMAINING_HIT_POINTS, Color.GREEN, remainingOwnHitPoints.toString())
        val remEnemyShips = defaultValues(inGame, PlayerMessage.REMAINING_SHIPS, Color.MAGENTA, remainingEnemyShips.toString())
        val remOwnShips = defaultValues(inGame, PlayerMessage.REMAINING_SHIPS, Color.GREEN, remainingOwnShips.toString())
        val enemy = defaultValues(inGame, PlayerMessage.ENEMY)
        val you = defaultValues(inGame, PlayerMessage.YOU)

        val lines = (gameBoard.getLines() zip trackBoard.getLines())
        lines.forEach {
            when (it) {
                lines[3] -> Writer.println("${it.first}      ${it.second}     $enemy")
                lines[5] -> Writer.println("${it.first}      ${it.second}     $remEnemyHitPoints")
                lines[7] -> Writer.println("${it.first}      ${it.second}     $remEnemyShips")
                lines[13] -> Writer.println("${it.first}      ${it.second}     $you")
                lines[15] -> Writer.println("${it.first}      ${it.second}     $remOwnHitPoints")
                lines[17] -> Writer.println("${it.first}      ${it.second}     $remOwnShips")
                else -> Writer.println("${it.first}      ${it.second}")
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

    private fun quit() {
        Writer.println("\nPress Enter to play again...")
        readLine()
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(size: Int): String = this.repeat(size)
