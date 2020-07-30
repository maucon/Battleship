package main.de.twintorx.battleship.game

import main.de.twintorx.battleship.connection.Client
import java.awt.Point

class Player {
    private lateinit var client: Client
    private lateinit var gameBoard: GameBoard
    private lateinit var trackBoard: TrackBoard

    private fun connect() {
        // TODO UI
        val address = readLine()
        client = Client(address!!)
    }

    private fun prepare() {
        // TODO implement
    }

    private fun shoot() {
        // TODO UI
        client.sendShot(Point(1, 2)) // TODO change later
        update()
    }

    private fun update() {
        // TODO implement
    }

    private fun triggerNotTurnWaiting(){
        // TODO implement
    }
}
