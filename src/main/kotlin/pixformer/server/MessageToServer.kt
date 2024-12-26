package pixformer.server

/**
 *
 */
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pixformer.controller.server.ServerManager
import pixformer.controller.server.command.CommandSerializer

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
    fun send(
        manager: ServerManager,
        endpoint: String = Endpoints.WEBSOCKETS,
    ) = runBlocking {
        launch(Dispatchers.IO) {
            val client =
                HttpClient(CIO) {
                    install(WebSockets)
                }

            client.webSocket("ws://localhost:${manager.port}/$endpoint?type=${type.name}") {
                if (manager.playablePlayerIndex != null) {
                    send(Frame.Text(CommandSerializer.serialize(manager.playablePlayerIndex!!, type.name)))
                } else {
                    System.err.println("Player index is not assigned")
                }

                type.send(manager, this)
            }

            client.close()
        }
    }
}
