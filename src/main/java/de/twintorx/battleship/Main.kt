package de.twintorx.battleship

import de.twintorx.battleship.game.Player
import org.fusesource.jansi.AnsiConsole

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (System.console() != null && System.getenv()["OS"]!!.contains("Windows")) {
            AnsiConsole.systemInstall() // only needed for windows
        }
        //        System.out.println(ansi().eraseLine().eraseLine().render("@|red Hello|@ @|green World|@")); // TODO delete later
        Player().connect()
        if (System.console() != null && System.getenv()["OS"]!!.contains("Windows")) {
            AnsiConsole.systemUninstall() // only needed for windows
        }
    }
}
