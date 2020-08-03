package test.de.twintorx.battleship

import main.de.twintorx.battleship.game.Cell
import main.de.twintorx.battleship.game.Move
import main.de.twintorx.battleship.game.Ship
import main.de.twintorx.battleship.game.ShipType
import main.de.twintorx.battleship.game.board.GameBoard
import main.de.twintorx.battleship.game.board.TrackBoard
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
        Assertions.assertTrue(player1GameBoard.addShip(Ship(ShipType.SUBMARINE, 3), hashSetOf(Point(2, 2), Point(2, 3), Point(2, 4))))
        Assertions.assertTrue(player1GameBoard.addShip(Ship(ShipType.DESTROYER, 2), hashSetOf(Point(0, 0), Point(0, 1))))

        // player 2 places ships
        Assertions.assertTrue(player2GameBoard.addShip(Ship(ShipType.SUBMARINE, 3), hashSetOf(Point(6, 2), Point(6, 3), Point(6, 4))))
        Assertions.assertTrue(player2GameBoard.addShip(Ship(ShipType.DESTROYER, 2), hashSetOf(Point(2, 5), Point(3, 5))))

        // Game starting
        // player 1 turn hitting: 1 1
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(1, 1)) // no hit
        Assertions.assertTrue(player1TrackBoard.mark(1, 1, Cell.HIT_NOTHING))

        // player 2 turn hitting: 0 0
        Assertions.assertEquals(Move.HIT, player1GameBoard.hit(0, 0)) // hit
        Assertions.assertTrue(player2TrackBoard.mark(0, 0, Cell.HIT_SHIP))

        // player 1 turn hitting: 2 5
        Assertions.assertEquals(Move.HIT, player2GameBoard.hit(2, 5)) // hit
        Assertions.assertTrue(player1TrackBoard.mark(2, 5, Cell.HIT_SHIP))

        // player 2 turn hitting: 0 1
        Assertions.assertEquals(Move.SUNK, player1GameBoard.hit(0, 1)) // hit and sunk
        Assertions.assertTrue(player2TrackBoard.mark(0, 1, Cell.HIT_SHIP))

        // player 1 turn hitting:  7 7
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(7, 7)) // no hit
        Assertions.assertTrue(player1TrackBoard.mark(7, 7, Cell.HIT_NOTHING))

        // player 2 turn hitting: 2 2
        Assertions.assertEquals(Move.HIT, player1GameBoard.hit(2, 2)) // hit
        Assertions.assertTrue(player2TrackBoard.mark(2, 2, Cell.HIT_SHIP))

        // player 1 turn hitting: 5 5
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(5, 5)) // no hit
        Assertions.assertTrue(player1TrackBoard.mark(5, 5, Cell.HIT_NOTHING))

        // player 2 turn hitting: 2 3
        Assertions.assertEquals(Move.HIT, player1GameBoard.hit(2, 3)) // hit
        Assertions.assertTrue(player2TrackBoard.mark(2, 3, Cell.HIT_SHIP))

        // player 1 turn hitting: 9 9
        Assertions.assertEquals(Move.NO_HIT, player2GameBoard.hit(9, 9))// no hit
        Assertions.assertTrue(player1TrackBoard.mark(9, 9, Cell.HIT_NOTHING))

        // player 2 turn hitting: 2 4
        Assertions.assertEquals(Move.GAME_OVER, player1GameBoard.hit(2, 4)) // hit and sunk and won
        Assertions.assertTrue(player2TrackBoard.mark(2, 4, Cell.HIT_SHIP))

        //player 2 won
        println(player1GameBoard)
        println(player1TrackBoard)
        println(player2GameBoard)
        println(player2TrackBoard)
    }

}
