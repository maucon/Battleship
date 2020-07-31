package test.de.twintorx.battleship

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ConnectionTest {

    @Test
    fun testConnection() {
        val reader = ByteArrayOutputStream()
        System.setOut(PrintStream(reader))

        var server: Server? = null
        runBlocking {
            val host = GlobalScope.launch {
                val hostServer = GlobalScope.launch {
                    server = Server()
                    server!!.start()
                }
                val clientHost = Client()
                clientHost.sendReadyGetTurn()

                hostServer.join()
            }

            val player2 = GlobalScope.launch {
                val client2 = Client("127.0.0.1")
                client2.sendReadyGetTurn()
            }

            System.err.println("Out was: $reader")
            host.join()
            player2.join()
        }
        server!!.close()

    }
}
