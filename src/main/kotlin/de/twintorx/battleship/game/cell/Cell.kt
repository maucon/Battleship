package de.twintorx.battleship.game.cell

import de.twintorx.battleship.game.Ship
import java.io.Serializable

class Cell(
        val mark: Mark = Mark.WATER,
        val ship: Ship? = null
) : Serializable
