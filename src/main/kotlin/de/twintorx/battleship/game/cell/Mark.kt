package de.twintorx.battleship.game.cell

enum class Mark(
        val value: String
) {
    WATER(" "),
    HIT_SHIP("o"),
    HIT_NOTHING("x"),
    SHIP("■");
}
