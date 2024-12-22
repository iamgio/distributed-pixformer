package pixformer.controller.server

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pixformer.server.Endpoints

/**
 *
 */
class RealignRequest {
    /**
     * Asynchronously sends the message to a WebSocket server.
     * @param host host of the server
     * @param port port of the server
     */
    fun send(
        host: String = "localhost",
        port: Int,
    ) = runBlocking {
        launch(Dispatchers.IO) {
            val client = HttpClient(CIO)

            val response = client.get("http://$host:$port/${Endpoints.REALIGN}")
            println(response.bodyAsText())

            client.close()
        }
    }
}
