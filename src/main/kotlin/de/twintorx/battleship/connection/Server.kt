package de.twintorx.battleship.connection

import ClientSocket
import de.twintorx.battleship.game.board.GameBoard
import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.ui.io.ServerMessage
import de.twintorx.battleship.ui.io.Writer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Point
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.URL
import kotlin.random.Random
import kotlin.system.exitProcess

class Server(
        port: Int = 9999
) {
    private val server = ServerSocket(port)
            .also { Writer.println("${ServerMessage.PORT_RUNNING}${it.localPort}") }
    private val clientSockets = mutableMapOf<Boolean, ClientSocket>()
    private var running = true

    fun start() {
        Writer.println("${ServerMessage.LOCAL_ADDRESS} ${InetAddress.getLocalHost().hostAddress}" +
                "\n${ServerMessage.PUBLIC_ADDRESS} ${try {
                    BufferedReader(InputStreamReader(URL("http://checkip.amazonaws.com").openStream())).readLine()
                } catch (e: Exception) {
                    "-"
                }}")

        Writer.println("\n${ServerMessage.WAITING_PLAYER1}")

        // Player 1 connecting
        clientSockets[true] = ClientSocket(server.accept().also {
            Writer.println("${ServerMessage.PLAYER1_CONNECTED}${it.inetAddress.hostAddress}")
        })

        Writer.println("\n${ServerMessage.WAITING_PLAYER2}")

        // Player 2 connecting
        clientSockets[false] = ClientSocket(server.accept().also {
            Writer.println("${ServerMessage.PLAYER2_CONNECTED}${it.inetAddress.hostAddress}")
        })

        // Sending start signal to clients
        clientSockets.values.forEach { it.write(Package()) }

        gameLoop(prepare())
    }

    private fun prepare(): Boolean {
        runBlocking { // wait for players ready signal -> placed their ships
            val answer1 = GlobalScope.launch {
                doSafe { clientSockets[true]!!.read() }
            }
            val answer2 = GlobalScope.launch {
                doSafe { clientSockets[false]!!.read() }
            }

            answer1.join()
            answer2.join()
        }

        // draw starting player
        val startingPlayer = Random.nextBoolean()
        for ((key, value) in clientSockets) {
            value.write(Package(key == startingPlayer)) // sending player starting
        }

        return startingPlayer
    }

    private fun gameLoop(startingPlayer: Boolean) {
        var turn = startingPlayer

        while (running) {
            // sending players attack to other player
            val attack = doSafe { clientSockets[turn]!!.read() }.body as Point
            clientSockets[!turn]!!.write(Package(attack))

            // getting response of player and sending to other player
            val response = doSafe { clientSockets[!turn]!!.read() }.body as Int
            clientSockets[turn]!!.write(Package(response))

            when (Move.values()[response]) {
                Move.NO_HIT -> turn = !turn
                Move.GAME_OVER -> {
                    clientSockets.forEach { (key, value) ->
                        // receive gameBoard and send to other player
                        clientSockets[!key]!!.write(Package(doSafe { value.read() }.body as GameBoard))
                    }
                    close()
                }
                else -> Unit // when move is invalid or move hit ship -> players turn again
            }
        }
    }

    private fun doSafe(method: () -> (Package)) = try {
        method()
    } catch (e: Exception) {
        Writer.print("\n${ServerMessage.GAME_ABORT}")
        close()
        exitProcess(1)
    }

    private fun close() {
        clientSockets.values.forEach(ClientSocket::closeAll)
        server.close()

        running = false
    }
}
