package de.twintorx.battleship.ui.io

enum class InputRegex(
        regexString: String
) {
    YES_OR_NO("[yYnN]"),
    PLACE_SHIP("[hHvV][a-jA-J]([1-9]|10)"),
    SELECT_SHIP("[1-5]"),
    SHOOT_CELL("[a-jA-J]([1-9]|10)");

    private val regex = regexString.toRegex()
    fun matches(string: String) = regex.matches(string)
}
