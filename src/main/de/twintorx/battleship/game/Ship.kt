package main.de.twintorx.battleship.game

class Ship(
        val name: String,
        val size: Int
) {
    companion object {
        fun getStandardShipSet(): MutableMap<Int, MutableList<Ship>> = mutableMapOf(
                1 to Ship("Carrier", 5) * 1,
                2 to Ship("Battleship", 4) * 2,
                3 to Ship("Cruiser", 3) * 3,
                4 to Ship("Submarine", 3) * 4,
                5 to Ship("Destroyer", 2) * 5
        )
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun Ship.times(i: Int): MutableList<Ship> {
    val list = mutableListOf<Ship>()

    (1..i).forEach { _ ->
        list.add(Ship(this.name, this.size))
    }

    return list
}
