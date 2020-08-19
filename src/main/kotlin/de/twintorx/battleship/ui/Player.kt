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
        Console.printWelcome()

        if (Console.input("\n${PlayerMessage.HOST_SERVER}\n") { InputRegex.YES_OR_NO.matches(it) }.toLowerCase() == "y") {
            Console.clearConsole()

            val port = Console.input("${PlayerMessage.PORT}\n") { InputRegex.PORT.matches(it) }.run {
                if (isBlank()) 9999 else toInt()
            }
            Console.clearConsole()
            GlobalScope.launch {
                Server(port).start()
            }
            client = Client()
            client.tryConnect(port = port)

        } else {
            Console.clearConsole()
            client = Client().also {
                while (!it.tryConnect(Console.input("${PlayerMessage.SERVER_IP}\n").also { Console.clearConsole() },
                                Console.input("${PlayerMessage.PORT}\n") { str -> InputRegex.PORT.matches(str) }.run {
                                    if (isBlank()) 9999 else toInt()
                                })) {
                    Console.eraseLastLines(2)
                    continue
                }
            }
        }

        prepare()
        Console.printQuit()
        waitForRestart()
    }

    private fun prepare() {
        val ships = Ship.getStandardShipSet()

        Console.printPlaceShips(gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)

        while (ships.isNotEmpty()) {
            Console.printChooseShip()
            val option = Console.input(ships.map {
                val name = it.value[0].type
                "[${it.key}] ${it.value.size}x$name${" " * (11 - name.length)}(Size:${it.value[0].size})\n"
            }.joinToString("")) {
                InputRegex.SELECT_SHIP.matches(it) && ships.containsKey(it.toInt())
            }.toInt()

            Console.eraseLastLines(ships.size + 2)

            with(ships[option]!!) {
                placeShip(this[0]).run {
                    Console.printPlaceShips(gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)
                }
                removeAt(0)

                if (isEmpty()) {
                    ships.remove(option)
                }
            }
        }

        Console.printWaitingForPlacement()

        if (client.sendReadyGetTurn().also {
                    Console.printBoards(clearConsole = true, inGame = true, gameBoard = gameBoard, trackBoard = trackBoard,
                            enemyHP = remainingEnemyHitPoints, ownHP = remainingOwnHitPoints, remEnemyShips = remainingEnemyShips, remOwnShips = remainingOwnShips)
                }) shoot() else waitForTurn()
    }

    private fun placeShip(ship: Ship) {
        while (true) {
            val placement = Console.input("${PlayerMessage.POSITION_SHIP}\n") { InputRegex.PLACE_SHIP.matches(it) }
                    .toLowerCase()
            val startCol = placement[1].toInt() - 97 // 'a'.toInt()
            val startLine = placement.substring(2).toInt() - 1

            val points = hashSetOf<Point>().apply {
                when (placement[0] == 'h') {
                    true -> (startCol until startCol + ship.size).forEach { add(Point(it, startLine)) }
                    else -> (startLine until startLine + ship.size).forEach { add(Point(startCol, it)) }
                }
            }

            if (gameBoard.addShip(ship, points)) return else Console.eraseLastLines(3)
        }
    }

    private fun shoot() {
        val position = Console.input("\n${PlayerMessage.SHOOT}\n") { InputRegex.SHOOT_CELL.matches(it) }
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
                remainingEnemyHitPoints--
                trackBoard.mark(point.x, point.y, Mark.HIT_SHIP)

                Console.printShotUpdate(
                        gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips,
                        "\n${PlayerMessage.HIT_SHIP} $shotPosition\n")

                shoot()
            }
            Move.SUNK -> {
                remainingEnemyHitPoints--
                remainingEnemyShips--
                trackBoard.mark(point.x, point.y, Mark.HIT_SHIP)

                Console.printShotUpdate(
                        gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips,
                        "\n${PlayerMessage.SUNK_SHIP} $shotPosition\n")

                shoot()
            }
            Move.GAME_OVER -> {
                remainingEnemyHitPoints = 0
                remainingEnemyShips = 0

                Console.printWinScreen(true, gameBoard, client.sendBoardGetBoard(gameBoard),
                        remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)

                client.disconnect()
            }
            Move.NO_HIT -> {
                trackBoard.mark(point.x, point.y, Mark.HIT_NOTHING)

                Console.printShotUpdate(
                        gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips,
                        "\n${PlayerMessage.HIT_NOTHING} $shotPosition\n")

                waitForTurn()
            }
            else -> {
                Console.eraseLastLines(3)
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

                Console.printShotUpdate(
                        gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips,
                        "\n${PlayerMessage.OPPONENT_HIT} $shotPosition\n")

                waitForTurn()
            }
            Move.SUNK -> {
                remainingOwnHitPoints--
                remainingOwnShips--

                Console.printShotUpdate(
                        gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips,
                        "\n${PlayerMessage.OPPONENT_SUNK} $shotPosition\n")

                waitForTurn()
            }
            Move.GAME_OVER -> {
                remainingOwnHitPoints = 0
                remainingOwnShips = 0
                Console.printWinScreen(false, gameBoard, client.sendBoardGetBoard(gameBoard),
                        remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)
                client.disconnect()
            }
            Move.NO_HIT -> {

                Console.printShotUpdate(
                        gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips,
                        "\n${PlayerMessage.OPPONENT_MISSED} $shotPosition\n")

                shoot()
            }
            else -> {
                Console.eraseLastLines(2)
                waitForTurn()
            }
        }
    }

    private fun waitForTurn() {
        Console.printWaitForTurn()
        updateGameBoard(client.waitForIncomingShot())
    }


    private fun waitForRestart() {
        Console.printWaitRestart()
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(size: Int): String = this.repeat(size)
