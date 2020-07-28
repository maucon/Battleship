package main.de.twintorx.battleship.game

import java.awt.Point

class GameBoard(
        val size: Int = 10
) : TrackBoard(size) {

    private var shipCoordinates = mutableMapOf<Ship, ArrayList<Point>>()

    fun addShip(ship: Ship, coordinates: ArrayList<Point>) {
        shipCoordinates[ship] = coordinates
        coordinates.forEach {
            grid[it.x, it.y] = Cell.SHIP

        }
    }

    override fun hit(x: Int, y: Int) {
        super.hit(x, y)

        for ((key, value) in shipCoordinates) {
            val removePoint = value.firstOrNull { point ->
                point.x == x && point.y == y
            } ?: continue

            if (shipCoordinates[key]!!.remove(removePoint) && shipCoordinates[key]!!.size <= 0) {
                shipCoordinates.remove(key)
                //TODO: announce ship sunk
            }
            break
        }
    }

}
