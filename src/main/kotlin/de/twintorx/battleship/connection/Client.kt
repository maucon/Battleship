package de.twintorx.battleship.connection

import ClientSocket
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.ui.io.PlayerMessage
import de.twintorx.battleship.ui.io.Writer
import java.awt.Point
import kotlin.system.exitProcess

class Client {
    private lateinit var socket: ClientSocket

    fun tryConnect(address: String = "localhost", port: Int = 9999) = try {
        socket = ClientSocket(address, port)
        socket.read()

        true
    } catch (ignored: Exception) {
        false
    }

    fun sendReadyGetTurn(): Boolean {
        socket.write(Package()) // Sending server ready signal
        return doSafe { socket.read() }.body as Boolean // true -> your turn: false -> opponents turn
    }

    fun waitForIncomingShot() = doSafe { socket.read() }.body as Point

    fun sendShotAnswer(move: Move) {
        socket.write(Package(move.ordinal))
    }

    fun sendShot(coordinates: Point): Move {
        socket.write(Package(coordinates))
        return Move.values()[doSafe { socket.read() }.body as Int]
    }

    fun sendBoardGetBoard(board: GameBoard): GameBoard {
        socket.write(Package(board.apply { resetLastPoint() }))
        return doSafe { socket.read() }.body as GameBoard
    }

    private fun doSafe(method: () -> (Package)) = try {
        method()
    } catch (e: Exception) {
        Writer.print("\n${PlayerMessage.GAME_ABORT}")
        disconnect()
        exitProcess(1)
    }

    fun disconnect() {
        socket.closeAll()
    }
}
