package de.twintorx.battleship.connection

import de.twintorx.battleship.game.board.Move
import java.awt.Point
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.*

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
        val split = input.nextLine().split(",").map { it.toInt() }
        return Point(split[0], split[1])
    }

    fun sendShotAnswer(move: Move) {
        output.println("${move.ordinal}")
    }

    fun sendShot(coordinates: Point): Move {
        output.println("${coordinates.x},${coordinates.y}")
        return Move.values()[input.nextLine().toString().toInt()]
    }

    fun disconnect() {
        output.close()
        input.close()
        socket.close()
    }
}
