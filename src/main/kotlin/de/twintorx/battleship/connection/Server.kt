package de.twintorx.battleship.connection

import de.twintorx.battleship.game.board.Move
import de.twintorx.battleship.ui.io.ServerMessage
import de.twintorx.battleship.ui.io.Writer
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
import kotlin.system.exitProcess

class Server {
    private val server = ServerSocket(9999)
            .also { Writer.print("${ServerMessage.PORT_RUNNING}${it.localPort}") }
    private val clientSockets = mutableMapOf<Boolean, Triple<Socket, Scanner, PrintWriter>>()
    private var running = true

    fun start() {
        // Host connecting
        val host = server.accept().also {
            Writer.print("${ServerMessage.HOST_CONNECTED}${it.inetAddress.hostAddress}")
        }
        clientSockets[true] = Triple(host, Scanner(host.getInputStream()),
                PrintWriter(OutputStreamWriter(host.getOutputStream()), true))

        Writer.print(ServerMessage.WAITING_PLAYER2.toString())

        // Player 2 connecting
        val client2 = server.accept().also {
            Writer.print("${ServerMessage.PLAYER2_CONNECTED}${it.inetAddress.hostAddress}")
        }
        clientSockets[false] = Triple(client2, Scanner(client2.getInputStream()),
                PrintWriter(OutputStreamWriter(client2.getOutputStream()), true))

        // Sending start signal to clients
        clientSockets.values.forEach { it.third.println("1") }

        Writer.print(ServerMessage.START_PREPARATION.toString())
        val startingPlayer = prepare()

        Writer.print(ServerMessage.START_GAME.toString())
        gameLoop(startingPlayer)

        Writer.print(ServerMessage.GAME_FINISHED.toString())
    }

    private fun prepare(): Boolean {
        runBlocking { // wait for players ready signal -> placed their ships
            val answer1 = GlobalScope.launch {
                Writer.print("${ServerMessage.HOST_IS}${clientSockets[true]!!.second.nextLine()}.")
            }
            val answer2 = GlobalScope.launch {
                doSafe { Writer.print("${ServerMessage.PLAYER2_IS}${clientSockets[false]!!.second.nextLine()}.") }
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
            val attack = doSafe { clientSockets[turn]!!.second.nextLine() } as String
            clientSockets[!turn]!!.third.println(attack)

            // getting response of player and sending tp other player
            val response = doSafe { clientSockets[!turn]!!.second.nextLine().toInt() } as Int
            clientSockets[turn]!!.third.println(response.toString())

            when (Move.values()[response]) {
                Move.NO_HIT -> turn = !turn
                Move.GAME_OVER -> close()
                else -> Unit // when move is invalid or move hit ship -> players turn again
            }
        }
    }

    private fun doSafe(method: () -> (Any)) = try {
        method()
    } catch (e: Exception) {
        Writer.print(ServerMessage.GAME_ABORT.toString())
        exitProcess(1)
    }

    private fun close() {
        clientSockets.values.flatMap { it.toList() }.forEach(Closeable::close)
        server.close()

        running = false
    }
}
