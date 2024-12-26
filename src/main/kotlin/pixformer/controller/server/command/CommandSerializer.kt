package pixformer.controller.server.command

import pixformer.server.EventType

/**
 *
 */
class CommandSerializer {
    companion object {
        private const val DELIMITER = "|"

        fun isCommand(raw: String): Boolean = raw.contains(DELIMITER)

        fun serialize(
            playerIndex: Int,
            type: String,
        ): String = "$playerIndex$DELIMITER$type"

        fun deserialize(raw: String): Command? {
            if (!isCommand(raw)) return null

            val (rawPlayerIndex, command) = raw.split(DELIMITER, limit = 2)
            val playerIndex = rawPlayerIndex.toIntOrNull() ?: return null

            return when (command) {
                EventType.PLAYER_MOVE_RIGHT -> MoveRightCommand(playerIndex)
                EventType.PLAYER_MOVE_LEFT -> MoveLeftCommand(playerIndex)
                EventType.PLAYER_JUMP -> JumpCommand(playerIndex)
                else -> null
            }
        }
    }
}
