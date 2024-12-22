package pixformer.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import pixformer.controller.server.ServerManager

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
    )
}

/**
 *
 */
data object PlayerConnectMessage : MessageToServerType {
    override val name = EventType.PLAYER_CONNECT

    override suspend fun send(
        manager: ServerManager,
        session: DefaultClientWebSocketSession,
    ) {
        session.send(Frame.Text("hello"))

        session.incoming.receive().let { frame ->
            if (frame is Frame.Text) {
                val playerIndex = frame.readText().toInt()
                manager.playablePlayerIndex = playerIndex
                println("You were assigned player index $playerIndex")
            }
        }
    }
}

/**
 * A message sent from the client to the server to let the player moves right.
 */
data object PlayerMoveRightMessage : MessageToServerType {
    override val name = EventType.PLAYER_MOVE_RIGHT

    override suspend fun send(
        manager: ServerManager,
        session: DefaultClientWebSocketSession,
    ) {
        session.send(Frame.Text(""))
    }
}

/**
 * A message sent from the client to the server to let the player moves right.
 */
data object PlayerMoveLeftMessage : MessageToServerType {
    override val name = EventType.PLAYER_MOVE_LEFT

    override suspend fun send(
        manager: ServerManager,
        session: DefaultClientWebSocketSession,
    ) {
        session.send(Frame.Text(""))
    }
}

/**
 * A message sent from the client to the server to let the player jump.
 */
data object PlayerJumpMessage : MessageToServerType {
    override val name = EventType.PLAYER_JUMP

    override suspend fun send(
        manager: ServerManager,
        session: DefaultClientWebSocketSession,
    ) {
        session.send(Frame.Text(""))
    }
}
