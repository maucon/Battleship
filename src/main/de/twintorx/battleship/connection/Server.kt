package main.de.twintorx.battleship.connection

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import main.de.twintorx.battleship.game.Move
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.random.Random

class Server {
    private val server = ServerSocket(9999)
            .also { println("[Server] Server running on port ${it.localPort}") }
    private val clientSockets = mutableMapOf<Boolean, Triple<Socket, Scanner, PrintWriter>>()
    private var running = true

    fun start() {
        val host = server.accept().also {
            println("[Server] Client connected as host: ${it.inetAddress.hostAddress}")
        }
        clientSockets[true] = Triple(host, Scanner(host.getInputStream()),
                PrintWriter(OutputStreamWriter(host.getOutputStream()), true)) // TODO maybe add outputsreamwriter

        println("[Server] Waiting for another player to connect...")

        val client2 = server.accept().also {
            println("[Server] Client connected as second player: ${it.inetAddress.hostAddress}")
        }
        clientSockets[false] = Triple(client2, Scanner(client2.getInputStream()),
                PrintWriter(OutputStreamWriter(client2.getOutputStream()), true))// TODO maybe add outputsreamwriter

        clientSockets.values.forEach { it.third.println("1") }
        println("[Server] Starting preparation")

        prepare()
        startLoop()

        println("[Server] Game finished!")
    }

    private fun prepare() = runBlocking {
        val answer1 = GlobalScope.launch {
            println("[Server] Host is ${clientSockets[true]!!.second.nextLine()}")
        }
        val answer2 = GlobalScope.launch {
            println("[Server] Player2 is ${clientSockets[false]!!.second.nextLine()}")
        }

        answer1.join()
        answer2.join()
    }

    private fun startLoop() {
        var turn = Random.nextBoolean()

        println("[Server] Starting the game")

        for ((key, value) in clientSockets) {
            value.third.println(if (key == turn) "1" else "0")
        }

        while (running) {
            val attack = clientSockets[turn]!!.second.nextLine()
            clientSockets[!turn]!!.third.println(attack) // TODO implement this in client

            val response = clientSockets[!turn]!!.second.nextLine().toInt()
            clientSockets[turn]!!.third.println(response.toString())

            when (Move.values()[response]) {
                Move.NO_HIT -> turn = !turn
                Move.GAME_OVER -> close()
                else -> Unit
            }
        }
    }

    fun close() {
        clientSockets.values.forEach {
            it.toList().forEach(Closeable::close)
        }

        server.close()
        running = false
    }
}

//// ---------------- Extensions and Overloading ----------------
//fun OutputStream.write(msg: String) {
//    write("$msg\n".toByteArray())
//}
