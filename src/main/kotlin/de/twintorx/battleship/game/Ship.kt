package de.twintorx.battleship.game

import de.twintorx.battleship.ui.Color
import java.io.Serializable

enum class Ship(
        val type: String,
        val color: Color,
        val size: Int
) : Serializable {

    CARRIER("Carrier", Color.YELLOW, 5),
    BATTLESHIP("Battleship", Color.BLUE, 4),
    CRUISER("Cruiser", Color.MAGENTA, 3),
    SUBMARINE("Submarine", Color.GREEN, 3),
    DESTROYER("Destroyer", Color.CYAN, 2);

    companion object {
        fun getStandardShipSet(): MutableMap<Int, MutableList<Ship>> = mutableMapOf(
                1 to CARRIER * 1,
                2 to BATTLESHIP * 2,
                3 to CRUISER * 3,
                4 to SUBMARINE * 4,
                5 to DESTROYER * 5
        )
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun Ship.times(i: Int): MutableList<Ship> {
    val ship = this
    return mutableListOf<Ship>().apply {
        repeat(i) { add(ship) }
    }
}

private operator fun String.times(size: Int): String = this.repeat(size)
