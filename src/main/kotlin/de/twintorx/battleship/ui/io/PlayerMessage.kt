package de.twintorx.battleship.ui.io

import de.twintorx.battleship.ui.Color

enum class PlayerMessage(
        private val message: String
) {
    CHOOSE_SHIP(Color.YELLOW.paint("Choose your ship:")),
    HOST_SERVER(Color.YELLOW.paint("Do you want to host a server? [Y]/[N]")),
    SERVER_IP(Color.YELLOW.paint("Please enter the Server-Ip you want to connect to:")),
    PORT(Color.YELLOW.paint("Please enter a custom port or leave blank for default:")),
    PLACE_SHIPS(Color.YELLOW.paint("Please place your ships:")),
    POSITION_SHIP(Color.YELLOW.paint("Please enter the ship position!\n[h]orizontal/[v]ertical + column[A-J] + line[1-10] -> (e.g: ha1) :")),
    SHOOT(Color.YELLOW.paint("Which cell do you want to shoot at ? ~ column[A-J] + line[1-10] ~ (e.g: a1) :")),

    WAITING_FOR_PLACEMENT(Color.WHITE.paint("Waiting until all players have placed their ships...")),
    WAITING_FOR_TURN(Color.WHITE.paint("Wait until its your turn...")),

    AVAILABLE_SHIPS("Available ships:"),
    GAME_BOARD("Your ships:"),
    TRACK_BOARD("Your shots:"),
    ENEMY("Enemy:"),
    YOU("You:"),
    REMAINING_HIT_POINTS("Remaining HP:"),
    REMAINING_SHIPS("Remaining ships:"),

    HIT_NOTHING(Color.CYAN.paint("You hit nothing on")),
    HIT_SHIP(Color.CYAN.paint("You hit a ship on")),
    SUNK_SHIP(Color.CYAN.paint("You sunk a ship on")),
    WIN(Color.CYAN.paint("You sunk the last ship and won the game!")),

    OPPONENT_HIT(Color.CYAN.paint("Your opponent hit on")),
    OPPONENT_MISSED(Color.CYAN.paint("Your opponent missed on")),
    OPPONENT_SUNK(Color.CYAN.paint("Your opponent sunk one of your ships on")),
    LOSE(Color.CYAN.paint("You lost :(")),

    GAME_ABORT(Color.RED.paint("The game was aborted because your opponent lost connection")),

    WELCOME(Color.GREEN.paint("~~ Welcome to BATTLESHIP by MauCon and Dennis ~~")),
    WELCOME_INFO(Color.GREEN.paint("~~ We recommend to play in fullscreen mode! ~~")),
    QUIT(Color.GREEN.paint("~~ Thank you for playing! ~~"));

    override fun toString(): String {
        return message
    }
}
