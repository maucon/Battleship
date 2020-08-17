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
    }

    enum class AsciiShip(val lines: MutableList<String>) {
        CARRIER(mutableListOf(
                "                           ]+[             \n",
                "                          --|--         \n",
                "                          |_|__|                       \n",
                "                          |____|\n",
                "___________________________|65|_____________________\n",
                "  \\\\              \\\\                    (______)     /\n",
                "   \\\\______________\\\\_______________________________/")),
        BATTLESHIP(mutableListOf(
                "                   |__\n",
                "                    |\\/\n",
                "                    ---\n",
                "                    / | [\n",
                "             !      | |||\n",
                "           _/|     _/|-++'\n",
                "       +  +--|    |--|--|_ |-\n",
                "     { /|__|  |/\\__|  |--- |||__/\n",
                "   +---------------___[}-_===_.'____               \n",
                " _/_|__|_____________________________[,---.7\n",
                "|                                         /\n",
                " \\______________________________________/"
        )),
        CRUISER(mutableListOf(
                "                      __\n",
                "              _______/__/_\n",
                "             /===========| \n",
                " ____    ___/____________|_\n",
                " \\   \\___/_________________\\___\n",
                "  \\                            |\n",
                "   \\ __________________________/"
        )),
        SUBMARINE(mutableListOf(
                "                    |   \n",
                "                   _|_|\n",
                "   __         _____|   |____________\n",
                " >|   \\_____/                       )\n",
                "  |__ ----_________________________/"
        )),
        DESTROYER(mutableListOf(
                "          ___\n",
                "  __--A__/__\\_A--_\n",
                "  \\_______________|"))

    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun Ship.times(i: Int): MutableList<Ship> {
    val list = mutableListOf<Ship>()
    repeat(i) { list.add(this) }
    return list
}
