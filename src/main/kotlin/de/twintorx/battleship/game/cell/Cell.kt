package de.twintorx.battleship.game.cell

import de.twintorx.battleship.game.Ship

class Cell(
        val mark: Mark = Mark.WATER,
        val ship: Ship? = null
)
