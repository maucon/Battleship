package de.twintorx.battleship.ui.io

import de.twintorx.battleship.ui.Color

enum class PlayerMessage(
        private val message: String
) {
    CHOOSE_SHIP("Choose your ship:"),
    GAME_BOARD("Your ships:"),
    HIT_NOTHING("You hit nothing"),
    HIT_SHIP("You've hit a ship!"),
    HOST_SERVER("Do you want to host a server? [Y]/[N]"),
    INVALID_MOVE("Your move was invalid."),
    LOSE("You lost :("),
    OPPONENT_HIT("Your opponent hit!"),
    OPPONENT_MISSED("Your opponent missed a shot"),
    OPPONENT_SUNK("Your opponent sunk one of your ships!"),
    PLACE_SHIPS("Please place your ships."),
    POSITION_SHIP("Pls enter ship position!\n[h]orizontal/[v]ertical + column[A-J] + line[1-10] -> (e.g: ha1) :"),
    QUIT(Color.GREEN.paint("~~ Thank you for playing! ~~")),
    SERVER_IP("Please enter the Server-Ip you want to connect to:"),
    SHOOT("Which cell do you want to shoot at ? ~ column[A-J] + line[1-10] ~ (e.g: a1) :"),
    SUNK_SHIP("You've sunk a ship!"),
    TRACK_BOARD("Your shots:"),
    WAITING_FOR_PLACEMENT("Waiting until all players have placed their ships..."),
    WAITING_FOR_TURN("Wait until its your turn..."),
    WELCOME(Color.GREEN.paint("~~ Welcome to BATTLESHIP by Maucon and Dennis ~~")),
    WIN("You've sunk the last ship and won the game!"),
    GAME_ABORT("The game was aborted because your opponent lost connection");

    override fun toString(): String {
        return message
    }
}
