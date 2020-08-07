package de.twintorx.battleship.game.cell

import de.twintorx.battleship.ui.Color

enum class Mark(
        val value: String
) {
    WATER(" "),
    HIT_SHIP(Color.RED.paint("o")),
    HIT_NOTHING("x"),
    SHIP("■");
}
