package pixformer.server

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.engine.EmbeddedServer
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
import pixformer.model.modelinput.HorizontalModelInput
import pixformer.model.modelinput.JumpModelInput
import pixformer.model.modelinput.ModelInput
import pixformer.serialization.LevelSerialization
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

/**
 *
 */
class ServerImpl(
    private val manager: ServerManager,
) : Server {
    private lateinit var server: EmbeddedServer<*, *>

    override fun start(port: Int) {
        server = embeddedServer(Netty, port, module = { ApplicationModule(manager) }).start(wait = true)
    }

    override fun stop() {
        server.stop(1000, 1000)
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
                EventType.PLAYER_CONNECT -> {
                    log.info("Connect requested")

                    val playerIndex =
                        manager.players.keys
                            .maxOrNull()
                            ?.plus(1) ?: 0

                    send(Frame.Text("$playerIndex"))
                    manager.onPlayerConnect(playerIndex)

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
                }

                EventType.PLAYER_MOVE_RIGHT -> input<HorizontalModelInput>(manager)?.right()
                EventType.PLAYER_MOVE_LEFT -> input<HorizontalModelInput>(manager)?.left()
                EventType.PLAYER_JUMP -> input<JumpModelInput>(manager)?.jump()
            }
        }

        get("/${Endpoints.REALIGN}") {
            val level = manager.levelSupplier() ?: return@get

            val serialized = LevelSerialization.serialize(level)
            call.respondText(serialized)
        }
    }
}

private suspend inline fun <reified T : ModelInput> DefaultWebSocketServerSession.input(manager: ServerManager): T? {
    incoming.receive()

    val playerIndex = call.request.queryParameters["player"]?.toIntOrNull() ?: return null
    val player = manager.players[playerIndex] ?: return null
    return player.inputComponent.getOrNull() as? T
}
