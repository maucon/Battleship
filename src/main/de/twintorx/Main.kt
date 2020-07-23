package main.de.twintorx

import main.de.twintorx.console.Color
import main.de.twintorx.console.print

fun main() {
    Color.values().forEach {
        print("$it: DESTROY", it)
    }
}
