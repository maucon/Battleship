package de.twintorx.battleship.ui

import de.twintorx.battleship.game.Ship
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.TrackBoard
import de.twintorx.battleship.ui.io.PlayerMessage
import de.twintorx.battleship.ui.io.Writer

object Console {

    fun printBoards(inGame: Boolean, gameBoard: GameBoard, trackBoard: TrackBoard, rehp: Int, rohp: Int, res: Int, ros: Int) {
        printAvailableShips()
        Writer.println("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (trackBoard.size * 3)}${" " * 7}${PlayerMessage.TRACK_BOARD}")
        printBoardWithStats(gameBoard, trackBoard, inGame, rehp, rohp, res, ros)
    }

    fun printWinScreen(won: Boolean, gameBoard: GameBoard, enemyBoard: GameBoard, rehp: Int, rohp: Int, res: Int, ros: Int) {
        Writer.clearConsole()

        printAvailableShips()
        Writer.println("\n${" " * 4}${PlayerMessage.GAME_BOARD}${" " * (gameBoard.size * 3)}${" " * 9}${PlayerMessage.ENEMY_BOARD}")
        printBoardWithStats(gameBoard, enemyBoard, true, rehp, rohp, res, ros)

        Writer.print(if (won) "\n${PlayerMessage.WIN}\n" else "\n${PlayerMessage.LOSE}\n")
    }

    private fun printAvailableShips() {
        Writer.println("\n${PlayerMessage.AVAILABLE_SHIPS}")
        Writer.println(Ship.values().joinToString("   ") { "${it.color.paint(it.type)}(Size:${it.size})" })
    }

    private fun printBoardWithStats(board1: GameBoard, board2: TrackBoard, inGame: Boolean, rehp: Int, rohp: Int, res: Int, ros: Int) {
        val remainingEnemyHP = defaultValues(inGame, PlayerMessage.REMAINING_HIT_POINTS, Color.RED, rehp.toString())
        val remainingOwnHP = defaultValues(inGame, PlayerMessage.REMAINING_HIT_POINTS, Color.GREEN, rohp.toString())
        val remainingEnemyShips = defaultValues(inGame, PlayerMessage.REMAINING_SHIPS, Color.RED, res.toString())
        val remainingOwnShips = defaultValues(inGame, PlayerMessage.REMAINING_SHIPS, Color.GREEN, ros.toString())

        (board1.getLines() zip board2.getLines()).withIndex().forEach { (index, value) ->
            when (index) {
                3 -> Writer.println("${value.first}      ${value.second}     ${defaultValues(inGame, PlayerMessage.ENEMY)}")
                5 -> Writer.println("${value.first}      ${value.second}     $remainingEnemyHP")
                7 -> Writer.println("${value.first}      ${value.second}     $remainingEnemyShips")
                13 -> Writer.println("${value.first}      ${value.second}     ${defaultValues(inGame, PlayerMessage.YOU)}")
                15 -> Writer.println("${value.first}      ${value.second}     $remainingOwnHP")
                17 -> Writer.println("${value.first}      ${value.second}     $remainingOwnShips")
                else -> Writer.println("${value.first}      ${value.second}")
            }
        }
    }

    private fun defaultValues(inGame: Boolean, playerMessage: PlayerMessage, color: Color = Color.WHITE, counter: String = ""): String =
            if (inGame) "$playerMessage ${color.paint(counter)}" else ""

}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(size: Int): String = this.repeat(size)