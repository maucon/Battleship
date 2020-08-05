package de.twintorx.battleship

import de.twintorx.battleship.console.Writer
import de.twintorx.battleship.game.Cell
import de.twintorx.battleship.game.board.TrackBoard
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
        Assertions.assertFalse(trackBoard.mark(1, 1, Cell.SHIP_DESTROYER))
        Assertions.assertFalse(trackBoard.mark(1, 1, Cell.WATER))
    }

    @Test
    fun testToString() {
        trackBoard.mark(1, 1, Cell.HIT_NOTHING)
        trackBoard.mark(4, 4, Cell.HIT_NOTHING)
        val lines = trackBoard.getLines()
        Assertions.assertEquals(mutableListOf(
                "  ┌───┬───┬───┬───┬───┐",
                "5 │   │   │   │   │ x │",
                "  ├───┼───┼───┼───┼───┤",
                "4 │   │   │   │   │   │",
                "  ├───┼───┼───┼───┼───┤",
                "3 │   │   │   │   │   │",
                "  ├───┼───┼───┼───┼───┤",
                "2 │   │ x │   │   │   │",
                "  ├───┼───┼───┼───┼───┤",
                "1 │   │   │   │   │   │",
                "  └───┴───┴───┴───┴───┘",
                "    A   B   C   D   E "),
                trackBoard.getLines())
    }
}
