package de.twintorx.battleship.connection

import de.twintorx.battleship.console.ServerMessage
import de.twintorx.battleship.game.Move
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.random.Random

class Server {
    private val server = ServerSocket(9999)
            .also { println("${ServerMessage.PORT_RUNNING}${it.localPort}") }
    private val clientSockets = mutableMapOf<Boolean, Triple<Socket, Scanner, PrintWriter>>()
    private var running = true

    fun start() {
        // Host connecting
        val host = server.accept().also {
            println("${ServerMessage.HOST_CONNECTED}${it.inetAddress.hostAddress}")
        }
        clientSockets[true] = Triple(host, Scanner(host.getInputStream()),
                PrintWriter(OutputStreamWriter(host.getOutputStream()), true))

        println(ServerMessage.WAITING_PLAYER2)

        // Player 2 connecting
        val client2 = server.accept().also {
            println("${ServerMessage.PLAYER2_CONNECTED}${it.inetAddress.hostAddress}")
        }
        clientSockets[false] = Triple(client2, Scanner(client2.getInputStream()),
                PrintWriter(OutputStreamWriter(client2.getOutputStream()), true))

        // Sending start signal to clients
        clientSockets.values.forEach { it.third.println("1") }

        println(ServerMessage.START_PREPARATION)
        val startingPlayer = prepare()

        println(ServerMessage.START_GAME)
        gameLoop(startingPlayer)

        println(ServerMessage.GAME_FINISHED)
    }

    private fun prepare(): Boolean {
        runBlocking { // wait for players ready signal -> placed their ships
            val answer1 = GlobalScope.launch {
                println("${ServerMessage.HOST_IS}${clientSockets[true]!!.second.nextLine()}.")
            }
            val answer2 = GlobalScope.launch {
                println("${ServerMessage.PLAYER2_IS}${clientSockets[false]!!.second.nextLine()}.")
            }

            answer1.join()
            answer2.join()
        }

        // draw starting player
        val startingPlayer = Random.nextBoolean()
        for ((key, value) in clientSockets) {
            value.third.println(if (key == startingPlayer) "1" else "0") // sending player starting
        }

        return startingPlayer
    }

    private fun gameLoop(startingPlayer: Boolean) {
        var turn = startingPlayer

        while (running) {
            // sending players attack to other player
            val attack = clientSockets[turn]!!.second.nextLine()
            clientSockets[!turn]!!.third.println(attack)

            // getting response of player and sending tp other player
            val response = clientSockets[!turn]!!.second.nextLine().toInt()
            clientSockets[turn]!!.third.println(response.toString())

            when (Move.values()[response]) {
                Move.NO_HIT -> turn = !turn
                Move.GAME_OVER -> close()
                else -> Unit // when move is invalid or move hit ship -> players turn again
            }
        }
    }

    fun close() {
        clientSockets.values.flatMap { it.toList() }.forEach(Closeable::close)
        server.close()

        running = false
    }
}
