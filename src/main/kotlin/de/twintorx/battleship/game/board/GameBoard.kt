package de.twintorx.battleship.game.board

import de.twintorx.battleship.game.Cell
import de.twintorx.battleship.game.Mark
import de.twintorx.battleship.game.Ship
import java.awt.Point

class GameBoard(
        size: Int = 10
) : TrackBoard(size) {

    private var shipList = mutableListOf<Pair<Ship, HashSet<Point>>>()

    fun addShip(ship: Ship, coordinates: HashSet<Point>): Boolean {
        if (invalidShipCoordinates(ship, coordinates)) return false

        shipList.add(Pair(ship, coordinates))
        coordinates.forEach {
            grid[it.x, it.y] = Cell(Mark.SHIP, ship)
        }

        return true
    }

    private fun invalidShipCoordinates(ship: Ship, coordinates: HashSet<Point>): Boolean {
        if (ship.size != coordinates.size) return true

        coordinates.forEach { point ->
            if (point.x !in (0 until size)) return true
            if (point.y !in (0 until size)) return true
            if (grid[point.x, point.y].mark == Mark.SHIP) return true
        }

        // count of distinct x and y values must be ship size + 1
        return coordinates.map { it.x }.distinct().size + coordinates.map { it.y }.distinct().size != ship.size + 1
    }

    fun hit(x: Int, y: Int): Move {
        if (!mark(x, y, if (grid[x, y].mark == Mark.SHIP) Mark.HIT_SHIP else Mark.HIT_NOTHING))
            return Move.INVALID

        for (pair in shipList) {
            val coordinates = pair.second // val (ship, coordinates) = pair
            val removePoint = coordinates.firstOrNull { point ->
                point.x == x && point.y == y
            } ?: continue

            if (coordinates.remove(removePoint) && coordinates.isEmpty()) {
                shipList.remove(pair)

                return if (shipList.isEmpty()) Move.GAME_OVER else Move.SUNK
            }
            return Move.HIT
        }

        return Move.NO_HIT
    }
}
