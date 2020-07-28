package main.de.twintorx.battleship.game

open class TrackBoard(
        val size: Int = 10
) {
    protected val grid: Grid = Grid(size)

    open fun hit(x: Int, y: Int): Move {
        // can't hit already hit cells
        if (grid[x, y] == Cell.HIT_SHIP || grid[x, y] == Cell.HIT_NOTHING) return Move.INVALID

        if (grid[x, y] == Cell.SHIP) {
            grid[x, y] = Cell.HIT_SHIP
            return Move.HIT
        }

        grid[x, y] = Cell.HIT_NOTHING
        return Move.NO_HIT
    }

    override fun toString(): String {
        val len = size - 1
        val sizeLength = size.toString().length
        val space = " " * sizeLength
        val table = arrayListOf(" $space┌${"───┬" * len}───┐")
        val div = " $space├${"───┼" * len}───┤"

        grid.values.withIndex().forEach {
            val index = size - it.index

            table add "${" " * (sizeLength - index.toString().length)}$index │" +
                    "${it.value.joinToString("│") { cell ->
                        " ${cell.value} "
                    }}│" add div
        }

        table.removeLast()
        return (table add " $space└${"───┴" * len}───┘"
                add "   $space${(65..(64 + size)).map { it.toChar() }.joinToString("   ")}")
                .joinToString("\n")
    }
}

// ---------------- Extensions and Overloading ----------------
private operator fun String.times(i: Int): String = this.repeat(i)

private infix fun ArrayList<String>.add(element: String): ArrayList<String> {
    add(element)
    return this
}
