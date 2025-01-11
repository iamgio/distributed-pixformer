package pixformer

import pixformer.controller.server.command.CommandSerializer
import pixformer.controller.server.command.JumpCommand
import pixformer.server.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 *
 */
class CommandTest {
    @Test
    fun serialization() {
        val serialized = CommandSerializer.serialize(playerIndex = 3, type = EventType.PLAYER_JUMP)
        assertEquals("3|jump", serialized)

        val deserialized = CommandSerializer.deserialize(serialized)
        assertNotNull(deserialized)
        assertEquals(3, deserialized.playerIndex)
        assertIs<JumpCommand>(deserialized)
    }
}
