package main.de.twintorx.battleship.connection

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import main.de.twintorx.battleship.game.Move
import java.io.Closeable
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.random.Random

class Server() {
    private val server = ServerSocket(9999)
    private val clientSockets = mutableMapOf<Boolean, Triple<Socket, Scanner, OutputStream>>()
    private var running = true

    init {
        println("[Server] Server running on port ${server.localPort}")
    }

    fun start() {
        val host = server.accept().also {
            println("[Server] Client connected as host: ${it.inetAddress.hostAddress}")
        }
        clientSockets[true] = Triple(host, Scanner(host.getInputStream()), host.getOutputStream())
        clientSockets[true]!!.third.write("1")
        println("[Server] Waiting for another player to connect...")
        val client2 = server.accept().also {
            println("[Server] Client connected as second player: ${it.inetAddress.hostAddress}")
        }
        clientSockets[false] = Triple(client2, Scanner(client2.getInputStream()), client2.getOutputStream())
        clientSockets[false]!!.third.write("1")
        println("[Server] Starting preparation")
        prepare()
        startLoop()
        println("[Server] Game finished!")
    }

    private fun prepare() = runBlocking {
        val answer1 = GlobalScope.launch {
            println(clientSockets[true]!!.second.nextLine())
        }
        val answer2 = GlobalScope.launch {
            println(clientSockets[false]!!.second.nextLine())
        }

        answer1.join()
        answer2.join()
    }

    private fun startLoop() = GlobalScope.launch {
        var turn = Random.nextBoolean()
        println("[Server] Starting the game")
        for ((key, value) in clientSockets) {
            value.third.write(if (key == turn) "1" else "0")
        }
        while (running) {
            val attack = clientSockets[turn]!!.second.nextLine()
            clientSockets[!turn]!!.third.write(attack.toByteArray().toString()) // TODO implement this in client

            val response = clientSockets[!turn]!!.second.nextLine().toInt()
            clientSockets[turn]!!.third.write(response.toString())

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

// ---------------- Extensions and Overloading ----------------
 fun OutputStream.write(message: String) {
    this.write("$message\n".toByteArray())
}
