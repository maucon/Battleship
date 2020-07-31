package test.de.twintorx.battleship

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import main.de.twintorx.battleship.connection.Client
import main.de.twintorx.battleship.connection.Server
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream
import java.util.*


class ConnectionTest {

    @Test
    fun testConnection() {
        val startTime = System.currentTimeMillis()
        var server: Server? = null

        runBlocking {
            val pipeOut = PipedOutputStream()
            System.setOut(PrintStream(pipeOut))

            GlobalScope.launch {
                val hostServer = GlobalScope.launch {
                    server = Server()
                    server!!.start()
                }
                val clientHost = Client()
                clientHost.sendReadyGetTurn()

                hostServer.join()
            }

            GlobalScope.launch {
                val client2 = Client("127.0.0.1")
                client2.sendReadyGetTurn()
            }

            val sc = Scanner(PipedInputStream(pipeOut))
            while (sc.nextLine() != "[Server] Starting the game") {
                if (System.currentTimeMillis() - startTime > 5000) {
                    Assertions.fail<String>("Not connected after 5s!")
                }
            }

            server!!.close()
        }

    }
}
