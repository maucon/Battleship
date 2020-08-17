import de.twintorx.battleship.connection.Package
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ClientSocket(
        private val socket: Socket,
        serverClient: Boolean = true
) {
    constructor(ip: String, port: Int) : this(Socket(ip, port), false)

    private val input: ObjectInputStream
    private val output: ObjectOutputStream

    init {
        if (serverClient) {
            input = ObjectInputStream(socket.getInputStream())
            output = ObjectOutputStream(socket.getOutputStream())
        } else {
            output = ObjectOutputStream(socket.getOutputStream())
            input = ObjectInputStream(socket.getInputStream())
        }
    }

    fun write(data: Package) = output.writeObject(data)

    fun read(): Package = input.readObject() as Package

    fun closeAll() {
        input.close()
        output.close()
        socket.close()
    }
}
