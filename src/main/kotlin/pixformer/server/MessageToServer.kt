package pixformer.server

/**
 *
 */
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pixformer.controller.server.ServerManager

/**
 * A message sent from the client to the server as a WebSocket.
 */
data class MessageToServer(
    val type: MessageToServerType,
) {
    /**
     * Asynchronously sends the message to a WebSocket server.
     * @param manager server manager
     */
    fun send(manager: ServerManager) =
        runBlocking {
            launch(Dispatchers.IO) {
                val client =
                    HttpClient(CIO) {
                        install(WebSockets)
                    }

                client.webSocket("ws://localhost:${manager.port}/ws?type=${type.name}&player=${manager.playablePlayerIndex}") {
                    type.send(manager, this)
                }

                client.close()
            }
        }
}
