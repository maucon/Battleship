package de.twintorx.battleship.game.ship

class Ship(
        val type: ShipType,
        val size: Int
) {
    companion object {
        fun getStandardShipSet(): MutableMap<Int, MutableList<Ship>> = mutableMapOf(
                1 to Ship(ShipType.CARRIER, 5) * 1
        )
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun Ship.times(i: Int): MutableList<Ship> {
    val list = mutableListOf<Ship>()

    (1..i).forEach { _ ->
        list.add(Ship(this.type, this.size))
    }

    return list
}
