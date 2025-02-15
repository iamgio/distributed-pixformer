package pixformer.gamefinder

import org.junit.jupiter.api.Test
import pixformer.controller.gamefinder.ServerGameFinderAgent
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for the game finder server and agent.
 */
class GameFinderTest {
    @Test
    fun test() {
        val agent = ServerGameFinderAgent()

        assertFalse(agent.isAccessible())

        thread { startGameFinderServer() }
        Thread.sleep(1000)

        assertTrue(agent.isAccessible())

        assertNull(agent.getGameIp("test1"))
        assertTrue(agent.addGame("test1", "ip1"))
        assertEquals("ip1", agent.getGameIp("test1"))
        assertFalse(agent.addGame("test1", "ip2"))

        assertNull(agent.getGameIp("test2"))
        assertTrue(agent.addGame("test2", "ip2"))
        assertEquals("ip2", agent.getGameIp("test2"))
        assertFalse(agent.addGame("test2", "ip3"))

        assertTrue(agent.removeGame("test1"))
        assertNull(agent.getGameIp("test1"))
    }
}
