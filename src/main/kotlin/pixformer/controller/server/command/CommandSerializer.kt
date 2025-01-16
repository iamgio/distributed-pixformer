package pixformer.controller.server.command

import pixformer.server.EventType

/**
 * Serializes and deserializes [Command]s.
 * A command is a string that contains the player index and a command type separated by a delimiter.
 */
class CommandSerializer {
    companion object {
        /**
         * The delimiter that separates the player index and the command type.
         */
        private const val DELIMITER = "|"

        /**
         * Checks if the given string is a command.
         * @param raw the string to check
         * @return `true` if the string is a command
         */
        fun isCommand(raw: String): Boolean = raw.contains(DELIMITER)

        /**
         * Serializes the command.
         * @param playerIndex the index of the player
         * @param type the type of the command
         * @return the serialized command
         */
        fun serialize(
            playerIndex: Int,
            type: String,
        ): String = "$playerIndex$DELIMITER$type"

        /**
         * Deserializes the command.
         * @param raw the serialized command
         * @return the deserialized command or `null` if the string is not a command
         */
        fun deserialize(raw: String): Command? {
            if (!isCommand(raw)) return null

            val (rawPlayerIndex, command) = raw.split(DELIMITER, limit = 2)
            val playerIndex = rawPlayerIndex.toIntOrNull() ?: return null

            return when (command) {
                EventType.PLAYER_MOVE_RIGHT -> MoveRightCommand(playerIndex)
                EventType.PLAYER_MOVE_LEFT -> MoveLeftCommand(playerIndex)
                EventType.PLAYER_JUMP -> JumpCommand(playerIndex)
                EventType.PLAYER_SPRINT -> SprintCommand(playerIndex)
                EventType.PLAYER_ABILITY -> AbilityCommand(playerIndex)
                else -> null
            }
        }
    }
}
