package de.twintorx.battleship.ui.io

import de.twintorx.battleship.ui.Color

enum class ServerMessage(
        private val message: String
) {
    PORT_RUNNING("Server running on port: "),
    WAITING_PLAYER1("Waiting for a player to connect..."),
    WAITING_PLAYER2("Waiting for another player to connect..."),
    PLAYER1_CONNECTED("Client connected as player1: "),
    PLAYER2_CONNECTED("Client connected as player2: "),
    GAME_ABORT("The game was aborted because a player lost connection"),
    PUBLIC_ADDRESS("Public-IP:"),
    LOCAL_ADDRESS("Local-IP: ");

    override fun toString(): String {
        return "${Color.RED.paint("[SERVER]")} $message"
    }
}
