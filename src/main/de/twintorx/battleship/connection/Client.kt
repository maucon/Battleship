package main.de.twintorx.battleship.connection

import java.awt.Point
import java.net.Socket
import java.util.*

class Client(
        address: String = "localhost",
        private val player: Any // TODO change to type Player
) {
    var client: Socket = Socket(address, 9999)
            .also { it.getOutputStream().write("Hello from a new player!".toByteArray()) }

    fun sendShot(coordinates: Point): Boolean {
        client.outputStream
                .write("${coordinates.x},${coordinates.y}".toByteArray())
        // get move from server ->
        var answerReceived = false
        val scanner = Scanner(client.inputStream)
        while (!answerReceived) {
            if (scanner.hasNextLine())
                answerReceived = true
        }
        player.handleShotAnswer(scanner.nextLine()) // TODO implement
        // TODO receive servers answer
        // answer of client will be boolean
    }


    fun disconnect() {
        client.close()
    }
}
