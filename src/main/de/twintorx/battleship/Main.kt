package main.de.twintorx.battleship

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server

fun main() {
    connectionTest()
}

fun connectionTest() {

    GlobalScope.launch {
        try {
            val server = Server()
            server.start()
            server.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    GlobalScope.launch {
        var clientHost = Client()
        var client2 = Client("127.0.0.1")
    }

    readLine()
}
