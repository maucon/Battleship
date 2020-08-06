package de.twintorx.battleship

import de.twintorx.battleship.ui.Player
import org.fusesource.jansi.AnsiConsole

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val isWindows = System.console() != null && System.getenv()["OS"]!!.contains("Windows")

        if (isWindows) {
            AnsiConsole.systemInstall() // only needed for windows
        }

        Player().connect()

        if (isWindows) {
            AnsiConsole.systemUninstall() // only needed for windows
        }
    }
}
