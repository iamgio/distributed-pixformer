package pixformer.server

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 *
 */
class ServerImpl : Server {
    override fun start(port: Int) {
        embeddedServer(Netty, port, module = Application::ApplicationModule).start(wait = true)
    }

    override fun stop() {
        println("Server stopped")
    }
}

@Suppress("FunctionName")
fun Application.ApplicationModule() {
    // Shared flow to broadcast messages to all connected clients.
    val messageResponseFlow = MutableSharedFlow<String>()
    val sharedFlow = messageResponseFlow.asSharedFlow()

    install(WebSockets) {
        pingPeriod = 1.seconds
        timeout = 10.seconds
        maxFrameSize = Long.MAX_VALUE
    }

    routing {
        webSocket("/${Endpoints.PLAYER_CONNECT}") {
            log.info("Connect requested")

            // Forward messages to all connected clients as a broadcast.

            val job =
                launch {
                    sharedFlow.collect { message ->
                        send(Frame.Text(message))
                    }
                }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        messageResponseFlow.emit(receivedText)
                    }
                }
            }.onFailure { exception ->
                log.error("WebSocket error: ${exception.message}")
            }.also {
                job.cancel()
            }
        }

        webSocket("/${Endpoints.PLAYER_JUMP}") {
        }
    }
}