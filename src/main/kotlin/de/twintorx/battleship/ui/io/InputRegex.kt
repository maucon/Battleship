package de.twintorx.battleship.ui.io

enum class InputRegex(
        regexString: String
) {
    YES_OR_NO("[yYnN]"),
    PLACE_SHIP("[hHvV][a-jA-J]([1-9]|10)"),
    SELECT_SHIP("[1-6]"),
    SHOOT_CELL("[a-jA-J]([1-9]|10)"),
    PORT("^$|([1-8][0-9]{3}|9[0-8][0-9]{2}|99[0-8][0-9]|999[0-9]|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-6])");

    private val regex = regexString.toRegex()
    fun matches(string: String) = regex.matches(string)
}
