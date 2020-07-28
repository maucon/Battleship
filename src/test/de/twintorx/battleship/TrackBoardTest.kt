package test.de.twintorx.battleship

import main.de.twintorx.battleship.game.Move
import main.de.twintorx.battleship.game.TrackBoard
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TrackBoardTest {

    private lateinit var trackBoard: TrackBoard

    @BeforeEach
    fun init() {
        trackBoard = TrackBoard(5)
    }

    @Test
    fun testHit() {
        Assertions.assertEquals(Move.NO_HIT, trackBoard.hit(1, 1))
        Assertions.assertEquals(Move.INVALID, trackBoard.hit(1, 1))
    }

    @Test
    fun testToString() {
        trackBoard.hit(1, 1)
        trackBoard.hit(4, 4)
        Assertions.assertEquals("  ┌───┬───┬───┬───┬───┐\n5 │   │   │   │   │ \u001B[31mx\u001B[0m │\n  ├───┼───┼───┼───┼───┤\n4 │   │   │   │   │   │\n  ├───┼───┼───┼───┼───┤\n3 │   │   │   │   │   │\n  ├───┼───┼───┼───┼───┤\n2 │   │ \u001B[31mx\u001B[0m │   │   │   │\n  ├───┼───┼───┼───┼───┤\n1 │   │   │   │   │   │\n  └───┴───┴───┴───┴───┘\n    A   B   C   D   E"
                , trackBoard.toString())
    }
}
