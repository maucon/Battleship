package de.twintorx.battleship

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import de.twintorx.battleship.game.Player
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

class PlayerTest {
//    private lateinit var player: Player
//    private lateinit var input: KFunction<*>
//    private lateinit var backup: InputStream
//
//    @BeforeEach
//    fun setUp() {
//        backup = System.`in`
//        player = Player()
//        input = player::class.declaredFunctions.filter { it.name == "input" }[1].apply { isAccessible = true }
//    }
//
//    @Test
//    fun testInput01() = runBlocking {
//        GlobalScope.launch {
//            System.setIn(ByteArrayInputStream("a".toByteArray()))
//            delay(1)
//            System.setIn(ByteArrayInputStream("1".toByteArray()))
//        }
//
//        Assertions.assertEquals(1,
//                input.call(player, "try", { it: String -> it.toIntOrNull() != null }).toString().toInt())
//    }
//
//    @Test
//    fun testInput02() = runBlocking {
//        GlobalScope.launch {
//            System.setIn(ByteArrayInputStream("1234".toByteArray()))
//            delay(1)
//            System.setIn(ByteArrayInputStream("12345".toByteArray()))
//        }
//
//        Assertions.assertEquals("12345",
//                input.call(player, "try", { it: String -> it.length == 5 }).toString())
//    }
//
//    @AfterEach
//    fun tearDown() {
//        System.setIn(backup)
//    }
}
