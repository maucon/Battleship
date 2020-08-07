package de.twintorx.battleship

import de.twintorx.battleship.game.Mark
import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.TrackBoard
import de.twintorx.battleship.game.Ship
import org.junit.jupiter.api.Assertions
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
        Assertions.assertTrue(player1GameBoard.addShip(Ship.SUBMARINE, hashSetOf(Point(2, 2), Point(2, 3), Point(2, 4))))
        Assertions.assertTrue(player1GameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(0, 0), Point(0, 1))))

        // player 2 places ships
        Assertions.assertTrue(player2GameBoard.addShip(Ship.SUBMARINE, hashSetOf(Point(6, 2), Point(6, 3), Point(6, 4))))
        Assertions.assertTrue(player2GameBoard.addShip(Ship.DESTROYER, hashSetOf(Point(2, 5), Point(3, 5))))

        // Game starting
        // player 1 turn hitting: 1 1
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(1, 1)) // no hit
        Assertions.assertTrue(player1TrackBoard.mark(1, 1, Mark.HIT_NOTHING))

        // player 2 turn hitting: 0 0
        Assertions.assertEquals(Move.HIT, player1GameBoard.hit(0, 0)) // hit
        Assertions.assertTrue(player2TrackBoard.mark(0, 0, Mark.HIT_SHIP))

        // player 1 turn hitting: 2 5
        Assertions.assertEquals(Move.HIT, player2GameBoard.hit(2, 5)) // hit
        Assertions.assertTrue(player1TrackBoard.mark(2, 5, Mark.HIT_SHIP))

        // player 2 turn hitting: 0 1
        Assertions.assertEquals(Move.SUNK, player1GameBoard.hit(0, 1)) // hit and sunk
        Assertions.assertTrue(player2TrackBoard.mark(0, 1, Mark.HIT_SHIP))

        // player 1 turn hitting:  7 7
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(7, 7)) // no hit
        Assertions.assertTrue(player1TrackBoard.mark(7, 7, Mark.HIT_NOTHING))

        // player 2 turn hitting: 2 2
        Assertions.assertEquals(Move.HIT, player1GameBoard.hit(2, 2)) // hit
        Assertions.assertTrue(player2TrackBoard.mark(2, 2, Mark.HIT_SHIP))

        // player 1 turn hitting: 5 5
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(5, 5)) // no hit
        Assertions.assertTrue(player1TrackBoard.mark(5, 5, Mark.HIT_NOTHING))

        // player 2 turn hitting: 2 3
        Assertions.assertEquals(Move.HIT, player1GameBoard.hit(2, 3)) // hit
        Assertions.assertTrue(player2TrackBoard.mark(2, 3, Mark.HIT_SHIP))

        // player 1 turn hitting: 9 9
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(9, 9))// no hit
        Assertions.assertTrue(player1TrackBoard.mark(9, 9, Mark.HIT_NOTHING))

        // player 2 turn hitting: 2 4
        Assertions.assertEquals(Move.GAME_OVER, player1GameBoard.hit(2, 4)) // hit and sunk and won
        Assertions.assertTrue(player2TrackBoard.mark(2, 4, Mark.HIT_SHIP))
    }

}
