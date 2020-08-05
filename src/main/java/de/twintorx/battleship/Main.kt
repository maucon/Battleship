package de.twintorx.battleship

import de.twintorx.battleship.game.Player
import org.fusesource.jansi.AnsiConsole

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (System.console() != null && System.getenv()["OS"]!!.contains("Windows")) {
            AnsiConsole.systemInstall() // only needed for windows
        }
        Player().connect()
        if (System.console() != null && System.getenv()["OS"]!!.contains("Windows")) {
            AnsiConsole.systemUninstall() // only needed for windows
        }
    }
}
