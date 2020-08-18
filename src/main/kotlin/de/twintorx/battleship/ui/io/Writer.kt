package de.twintorx.battleship.ui.io

import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import java.io.PrintStream

object Writer {
    private val printStream = PrintStream(AnsiConsole.out(), true, "CP850")

    fun print(msg: String) {
        printStream.print(ansi().render(msg))
    }

    fun println(msg: String) {
        printStream.println(ansi().render(msg))
    }

    fun eraseLast(count: Int = 1) {
        (0 until count).forEach { _ ->
            print(ansi().cursorUp(1).eraseLine())
        }
    }

    fun clearConsole() {
        eraseLast(100)
    }
}


