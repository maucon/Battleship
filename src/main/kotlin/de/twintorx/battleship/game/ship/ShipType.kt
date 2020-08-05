package de.twintorx.battleship.game.ship

import de.twintorx.battleship.ui.Color

enum class ShipType(
        val value: String,
        val color: Color?
) {
    NONE("", null),
    CARRIER("Carrier", Color.YELLOW),
    BATTLESHIP("Battleship", Color.BLUE),
    CRUISER("Cruiser", Color.MAGENTA),
    SUBMARINE("Submarine", Color.GREEN),
    DESTROYER("Destroyer", Color.CYAN)
}
