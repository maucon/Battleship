package main.de.twintorx.battleship.game

class Ship(
        val name: String,
        val length: Int,
        private var health: Int
) {
    fun hit() {
        --health
    }
}
