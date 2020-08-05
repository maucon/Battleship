package de.twintorx.battleship.ui.io

import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import java.io.PrintStream

object Writer {
    // TODO add clear console
    private val printStream = PrintStream(AnsiConsole.out(), true, "CP850")

    fun print(msg: String) {
        printStream.println(ansi().render(msg))
    }
}


