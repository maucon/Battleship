package main.de.twintorx.battleship.connection

import main.de.twintorx.battleship.game.Move
import java.awt.Point
import java.io.*
import java.net.Socket
import java.util.*

class Client(
        address: String = "localhost"
) {
    private var socket: Socket = Socket(address, 9999)
    private var printWriter: PrintWriter = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
    private var scanner: Scanner = Scanner(socket.getInputStream()).also { it.nextLine() }

    fun sendReadyGetTurn(): Boolean {
        printWriter.println("1")
        // returns 1 -> its this players turn; 0 -> its the other players turn
        return scanner.nextLine().toString().toInt() == 1
    }

    fun waitForIncomingShot(): Point {
        val coordinates = scanner.nextLine()
        val split = coordinates.split(",").map { it.toInt() }
        return Point(split[0], split[1])
    }

    fun sendShotAnswer(move: Move) {
        printWriter.println("${move.ordinal}")
    }

    fun sendShot(coordinates: Point): Move {
        printWriter.println("${coordinates.x},${coordinates.y}")

        val response = scanner.nextLine().toString().toInt() // TODO check
        return Move.values()[response]
    }

    fun disconnect() {
        printWriter.close()
        scanner.close()
        socket.close()
    }
}
