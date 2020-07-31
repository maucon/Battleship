package main.de.twintorx.battleship.console

enum class PlayerMessage(
        private val message: String
) {
    CHOOSE_SHIP("Choose your ship:"),
    COLUMN("Please enter a column ([A]-[J]):"),
    HIT_NOTHING("You hit nothing"),
    HIT_SHIP("You've hit a ship!"),
    HOST_SERVER("Do you want to host a server? [Y]/[N]"),
    INVALID_MOVE("Your move was invalid."),
    LINE("Please enter a line ([1]-[10]):"),
    LOSE("You lost :("),
    OPPONENT_HIT("Your opponent hit!"),
    OPPONENT_MISSED("Your opponent missed a shot"),
    OPPONENT_SUNK("Your opponent sunk one of your ships!"),
    ORIENTATION("Please enter orientation ([H]orizontal/[V]ertical):"),
    PLACE_SHIPS("Please place your ships."),
    SELECT_CELL("Which cell do you want to shoot at?"),
    SERVER_IP("Please enter the Server-Ip you want to connect to:"),
    SUNK_SHIP("You've sunk a ship!"),
    WIN("You've sunk the last ship and won the game!");

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

class InputRegex {
    companion object {
        val YES_OR_NO = "[yYnN]".toRegex()
        val PLACE_SHIP = "[hHvV][a-jA-J]([1-9]|10)".toRegex()
        val SELECT_SHIP = "[1-5]".toRegex()
        val SHOOT_CELL = "[a-jA-J]([1-9]|10)".toRegex()
    }
}
