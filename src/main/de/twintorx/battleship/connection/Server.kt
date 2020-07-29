package main.de.twintorx.battleship.connection

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class Server() {
    private val server = ServerSocket(9999)
    private val clientSockets = mutableMapOf<Boolean, Triple<Socket, Scanner, OutputStream>>()
    private var running = true
    private val gameLogic = GameLogic()

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
        println("Starting the game")
        startLoop()
    }

    private fun startLoop() = GlobalScope.launch {
        while (running) {
            if (clientSockets[gameLogic.turn]!!.second.hasNextLine()) {
                if (!gameLogic.handleInput(scanner.nextLine())) { // TODO implement
                    clientSockets[indexClientCurrent].getOutputStream()
                            .write("[Error] Invalid input!".toByteArray())
//                    gamelogic.handleInput(input) // validation inside logic ?
                }
//                indexClientCurrent = gameLogic.playerInTurn()
                break
            }
        }
    }

    fun sendHitAnswer(clientIndex: Int, hit: Boolean) { // TODO maybe better directly after hit call
        clientSockets[clientIndex].getOutputStream().write(if (hit) 1 else 0)

        fun close() {
            running = false
            server.close()
            println("Server closed")
        }
    }
