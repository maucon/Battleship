package test.de.twintorx.battleship

import main.de.twintorx.battleship.console.InputRegex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InputRegexTest {

    @Test
    fun testYesNo() {
        Assertions.assertTrue(InputRegex.YES_OR_NO.matches("y"))
        Assertions.assertTrue(InputRegex.YES_OR_NO.matches("Y"))
        Assertions.assertTrue(InputRegex.YES_OR_NO.matches("n"))
        Assertions.assertTrue(InputRegex.YES_OR_NO.matches("N"))

        Assertions.assertFalse(InputRegex.YES_OR_NO.matches("k"))
        Assertions.assertFalse(InputRegex.YES_OR_NO.matches("2"))
        Assertions.assertFalse(InputRegex.YES_OR_NO.matches("j2"))
    }

    @Test
    fun testPlaceShip() {
        Assertions.assertTrue(InputRegex.PLACE_SHIP.matches("ha5"))
        Assertions.assertTrue(InputRegex.PLACE_SHIP.matches("Vd1"))
        Assertions.assertTrue(InputRegex.PLACE_SHIP.matches("hj8"))
        Assertions.assertTrue(InputRegex.PLACE_SHIP.matches("vb9"))
        Assertions.assertTrue(InputRegex.PLACE_SHIP.matches("Ha10"))

        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("8u231"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("ha"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("Ha0"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("Vk19"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("ga1"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("h1a"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("1h1"))
        Assertions.assertFalse(InputRegex.PLACE_SHIP.matches("va11"))
    }

    @Test
    fun testSelectShip() {
        Assertions.assertTrue(InputRegex.SELECT_SHIP.matches("1"))
        Assertions.assertTrue(InputRegex.SELECT_SHIP.matches("3"))
        Assertions.assertTrue(InputRegex.SELECT_SHIP.matches("5"))

        Assertions.assertFalse(InputRegex.SELECT_SHIP.matches("0"))
        Assertions.assertFalse(InputRegex.SELECT_SHIP.matches("6"))
        Assertions.assertFalse(InputRegex.SELECT_SHIP.matches("a"))
        Assertions.assertFalse(InputRegex.SELECT_SHIP.matches("1a"))
    }

    @Test
    fun testShootCell() {
        Assertions.assertTrue(InputRegex.SHOOT_CELL.matches("a1"))
        Assertions.assertTrue(InputRegex.SHOOT_CELL.matches("B6"))
        Assertions.assertTrue(InputRegex.SHOOT_CELL.matches("J10"))

        Assertions.assertFalse(InputRegex.SHOOT_CELL.matches("a0"))
        Assertions.assertFalse(InputRegex.SHOOT_CELL.matches("U9"))
        Assertions.assertFalse(InputRegex.SHOOT_CELL.matches("aa"))
        Assertions.assertFalse(InputRegex.SHOOT_CELL.matches("01"))
    }
}
