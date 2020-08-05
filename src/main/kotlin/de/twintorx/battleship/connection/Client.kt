package de.twintorx.battleship.connection

import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.ui.io.PlayerMessage
import de.twintorx.battleship.ui.io.Writer
import java.awt.Point
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import kotlin.system.exitProcess

class Client {
    private lateinit var socket: Socket
    private lateinit var output: PrintWriter
    private lateinit var input: Scanner

    fun tryConnect(address: String = "localhost"): Boolean {
        return try {
            socket = Socket(address, 9999)
            output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            input = Scanner(socket.getInputStream()).also { it.nextLine() }

            true
        } catch (ignored: Exception) {
            false
        }
    }

    fun sendReadyGetTurn(): Boolean {
        output.println("ready") // Sending server ready signal
        return input.nextLine().toString().toInt() == 1 // 1 -> your turn: 0 -> opponents turn
    }

    fun waitForIncomingShot(): Point {
        @Suppress("UNCHECKED_CAST")
        val split = doSafe { input.nextLine().split(",").map { it.toInt() } } as List<Int>
        return Point(split[0], split[1])
    }

    fun sendShotAnswer(move: Move) {
        output.println("${move.ordinal}")
    }

    fun sendShot(coordinates: Point): Move {
        output.println("${coordinates.x},${coordinates.y}")
        return doSafe { Move.values()[input.nextLine().toString().toInt()] } as Move
    }

    private fun doSafe(method: () -> (Any)) = try {
        method()
    } catch (e: Exception) {
        Writer.print(PlayerMessage.GAME_ABORT.toString())
        exitProcess(1)
    }

    fun disconnect() {
        output.close()
        input.close()
        socket.close()
    }
}
