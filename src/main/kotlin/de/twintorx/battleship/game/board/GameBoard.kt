package de.twintorx.battleship.game.board

import de.twintorx.battleship.game.Ship
import de.twintorx.battleship.game.cell.Cell
import de.twintorx.battleship.game.cell.Mark
import java.awt.Point
import java.io.Serializable

class GameBoard(
        size: Int = 10
) : Serializable, TrackBoard(size) {

    private var shipList = mutableListOf<Pair<Ship, HashSet<Point>>>()

    fun addShip(ship: Ship, coordinates: HashSet<Point>): Boolean {
        if (invalidShipCoordinates(ship, coordinates)) return false

        shipList.add(Pair(ship, coordinates))
        coordinates.forEach {
            grid[it.x, it.y] = Cell(Mark.SHIP, ship)
        }

        return true
    }

    fun removeShip(coordinates: HashSet<Point>): Boolean {
        coordinates.forEach {
            if (grid[it.x, it.y].mark != Mark.SHIP) return false
        }
        coordinates.forEach {
            grid[it.x, it.y] = Cell(Mark.WATER)
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

        lastPoint.move(x, y)

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

    fun resetLastPoint() = lastPoint.move(-1, -1)
}
