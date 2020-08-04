package de.twintorx.battleship.game

class Ship(
        val type: ShipType,
        val size: Int
) {
    companion object {
        fun getStandardShipSet(): MutableMap<Int, MutableList<Ship>> = mutableMapOf(
                1 to Ship(ShipType.CARRIER, 5) * 1,
                2 to Ship(ShipType.BATTLESHIP, 4) * 2,
                3 to Ship(ShipType.CRUISER, 3) * 3,
                4 to Ship(ShipType.SUBMARINE, 3) * 4,
                5 to Ship(ShipType.DESTROYER, 2) * 5
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
