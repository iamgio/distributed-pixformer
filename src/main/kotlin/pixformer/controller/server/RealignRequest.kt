package pixformer.controller.server

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pixformer.model.entity.EntityFactory
import pixformer.serialization.LevelSerialization
import pixformer.server.Endpoints

/**
 *
 */
class RealignRequest {
    /**
     * Asynchronously sends the message to a WebSocket server.
     * @param manager the server manager
     */
    fun send(
        entityFactory: EntityFactory,
        manager: ServerManager,
    ) = runBlocking {
        launch(Dispatchers.IO) {
            val client = HttpClient(CIO)

            val response = client.get("http://localhost:${manager.port}/${Endpoints.REALIGN}")

            client.close()

            LevelSerialization.deserialize(response.bodyAsText(), entityFactory)?.let { manager.onRealign(it) }
        }
    }
}
