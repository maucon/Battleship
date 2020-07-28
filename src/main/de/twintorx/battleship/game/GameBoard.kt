package main.de.twintorx.battleship.game

import java.awt.Point

class GameBoard(
        size: Int = 10
) : TrackBoard(size) {

    private var shipCoordinates = mutableMapOf<Ship, HashSet<Point>>()

    fun addShip(ship: Ship, coordinates: HashSet<Point>): Boolean {
        if (invalidShipCoordinates(ship, coordinates)) return false

        shipCoordinates[ship] = coordinates
        coordinates.forEach {
            grid[it.x, it.y] = Cell.SHIP
        }

        return true
    }

    private fun invalidShipCoordinates(ship: Ship, coordinates: HashSet<Point>): Boolean {
        if (ship.size != coordinates.size) return true

        coordinates.forEach { point ->
            if (point.x !in (0 until size)) return true
            if (point.y !in (0 until size)) return true
            if (grid[point.x, point.y] == Cell.SHIP) return true
        }

        // count of distinct x and y values must be ship size + 1
        return coordinates.map { it.x }.distinct().size + coordinates.map { it.y }.distinct().size != ship.size + 1
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
