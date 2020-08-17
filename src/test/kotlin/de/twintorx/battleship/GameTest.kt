package de.twintorx.battleship

import de.twintorx.battleship.game.Ship
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.game.board.TrackBoard
import de.twintorx.battleship.game.cell.Mark
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.Point

internal class GameTest {

    @Test
    fun testGame() {
        val player1GameBoard = GameBoard()
        val player1TrackBoard = TrackBoard()

        val player2GameBoard = GameBoard()
        val player2TrackBoard = TrackBoard()

        // Prepare game
        // player 1 places ships
        assertTrue(player1GameBoard.addShip(Ship.SUBMARINE, hashSetOf(Point(2, 2), Point(2, 3), Point(2, 4))))
        assertTrue(player1GameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(0, 0), Point(0, 1))))

        // player 2 places ships
        assertTrue(player2GameBoard.addShip(Ship.SUBMARINE, hashSetOf(Point(6, 2), Point(6, 3), Point(6, 4))))
        assertTrue(player2GameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(2, 5), Point(3, 5))))

        // Game starting
        // player 1 turn hitting: 1 1
        assertEquals(Move.NO_HIT, player2GameBoard.hit(1, 1)) // no hit
        assertTrue(player1TrackBoard.mark(1, 1, Mark.HIT_NOTHING))

        // player 2 turn hitting: 0 0
        assertEquals(Move.HIT, player1GameBoard.hit(0, 0)) // hit
        assertTrue(player2TrackBoard.mark(0, 0, Mark.HIT_SHIP))

        // player 1 turn hitting: 2 5
        assertEquals(Move.HIT, player2GameBoard.hit(2, 5)) // hit
        assertTrue(player1TrackBoard.mark(2, 5, Mark.HIT_SHIP))

        // player 2 turn hitting: 0 1
        assertEquals(Move.SUNK, player1GameBoard.hit(0, 1)) // hit and sunk
        assertTrue(player2TrackBoard.mark(0, 1, Mark.HIT_SHIP))

        // player 1 turn hitting:  7 7
        assertEquals(Move.NO_HIT, player2GameBoard.hit(7, 7)) // no hit
        assertTrue(player1TrackBoard.mark(7, 7, Mark.HIT_NOTHING))

        // player 2 turn hitting: 2 2
        assertEquals(Move.HIT, player1GameBoard.hit(2, 2)) // hit
        assertTrue(player2TrackBoard.mark(2, 2, Mark.HIT_SHIP))

        // player 1 turn hitting: 5 5
        assertEquals(Move.NO_HIT, player2GameBoard.hit(5, 5)) // no hit
        assertTrue(player1TrackBoard.mark(5, 5, Mark.HIT_NOTHING))

        // player 2 turn hitting: 2 3
        assertEquals(Move.HIT, player1GameBoard.hit(2, 3)) // hit
        assertTrue(player2TrackBoard.mark(2, 3, Mark.HIT_SHIP))

        // player 1 turn hitting: 9 9
        assertEquals(Move.NO_HIT, player2GameBoard.hit(9, 9))// no hit
        assertTrue(player1TrackBoard.mark(9, 9, Mark.HIT_NOTHING))

        // player 2 turn hitting: 2 4
        assertEquals(Move.GAME_OVER, player1GameBoard.hit(2, 4)) // hit and sunk and won
        assertTrue(player2TrackBoard.mark(2, 4, Mark.HIT_SHIP))
    }

}
