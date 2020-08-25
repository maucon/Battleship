package de.twintorx.battleship.ui

import de.twintorx.battleship.game.Ship
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.TrackBoard
import de.twintorx.battleship.ui.io.PlayerMessage
import de.twintorx.battleship.ui.io.Writer

object Console {

    fun printWelcome() {
        clearConsole()
        Writer.print("${PlayerMessage.WELCOME}\n${PlayerMessage.WELCOME_INFO}\n")
    }

    fun printChooseShip() {
        Writer.print("\n${PlayerMessage.CHOOSE_SHIP}\n")
    }

    fun printPlaceShips(gameBoard: GameBoard, trackBoard: TrackBoard, enemyHP: Int, ownHP: Int, remEnemyShips: Int, remOwnShips: Int) {
        Writer.clearConsole()
        Writer.print("${PlayerMessage.PLACE_SHIPS}\n")
        printBoards(clearConsole = false, isInGame = false, gameBoard = gameBoard, trackBoard = trackBoard,
                enemyHP = enemyHP, ownHP = ownHP, remEnemyShips = remEnemyShips, remOwnShips = remOwnShips)
    }

    fun printWaitingForPlacement() {
        Writer.print("\n${PlayerMessage.WAITING_FOR_PLACEMENT}\n")
    }

    // prints boards and message for shoot/receive shot
    fun printShotUpdate(gameBoard: GameBoard, trackBoard: TrackBoard, enemyHP: Int, ownHP: Int, remEnemyShips: Int, remOwnShips: Int, message: String) {
        printBoards(true, isInGame = true, gameBoard = gameBoard, trackBoard = trackBoard, enemyHP = enemyHP, ownHP = ownHP, remEnemyShips = remEnemyShips, remOwnShips = remOwnShips)
        Writer.print(message)
    }

    fun printBoards(clearConsole: Boolean, isInGame: Boolean, gameBoard: GameBoard, trackBoard: TrackBoard, enemyHP: Int, ownHP: Int, remEnemyShips: Int, remOwnShips: Int) {
        if (clearConsole) clearConsole()
        printAvailableShips()
        Writer.println("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (trackBoard.size * 3)}${" " * 9}${PlayerMessage.TRACK_BOARD}")
        printBoardWithStats(gameBoard, trackBoard, isInGame, enemyHP, ownHP, remEnemyShips, remOwnShips)
    }

    fun printWaitForTurn() {
        Writer.print("\n${PlayerMessage.WAITING_FOR_TURN}\n")
    }

    fun printWinScreen(hasWon: Boolean, gameBoard: GameBoard, enemyBoard: GameBoard, enemyHP: Int, ownHP: Int, remEnemyShips: Int, remOwnShips: Int) {
        clearConsole()

        printAvailableShips()
        Writer.println("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (gameBoard.size * 3)}${" " * 9}${PlayerMessage.ENEMY_BOARD}")
        printBoardWithStats(gameBoard, enemyBoard, true, enemyHP, ownHP, remEnemyShips, remOwnShips)

        Writer.print(if (hasWon) "\n${PlayerMessage.WIN}\n" else "\n${PlayerMessage.LOSE}\n")
    }

    fun printQuit() {
        Writer.print("\n${PlayerMessage.QUIT}\n")
    }

    fun printWaitRestart() {
        Writer.println("\nPress Enter to play again...")
        readLine()
    }

    private fun printAvailableShips() {
        Writer.println("${PlayerMessage.AVAILABLE_SHIPS}")
        Writer.println(Ship.getStandardShipSet().values.joinToString("   ") { ships ->
            "${ships.size}x${ships[0].color.paint(ships[0].type)}(Size:${ships[0].size})"
        }
        )
    }

    private fun printBoardWithStats(board1: GameBoard, board2: TrackBoard, isInGame: Boolean, enemyHP: Int, ownHP: Int, remEnemyShips: Int, remOwnShips: Int) {
        val remainingEnemyHP = defaultValues(isInGame, PlayerMessage.REMAINING_HIT_POINTS, Color.RED, enemyHP.toString())
        val remainingOwnHP = defaultValues(isInGame, PlayerMessage.REMAINING_HIT_POINTS, Color.GREEN, ownHP.toString())
        val remainingEnemyShips = defaultValues(isInGame, PlayerMessage.REMAINING_SHIPS, Color.RED, remEnemyShips.toString())
        val remainingOwnShips = defaultValues(isInGame, PlayerMessage.REMAINING_SHIPS, Color.GREEN, remOwnShips.toString())

        (board1.getLines() zip board2.getLines()).withIndex().forEach { (index, value) ->
            when (index) {
                3 -> Writer.println("${value.first}      ${value.second}     ${defaultValues(isInGame, PlayerMessage.ENEMY)}")
                5 -> Writer.println("${value.first}      ${value.second}     $remainingEnemyHP")
                7 -> Writer.println("${value.first}      ${value.second}     $remainingEnemyShips")
                13 -> Writer.println("${value.first}      ${value.second}     ${defaultValues(isInGame, PlayerMessage.YOU)}")
                15 -> Writer.println("${value.first}      ${value.second}     $remainingOwnHP")
                17 -> Writer.println("${value.first}      ${value.second}     $remainingOwnShips")
                else -> Writer.println("${value.first}      ${value.second}")
            }
        }
    }

    fun eraseLastLines(lines: Int) {
        Writer.eraseLast(lines)

    }

    fun clearConsole() {
        Writer.clearConsole()
    }

    fun input(msg: String, validationMethod: (String) -> (Boolean) = { true }): String {
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

    private fun defaultValues(isInGame: Boolean, playerMessage: PlayerMessage, color: Color = Color.WHITE, counter: String = ""): String =
            if (isInGame) "$playerMessage ${color.paint(counter)}" else ""
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(size: Int): String = this.repeat(size)
