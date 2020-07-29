package test.de.twintorx.battleship

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server
import org.junit.jupiter.api.Test

class ConnectionTest {
    private lateinit var server: Server

    @Test
    fun testConnection() {
        GlobalScope.launch {
            try {
                server = Server()
                server.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        GlobalScope.launch {
            var clientHost = Client()
            var client2 = Client("127.0.0.1")
        }
        server.close()
        readLine()
    }
}
