package de.twintorx.battleship.connection

import de.twintorx.battleship.game.Move
import java.awt.Point
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class Client(
        address: String = "localhost"
) {
    private val socket = Socket(address, 9999)
    private val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
    private val input = Scanner(socket.getInputStream()).also { it.nextLine() }

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
