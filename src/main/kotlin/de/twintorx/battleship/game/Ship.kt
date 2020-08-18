package de.twintorx.battleship.game

import de.twintorx.battleship.ui.Color

enum class Ship(
        val type: String,
        val color: Color,
        val size: Int
) {
    CARRIER("Carrier", Color.YELLOW, 5),
    BATTLESHIP("Battleship", Color.BLUE, 4),
    CRUISER("Cruiser", Color.MAGENTA, 3),
    SUBMARINE("Submarine", Color.GREEN, 3),
    DESTROYER("Destroyer", Color.CYAN, 2);

    companion object {
        fun getStandardShipSet(): MutableMap<Int, MutableList<Ship>> = mutableMapOf(
                1 to CARRIER * 1,
                2 to BATTLESHIP * 2,
                3 to CRUISER * 3,
                4 to SUBMARINE * 4,
                5 to DESTROYER * 5
        )

        // not used yet
        fun getAvailableShips(): MutableList<String> {
            val ships = AsciiShip.values()
            val lines = mutableListOf<String>()
            val max = ships.maxBy { it.lines.size }!!.lines.size
            for (i in 0..max) {
                // TODO fix
                var line = ""
                ships.forEach {
                    val startIndex = max - (it.lines.size - 1)
                    line += if (startIndex < i) " " * it.lines.maxBy { line -> line.length }!!.length + "\t"
                    else it.lines[i - startIndex] +
                            " " * (it.lines.maxBy { line -> line.length }!!.length - it.lines[i - startIndex].length) +
                            "\t"
                }
                lines.add(line)

            }
            return lines
        }
    }

    // not used yet
    enum class AsciiShip(val lines: MutableList<String>) {
        CARRIER(mutableListOf(
                "                           ]+[",
                "                          --|--",
                "                          |_|__|",
                "                          |____|",
                "___________________________|65|_____________________",
                "  \\\\              \\\\                    (______)     /",
                "   \\\\______________\\\\_______________________________/")),
        BATTLESHIP(mutableListOf(
                "                   |__",
                "                    |\\/",
                "                    ---",
                "                    / | [",
                "             !      | |||",
                "           _/|     _/|-++'",
                "       +  +--|    |--|--|_ |-",
                "     { /|__|  |/\\__|  |--- |||__/",
                "   +---------------___[}-_===_.'____",
                " _/_|__|_____________________________[,---.7",
                "|                                         /",
                " \\______________________________________/"
        )),
        CRUISER(mutableListOf(
                "                      __",
                "              _______/__/_",
                "             /===========| ",
                " ____    ___/____________|_",
                " \\   \\___/_________________\\___",
                "  \\                            |",
                "   \\ __________________________/"
        )),
        SUBMARINE(mutableListOf(
                "                    |   ",
                "                   _|_|",
                "   __         _____|   |____________",
                " >|   \\_____/                       )",
                "  |__ ----_________________________/"
        )),
        DESTROYER(mutableListOf(
                "          ___",
                "  __--A__/__\\_A--_",
                "  \\_______________|"))

    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun Ship.times(i: Int): MutableList<Ship> {
    val list = mutableListOf<Ship>()
    repeat(i) { list.add(this) }
    return list
}

private operator fun String.times(size: Int): String = this.repeat(size)
