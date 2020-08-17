package de.twintorx.battleship

import de.twintorx.battleship.ui.io.InputRegex
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InputRegexTest {

    @Test
    fun testYesNo() {
        assertTrue(InputRegex.YES_OR_NO.matches("y"))
        assertTrue(InputRegex.YES_OR_NO.matches("Y"))
        assertTrue(InputRegex.YES_OR_NO.matches("n"))
        assertTrue(InputRegex.YES_OR_NO.matches("N"))

        assertFalse(InputRegex.YES_OR_NO.matches("k"))
        assertFalse(InputRegex.YES_OR_NO.matches("2"))
        assertFalse(InputRegex.YES_OR_NO.matches("j2"))
    }

    @Test
    fun testPlaceShip() {
        assertTrue(InputRegex.PLACE_SHIP.matches("ha5"))
        assertTrue(InputRegex.PLACE_SHIP.matches("Vd1"))
        assertTrue(InputRegex.PLACE_SHIP.matches("hj8"))
        assertTrue(InputRegex.PLACE_SHIP.matches("vb9"))
        assertTrue(InputRegex.PLACE_SHIP.matches("Ha10"))

        assertFalse(InputRegex.PLACE_SHIP.matches("8u231"))
        assertFalse(InputRegex.PLACE_SHIP.matches("ha"))
        assertFalse(InputRegex.PLACE_SHIP.matches("Ha0"))
        assertFalse(InputRegex.PLACE_SHIP.matches("Vk19"))
        assertFalse(InputRegex.PLACE_SHIP.matches("ga1"))
        assertFalse(InputRegex.PLACE_SHIP.matches("h1a"))
        assertFalse(InputRegex.PLACE_SHIP.matches("1h1"))
        assertFalse(InputRegex.PLACE_SHIP.matches("va11"))
    }

    @Test
    fun testSelectShip() {
        assertTrue(InputRegex.SELECT_SHIP.matches("1"))
        assertTrue(InputRegex.SELECT_SHIP.matches("3"))
        assertTrue(InputRegex.SELECT_SHIP.matches("5"))

        assertFalse(InputRegex.SELECT_SHIP.matches("0"))
        assertFalse(InputRegex.SELECT_SHIP.matches("6"))
        assertFalse(InputRegex.SELECT_SHIP.matches("a"))
        assertFalse(InputRegex.SELECT_SHIP.matches("1a"))
    }

    @Test
    fun testShootCell() {
        assertTrue(InputRegex.SHOOT_CELL.matches("a1"))
        assertTrue(InputRegex.SHOOT_CELL.matches("B6"))
        assertTrue(InputRegex.SHOOT_CELL.matches("J10"))

        assertFalse(InputRegex.SHOOT_CELL.matches("a0"))
        assertFalse(InputRegex.SHOOT_CELL.matches("U9"))
        assertFalse(InputRegex.SHOOT_CELL.matches("aa"))
        assertFalse(InputRegex.SHOOT_CELL.matches("01"))
    }

    @Test
    fun testPort() {
        assertTrue(InputRegex.PORT.matches(""))
        assertTrue(InputRegex.PORT.matches("9218"))
        assertTrue(InputRegex.PORT.matches("1000"))
        assertTrue(InputRegex.PORT.matches("65536"))

        assertFalse(InputRegex.PORT.matches("65537"))
        assertFalse(InputRegex.PORT.matches("999"))
        assertFalse(InputRegex.PORT.matches("01923"))
        assertFalse(InputRegex.PORT.matches("187"))
        assertFalse(InputRegex.PORT.matches("0"))
        assertFalse(InputRegex.PORT.matches("hello"))
    }
}
