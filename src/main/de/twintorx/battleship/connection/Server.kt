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
        println("Server running on port ${server.localPort}")
    }

    fun start() {
        println("Waiting for another client to connect")
        val host = server.accept().also {
            println("Client connected as host: ${it.inetAddress.hostAddress}")
        }
        clientSockets[true] = Triple(host, Scanner(host.getInputStream()), host.getOutputStream())
        println("Waiting for another player to connect...")
        val client2 = server.accept().also {
            println("Client connected as second player: ${it.inetAddress.hostAddress}")
        }
        clientSockets[false] = Triple(client2, Scanner(client2.getInputStream()), client2.getOutputStream())
        println("[Server] Starting the game")
        clientSockets.values.map { it.third }.forEach { it.write(1) }
        prepare()
        startLoop()
        println("[Server] Game finished!")
    }

    private fun prepare() = runBlocking {
        val answer1 = GlobalScope.launch {
            clientSockets[true]!!.second.next()
        }
        val answer2 = GlobalScope.launch {
            clientSockets[false]!!.second.next()
        }
        answer1.join()
        answer2.join()
    }

    private fun startLoop() = GlobalScope.launch {
        var turn = Random.nextBoolean()
        while (running) {
            val attack = clientSockets[turn]!!.second.nextLine()
            clientSockets[!turn]!!.third.write(attack.toByteArray())

            val response = clientSockets[!turn]!!.second.nextLine().toInt()
            clientSockets[turn]!!.third.write(response)

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
