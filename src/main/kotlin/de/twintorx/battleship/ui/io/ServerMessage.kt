package de.twintorx.battleship.ui.io

import de.twintorx.battleship.ui.Color

enum class ServerMessage(
        private val message: String
) {
    PORT_RUNNING("Server running on port: "),
    HOST_CONNECTED("Client connected as host: "),
    PLAYER2_CONNECTED("Client connected as second player: "),
    WAITING_PLAYER2("Waiting for another player to connect..."),
    GAME_ABORT("The game was aborted because your opponent lost connection"),
    PUBLIC_ADDRESS("Public-IP:"),
    LOCAL_ADDRESS("Local-IP: ");

    override fun toString(): String {
        return "${Color.RED.paint("[SERVER]")} $message"
    }
}
