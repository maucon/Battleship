package test.de.twintorx.battleship

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server
import org.junit.jupiter.api.Test

class ConnectionTest {

    @Test
    fun testConnection() {
        var server: Server? = null
        runBlocking {
            val host = GlobalScope.launch {
                val hostServer = GlobalScope.launch {
                    server = Server()
                    server!!.start()
                }
                val clientHost = Client()
                val turn = clientHost.sendReadyGetTurn()

                hostServer.join()
            }

            val player2 = GlobalScope.launch {
                val client2 = Client("127.0.0.1")
                val turn = client2.sendReadyGetTurn()
            }

            host.join()
            player2.join()
        }
        server!!.close()
    }
}
