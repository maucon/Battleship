package de.twintorx.battleship;

import de.twintorx.battleship.game.Player;
import org.fusesource.jansi.AnsiConsole;

public class Main {
    public static void main(String[] args) {
        if (System.console() != null && System.getenv().get("OS").contains("Windows")) {
            AnsiConsole.systemInstall(); // only needed for windows
        }
//        System.out.println(ansi().eraseLine().eraseLine().render("@|red Hello|@ @|green World|@")); // TODO delete later
        new Player().connect();
        if (System.console() != null && System.getenv().get("OS").contains("Windows")) {
            AnsiConsole.systemUninstall(); // only needed for windows
        }
    }
}
