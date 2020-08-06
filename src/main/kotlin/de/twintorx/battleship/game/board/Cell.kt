package de.twintorx.battleship.game.board

import de.twintorx.battleship.ui.Color
import de.twintorx.battleship.game.ship.ShipType

enum class Cell(
        private val value: String,
        private val shipType: ShipType = ShipType.NONE
) {
    WATER(" "),
    HIT_SHIP(Color.RED.paint("o")),
    HIT_NOTHING("x"),
    SHIP_CARRIER("■", ShipType.CARRIER),
    SHIP_BATTLESHIP("■", ShipType.BATTLESHIP),
    SHIP_CRUISER("■", ShipType.CRUISER),
    SHIP_SUBMARINE("■", ShipType.SUBMARINE),
    SHIP_DESTROYER("■", ShipType.DESTROYER);

    override fun toString() = when (shipType.color) {
        null -> value
        else -> shipType.color.paint(value)
    }

    fun isShip() = when (this) {
        WATER, HIT_SHIP, HIT_NOTHING -> false
        else -> true
    }
}
