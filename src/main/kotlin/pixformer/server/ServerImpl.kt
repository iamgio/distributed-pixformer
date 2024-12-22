package pixformer.server

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pixformer.controller.server.ServerManager
import pixformer.serialization.LevelSerialization
import kotlin.time.Duration.Companion.seconds

/**
 *
 */
class ServerImpl(
    private val manager: ServerManager,
) : Server {
    override fun start(port: Int) {
        embeddedServer(Netty, port, module = { ApplicationModule(manager) }).start(wait = true)
    }

    override fun stop() {
        println("Server stopped")
    }
}

@Suppress("FunctionName")
fun Application.ApplicationModule(manager: ServerManager) {
    // Shared flow to broadcast messages to all connected clients.
    val messageResponseFlow = MutableSharedFlow<String>()
    val sharedFlow = messageResponseFlow.asSharedFlow()

    install(WebSockets) {
        pingPeriod = 1.seconds
        timeout = 10.seconds
        maxFrameSize = Long.MAX_VALUE
    }

    monitor.subscribe(ApplicationStarted) {
        launch(Dispatchers.IO) {
            delay(500)
            manager.connectToServer() // Leader-follower pattern
        }
    }

    fun DefaultWebSocketServerSession.broadcast() {
        // Forward messages to all connected clients as a broadcast.
        val job =
            launch {
                sharedFlow.collect { message ->
                    send(Frame.Text(message))
                }
            }
    }

    routing {
        webSocket("/${Endpoints.WEBSOCKETS}") {
            when (call.request.queryParameters["type"]) {
                "connect" -> {
                    log.info("Connect requested")

                    runCatching {
                        incoming.consumeEach { frame ->
                            if (frame is Frame.Text) {
                                val receivedText = frame.readText()
                                messageResponseFlow.emit(receivedText)
                            }
                        }
                    }.onFailure { exception ->
                        log.error("WebSocket error: ${exception.message}")
                    }

                    manager.onPlayerConnect(
                        manager.players.keys
                            .maxOrNull()
                            ?.plus(1) ?: 0,
                    )
                }
            }
        }

        get("/${Endpoints.REALIGN}") {
            val level = manager.levelSupplier() ?: return@get

            val serialized = LevelSerialization.serialize(level)
            call.respondText(serialized)
        }
    }
}
