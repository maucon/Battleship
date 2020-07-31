package test.de.twintorx.battleship

import main.de.twintorx.battleship.game.Cell
import main.de.twintorx.battleship.game.board.TrackBoard
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TrackBoardTest {

    private lateinit var trackBoard: TrackBoard

    @BeforeEach
    fun setUp() {
        trackBoard = TrackBoard(5)
    }

    @Test
    fun testMark() {
        Assertions.assertTrue(trackBoard.mark(1, 1, Cell.HIT_NOTHING))
        Assertions.assertFalse(trackBoard.mark(1, 1, Cell.HIT_NOTHING))
        Assertions.assertFalse(trackBoard.mark(1, 1, Cell.SHIP))
        Assertions.assertFalse(trackBoard.mark(1, 1, Cell.WATER))
    }

    @Test
    fun testToString() {
        trackBoard.mark(1, 1, Cell.HIT_NOTHING)
        trackBoard.mark(4, 4, Cell.HIT_NOTHING)
        Assertions.assertEquals("  ┌───┬───┬───┬───┬───┐\n5 │   │   │   │   │ x │\n  ├───┼───┼───┼───┼───┤\n4 │   │   │   │   │   │\n  ├───┼───┼───┼───┼───┤\n3 │   │   │   │   │   │\n  ├───┼───┼───┼───┼───┤\n2 │   │ x │   │   │   │\n  ├───┼───┼───┼───┼───┤\n1 │   │   │   │   │   │\n  └───┴───┴───┴───┴───┘\n    A   B   C   D   E"
                , trackBoard.toString())
    }
}
