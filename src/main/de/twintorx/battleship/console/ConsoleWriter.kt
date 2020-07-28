package main.de.twintorx.battleship.console

import main.de.twintorx.battleship.game.Color

fun print(message: Any, color: Color = Color.RESET) {
    println("" + color + message + Color.RESET)
}
