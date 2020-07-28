package main.de.twintorx.battleship.game

import java.awt.Point

class GameBoard(
        val size: Int = 10
) : TrackBoard(size) {

    private var shipCoordinates = mutableMapOf<Ship, ArrayList<Point>>()

    fun addShip(ship: Ship, coordinates: ArrayList<Point>) {
        shipCoordinates[ship] = coordinates
        coordinates.forEach {
            setGridCell(it.x, it.y, Cell.SHIP)
        }
    }

}
