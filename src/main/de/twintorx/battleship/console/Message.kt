package main.de.twintorx.battleship.console

enum class PlayerMessage(
        private val message: String
) {
    HOST_SERVER("Do you want to host a server? [Y]/[N]"),
    SERVER_IP("Please enter the Server-Ip you want to connect to:"),
    ORIENTATION("Please enter orientation ([H]orizontal/[V]ertical):"),
    COLUMN("Please enter a column ([A]-[J]):"),
    LINE("Please enter a line ([1]-[10]):");

    override fun toString(): String {
        return message
    }
}

enum class ServerMessage(
        private val message: String
) {
    PORT_RUNNING("Server running on port: "),
    HOST_CONNECTED("Client connected as host: "),
    PLAYER2_CONNECTED("Client connected as second player: "),
    WAITING_PLAYER2("Waiting for another player to connect..."),
    START_PREPARATION("Starting preparation..."),
    START_GAME("Starting the game..."),
    GAME_FINISHED("Game finished!"),
    HOST_IS("Host is "),
    PLAYER2_IS("Player2 is ");

    override fun toString(): String {
        return "${Color.RED}[SERVER]${Color.RESET} $message"
    }
}
