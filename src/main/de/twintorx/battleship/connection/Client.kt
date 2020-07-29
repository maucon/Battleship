package main.de.twintorx.battleship.connection

import main.de.twintorx.battleship.game.Move
import java.awt.Point
import java.net.Socket
import java.util.*

class Client(
        address: String = "localhost"
) {
    private val socket: Socket = Socket(address, 9999)
    private val outputStream = socket.getOutputStream()
            .also { it.write("Hello from a new player!".toByteArray()) }
    private val scanner = Scanner(socket.getInputStream())

    fun sendReady() {
        outputStream.write(1)
    }

    fun sendShot(coordinates: Point): Move {
        outputStream.write("${coordinates.x},${coordinates.y}".toByteArray())

        val response = scanner.nextLine().toString().toInt() // TODO check
        return Move.values()[response]
    }

    fun disconnect() {
        outputStream.close()
        scanner.close()
        socket.close()
    }
}
