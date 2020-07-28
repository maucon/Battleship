package main.de.twintorx.battleship.game

class Ship(
        val name: String,
        val length: Int
) {
    private var health: Int = length

    fun hit() = --health
}

fun getStandardShipSet(): Collection<Ship> {
    val list = arrayListOf(Ship("Carrier", 5))
    list add Ship("Battleship", 4) * 2
    list add Ship("Cruiser", 3) * 3
    list add Ship("Submarine", 3) * 4
    list add Ship("Destroyer", 2) * 5
    return list
}

// ---------------- Extensions and Overloading ----------------
private operator fun Ship.times(i: Int): Collection<Ship> {
    val list = ArrayList<Ship>()
    (1..i).forEach { _ ->
        list.add(Ship(this.name, this.length))
    }
    return list
}

private infix fun ArrayList<Ship>.add(ships: Collection<Ship>) {
    this.addAll(ships)
}
