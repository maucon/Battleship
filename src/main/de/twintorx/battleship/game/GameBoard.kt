package main.de.twintorx.battleship.game

import java.awt.Point

class GameBoard(
        size: Int = 10
) : TrackBoard(size) {

    private var shipCoordinates = mutableMapOf<Ship, ArrayList<Point>>()

    fun addShip(ship: Ship, coordinates: ArrayList<Point>) {
        shipCoordinates[ship] = coordinates

        coordinates.forEach {
            grid[it.x, it.y] = Cell.SHIP
        }
    }

    override fun hit(x: Int, y: Int): Move {
        if (super.hit(x, y) == Move.INVALID) return Move.INVALID

        for ((key, value) in shipCoordinates) {
            val removePoint = value.firstOrNull { point ->
                point.x == x && point.y == y
            } ?: continue

            if (shipCoordinates[key]!!.remove(removePoint) && shipCoordinates[key]!!.isEmpty()) {
                shipCoordinates.remove(key)

                return if (shipCoordinates.isEmpty()) Move.GAME_OVER else Move.SUNK
            }
            return Move.HIT
        }

        return Move.NO_HIT
    }

}
