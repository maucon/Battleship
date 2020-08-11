package de.twintorx.battleship

import de.twintorx.battleship.ui.Player
import org.fusesource.jansi.AnsiConsole
import java.awt.GraphicsEnvironment

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val consoleIsNull = System.console() == null
        val isWindows = !consoleIsNull && System.getenv()["OS"]!!.contains("Windows")

        if (isWindows) AnsiConsole.systemInstall() // only needed for windows

        if (consoleIsNull && !GraphicsEnvironment.isHeadless()) {
            val filename = Main::class.java.protectionDomain.codeSource.location.toString().substring(6)
            Runtime.getRuntime().exec(arrayOf("cmd", "/c", "start", "cmd", "/k", "java -jar \"$filename\""))
        } else while (true) Player().connect()

        if (isWindows) AnsiConsole.systemUninstall() // only needed for windows
    }
}
