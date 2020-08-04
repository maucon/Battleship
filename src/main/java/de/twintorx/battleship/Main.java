package de.twintorx.battleship;

import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        if (System.console() != null && System.getenv().get("OS").contains("Windows")) {
            AnsiConsole.systemInstall(); // only needed for windows
        }
        System.out.println(ansi().eraseScreen().render("@|red Hello|@ @|green World|@")); // TODO erase screen clears screen
//        new Player().connect();
        if (System.console() != null && System.getenv().get("OS").contains("Windows")) {
            AnsiConsole.systemUninstall(); // only needed for windows
        }
    }
}
