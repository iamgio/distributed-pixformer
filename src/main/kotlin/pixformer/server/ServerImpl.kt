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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pixformer.controller.server.ServerManager
import pixformer.controller.server.command.CommandSerializer
import pixformer.serialization.LevelSerialization
import kotlin.time.Duration.Companion.seconds

/**
 *
 */
class ServerImpl(
    private val manager: ServerManager,
) : Server {
    private lateinit var server: EmbeddedServer<*, *>

    override fun start(port: Int) {
        server = embeddedServer(Netty, port, module = { ApplicationModule(manager) })
        server.start(wait = true)
    }

    override fun stop() {
        server.stop(500, 1000)
    }
}

@Suppress("FunctionName")
fun Application.ApplicationModule(manager: ServerManager) {
    install(WebSockets) {
        pingPeriod = 10.seconds
        timeout = 100.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    monitor.subscribe(ApplicationStarted) {
        launch(Dispatchers.IO) {
            delay(500)
            manager.connectToServer() // Leader-follower pattern
        }
    }

    routing {
        // Shared flow to broadcast messages to all connected clients.
        val messageResponseFlow = MutableSharedFlow<String>()
        val sharedFlow = messageResponseFlow.asSharedFlow()

        suspend fun broadcast(text: String) {
            messageResponseFlow.emit(text)
        }

        suspend fun DefaultWebSocketServerSession.connect() {
            // sessions.add(this)

            val playerIndex =
                manager.players.keys
                    .maxOrNull()
                    ?.plus(1) ?: 0

            send(Frame.Text("$playerIndex"))
            manager.onPlayerConnect(playerIndex)

            launch {
                sharedFlow.collect { text ->
                    outgoing.send(Frame.Text(text))
                }
            }
        }

        webSocket("/${Endpoints.WEBSOCKETS}") {
            when (call.request.queryParameters["type"]) {
                EventType.PLAYER_CONNECT -> {
                    log.info("Connect requested")
                    connect()
                }
            }

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    if (CommandSerializer.isCommand(text)) {
                        println("Broadcasting command $text")
                        broadcast(text)
                    }
                }
            }
        }

        get("/${Endpoints.REALIGN}") {
            val level = manager.levelSupplier() ?: return@get

            val serialized = LevelSerialization.serialize(level)
            call.respondText(serialized)

            // println(LevelSerialization.serialize(level))
        }
    }
}
