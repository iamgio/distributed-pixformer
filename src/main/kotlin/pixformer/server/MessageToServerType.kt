package pixformer.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame

/**
 * A type of message sent a client to the server as a WebSocket.
 * @see MessageToServer
 */
sealed interface MessageToServerType {
    /**
     * URL endpoint that the message should be sent to.
     */
    val endpoint: String

    /**
     * Sends the message to a WebSocket server.
     * @param session WebSocket session
     */
    suspend fun send(session: DefaultClientWebSocketSession)
}

/**
 *
 */
data object PlayerConnectMessage : MessageToServerType {
    override val endpoint = "connect"

    override suspend fun send(session: DefaultClientWebSocketSession) {
        session.send(Frame.Text(""))
    }
}

/**
 * A message sent from the client to the server to let the player jump.
 * @param player player ID
 */
data class PlayerJumpMessage(
    private val player: Int,
) : MessageToServerType {
    override val endpoint = "jump"

    override suspend fun send(session: DefaultClientWebSocketSession) {
        session.send(Frame.Text(player.toString()))
    }
}
