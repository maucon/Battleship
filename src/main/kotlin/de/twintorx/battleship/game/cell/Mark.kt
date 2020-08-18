package de.twintorx.battleship.game.cell

import java.io.Serializable

enum class Mark(
        val value: String
) : Serializable {

    WATER(" "),
    HIT_SHIP("o"),
    HIT_NOTHING("x"),
    SHIP("■");
}
