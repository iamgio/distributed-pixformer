package pixformer.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import pixformer.controller.server.ServerManager
import pixformer.controller.server.command.CommandSerializer

/**
 * A type of message sent a client to the server as a WebSocket.
 * @see MessageToServer
 */
sealed interface MessageToServerType {
    /**
     * Value for the `type` query parameter.
     */
    val name: String

    /**
     * Sends the message to a WebSocket server.
     * @param session WebSocket session
     */
    suspend fun send(
        manager: ServerManager,
        session: DefaultClientWebSocketSession,
    ) {}
}

/**
 * A message sent from the client to the server to connect.
 * This message begins the communication between the client and the server
 * and will actively start waiting for incoming messages from the client.
 */
data object PlayerConnectMessage : MessageToServerType {
    override val name = EventType.PLAYER_CONNECT

    override suspend fun send(
        manager: ServerManager,
        session: DefaultClientWebSocketSession,
    ) {
        session.send(Frame.Text("hello"))

        // The next frame will contain the player index that the server assigns to the client.
        session.incoming.receive().let { frame ->
            if (frame is Frame.Text) {
                val playerIndex = frame.readText().toInt()
                manager.playablePlayerIndex = playerIndex
                manager.startRealignmentRoutine()
                println("You were assigned player index $playerIndex")
            }
        }

        // Wait for incoming commands that are sent from a client to the server,
        // which then broadcasts them to all connected clients to dispatch them.
        // For example, player movements, jumps, and abilities.
        for (frame in session.incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                val command = CommandSerializer.deserialize(text) ?: continue
                manager.dispatch(command)
            }
        }
    }
}

/**
 * A message sent from the client to the server to let the player move to the right.
 */
data object PlayerMoveRightMessage : MessageToServerType {
    override val name = EventType.PLAYER_MOVE_RIGHT
}

/**
 * A message sent from the client to the server to let the player move to the left.
 */
data object PlayerMoveLeftMessage : MessageToServerType {
    override val name = EventType.PLAYER_MOVE_LEFT
}

/**
 * A message sent from the client to the server to let the player jump.
 */
data object PlayerJumpMessage : MessageToServerType {
    override val name = EventType.PLAYER_JUMP
}

/**
 * A message sent from the client to the server to let the player sprint.
 */
data object PlayerSprintMessage : MessageToServerType {
    override val name = EventType.PLAYER_SPRINT
}

/**
 * A message sent from the client to the server to let the player use an ability.
 */
data object PlayerAbilityMessage : MessageToServerType {
    override val name = EventType.PLAYER_ABILITY
}
