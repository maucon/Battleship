package de.twintorx.battleship

import de.twintorx.battleship.game.Mark
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
        Assertions.assertTrue(trackBoard.mark(1, 1, Mark.HIT_NOTHING))
        Assertions.assertFalse(trackBoard.mark(1, 1, Mark.HIT_NOTHING))
        Assertions.assertFalse(trackBoard.mark(1, 1, Mark.SHIP))
        Assertions.assertFalse(trackBoard.mark(1, 1, Mark.WATER))
    }

    @Test
    fun testToString() {
        trackBoard.mark(1, 1, Mark.HIT_NOTHING)
        trackBoard.mark(4, 4, Mark.HIT_NOTHING)
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
