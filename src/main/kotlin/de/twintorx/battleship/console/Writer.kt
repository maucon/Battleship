package de.twintorx.battleship.console

import org.fusesource.jansi.Ansi.ansi

object Writer {
    // TODO add clear console
    fun printColored(msg: String) {
        println(ansi().render(msg))
    }
}


