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
import kotlin.random.Random

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
        var ships = Ship.getStandardShipSet()

        // place ships manually
        if (Console.clearConsole().run { Console.input("${PlayerMessage.PLACE_OPTIONS}\n") { InputRegex.YES_OR_NO.matches(it) }.toLowerCase() == "y" }) {

            Console.printPlaceShips(gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)

            val shipStack = mutableListOf<Pair<Ship, HashSet<Point>>>()
            while (ships.isNotEmpty()) {

                Console.printChooseShip()

                val option = Console.input(ships.map {
                    with(it.value[0].type) {
                        "[${it.key}] ${it.value.size}x$this${" " * (11 - length)}(Size:${it.value[0].size})\n"
                    }
                }.joinToString("") + if (shipStack.isNotEmpty()) "${PlayerMessage.UNDO_OPTION}\n" else "") {
                    InputRegex.SELECT_SHIP.matches(it) && ((ships.containsKey(it.toInt())) || (it.toInt() == 6 && shipStack.isNotEmpty()))
                }.toInt()

                Console.eraseLastLines(ships.size + 2)

                if (option == 6 && shipStack.isNotEmpty()) {
                    val (ship, points) = shipStack[shipStack.size - 1]

                    if (gameBoard.removeShip(points)) {
                        shipStack.removeAt(shipStack.size - 1)

                        with(ship.ordinal + 1) {
                            ships[this]?.add(ship) ?: run {
                                ships[this] = mutableListOf(ship)
                                ships = ships.toSortedMap()
                            }
                        }
                        Console.printPlaceShips(gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)
                    }
                } else {
                    ships[option]?.let {
                        val addedPair = placeShip(it[0])
                        if (addedPair != null) {
                            shipStack.add(addedPair)
                        }
                        Console.printPlaceShips(gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)

                        it.removeAt(0)

                        if (it.isEmpty()) {
                            ships.remove(option)
                        }
                    }
                }
            }
        } else {
            // place ships random
            placeShipsRandom()
            Console.printRandomPlacement(gameBoard, trackBoard, remainingEnemyHitPoints, remainingOwnHitPoints, remainingEnemyShips, remainingOwnShips)
        }

        Console.printWaitingForPlacement()

        if (client.sendReadyGetTurn().also {
                    Console.printBoards(clearConsole = true, isInGame = true, gameBoard = gameBoard, trackBoard = trackBoard,
                            enemyHP = remainingEnemyHitPoints, ownHP = remainingOwnHitPoints, remEnemyShips = remainingEnemyShips, remOwnShips = remainingOwnShips)
                }) {
            shoot()
        } else {
            waitForTurn()
        }
    }

    private fun placeShip(ship: Ship): Pair<Ship, HashSet<Point>>? {
        while (true) {
            val placement = Console.input("${PlayerMessage.POSITION_SHIP}\n") { InputRegex.PLACE_SHIP.matches(it) }
                    .toLowerCase()
            val points = generateShipPoints(
                    placement[0] == 'h',
                    placement[1].toInt() - 97,
                    placement.substring(2).toInt() - 1,
                    ship
            )

            return if (gameBoard.addShip(ship, points))
                Pair(ship, points)
            else {
                Console.eraseLastLines(3)
                null
            }
        }
    }

    private fun placeShipsRandom() {
        val points = mutableListOf<Point>().apply {
            repeat(trackBoard.size) { x ->
                repeat(trackBoard.size) { y ->
                    add(Point(x, y))
                }
            }
            shuffle()
        }

        Ship.getStandardShipSet().flatMap { it.value }.forEach {
            val iterator = points.iterator()
            while (iterator.hasNext()) {
                val point = iterator.next()
                val direction = Random.nextBoolean()

                var shipPoints = generateShipPoints(direction, point.x, point.y, it)
                if (gameBoard.addShip(it, shipPoints)) {
                    points.removeAll(shipPoints)
                    break
                }

                shipPoints = generateShipPoints(!direction, point.x, point.y, it)
                if (gameBoard.addShip(it, shipPoints)) {
                    points.removeAll(shipPoints)
                    break
                }
            }
        }
    }

    private fun generateShipPoints(direction: Boolean, x: Int, y: Int, ship: Ship) = hashSetOf<Point>().apply {
        if (direction) (x until x + ship.size).forEach { add(Point(it, y)) } else (y until y + ship.size).forEach { add(Point(x, it)) }
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
