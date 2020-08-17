package de.twintorx.battleship

import de.twintorx.battleship.game.Ship
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.Move
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.Point

internal class GameBoardTest {

    private lateinit var gameBoard: GameBoard

    @BeforeEach
    fun setUp() {
        gameBoard = GameBoard()
    }

    @Test
    fun testAddShip() {
        assertFalse(gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(1, 1), Point(1, 1))))
        assertFalse(gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(1, 2), Point(1, 3), Point(1, 4))))
        assertFalse(gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(10, 2), Point(11, 2))))
        assertFalse(gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(-10, 2), Point(-11, 2))))
        assertTrue(gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(2, 2), Point(2, 1))))
        assertFalse(gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(1, 2), Point(2, 2))))
    }

    @Test
    fun testHit() {
        gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(1, 2), Point(1, 1)))
        gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(7, 2), Point(7, 3)))

        assertEquals(Move.HIT, gameBoard.hit(1, 2))
        assertEquals(Move.NO_HIT, gameBoard.hit(4, 4))
        assertEquals(Move.INVALID, gameBoard.hit(1, 2))
        assertEquals(Move.SUNK, gameBoard.hit(1, 1))

        gameBoard.hit(7, 2)
        assertEquals(Move.GAME_OVER, gameBoard.hit(7, 3))
    }

    @Test
    fun testToString() {
        gameBoard.addShip(Ship.SUBMARINE, hashSetOf(Point(4, 2), Point(4, 1), Point(4, 3)))
        gameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(7, 2), Point(7, 3)))

        gameBoard.hit(7, 3)
        gameBoard.hit(7, 6)

        assertEquals(mutableListOf(
                "   ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐",
                "10 │   │   │   │   │   │   │   │   │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 9 │   │   │   │   │   │   │   │   │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 8 │   │   │   │   │   │   │   │   │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 7 │   │   │   │   │   │   │   │ @|green x|@ │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 6 │   │   │   │   │   │   │   │   │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 5 │   │   │   │   │   │   │   │   │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 4 │   │   │   │   │ @|green ■|@ │   │   │ @|red o|@ │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 3 │   │   │   │   │ @|green ■|@ │   │   │ @|cyan ■|@ │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 2 │   │   │   │   │ @|green ■|@ │   │   │   │   │   │",
                "   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤",
                " 1 │   │   │   │   │   │   │   │   │   │   │",
                "   └───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘",
                "     A   B   C   D   E   F   G   H   I   J "),
                gameBoard.getLines())
    }
}
