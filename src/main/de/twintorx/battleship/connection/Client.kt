package main.de.twintorx.battleship.connection

import main.de.twintorx.battleship.game.Move
import java.awt.Point
import java.io.OutputStream
import java.net.Socket
import java.util.*

class Client(
        address: String = "localhost"
) {
    private var socket: Socket = Socket(address, 9999)
    private var outputStream: OutputStream = socket.getOutputStream()
    private var scanner: Scanner = Scanner(socket.getInputStream())

    private fun OutputStream.write(message: String) {
        this.write("$message\n".toByteArray())
    }

    init {
        // client waits until he is connected
        if (scanner.hasNextLine()) {
            scanner.nextLine()
        }
    }

    fun sendReadyGetTurn(): Boolean { // TODO rename or outsource in other method
        outputStream.write("1")
        // returns 1 -> its this players turn; 0 -> its the other players turn
        return scanner.nextLine().toString().toInt() == 1
    } // TODO if not turn -> wait to send answer if hit

    fun sendShot(coordinates: Point): Move {
        outputStream.write("${coordinates.x},${coordinates.y}")

        val response = scanner.nextLine().toString().toInt() // TODO check
        return Move.values()[response]
    }

    fun disconnect() {
        outputStream.close()
        scanner.close()
        socket.close()
    }

}
