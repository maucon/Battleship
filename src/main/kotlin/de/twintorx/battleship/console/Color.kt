package de.twintorx.battleship.console

enum class Color(
        private val value: String
) {
    DEFAULT("@|default"),
    WHITE("@|white"),
    RED("@|red"),
    GREEN("@|green"),
    BLUE("@|blue"),
    YELLOW("@|yellow"),
    CYAN("@|cyan"),
    MAGENTA("@|magenta"),
    BLACK("q|black");

    fun paint(msg: String): String {
        return "${this.value} $msg|@"
    }
}
