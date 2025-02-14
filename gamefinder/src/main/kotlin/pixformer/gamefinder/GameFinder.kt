package pixformer.gamefinder

import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.concurrent.ConcurrentHashMap

private const val PORT = 8083

fun main() {
    // In-memory store for game rooms.
    // For thread safety in production, consider using a ConcurrentHashMap.
    val games: MutableMap<String, String> = ConcurrentHashMap<String, String>()

    suspend fun RoutingCall.errorMissingParameter(name: String) {
        respondText("Missing or empty '$name' parameter.", status = HttpStatusCode.BadRequest)
    }

    embeddedServer(Netty, port = PORT) {
        routing {
            get("/get") {
                // Extract query parameters
                val name = call.request.queryParameters["name"]

                when {
                    name.isNullOrBlank() -> {
                        call.errorMissingParameter("name")
                    }

                    name in games -> {
                        // Room exists: return the stored IP address.
                        val ip = games[name]!!
                        call.respondText(ip)
                    }

                    else -> {
                        call.respondText("Game room '$name' not found.", status = HttpStatusCode.NotFound)
                    }
                }
            }

            get("/add") {
                // Extract query parameters
                val name = call.request.queryParameters["name"]
                val ip = call.request.queryParameters["ip"]

                when {
                    name.isNullOrBlank() -> {
                        call.errorMissingParameter("name")
                    }

                    ip.isNullOrBlank() -> {
                        call.errorMissingParameter("ip")
                    }

                    name in games -> {
                        // Room exists.
                        call.respondText("Game room '$name' already exists with IP: ${games[name]!!}", status = HttpStatusCode.Conflict)
                    }

                    else -> {
                        // Store the new game room entry.
                        games[name] = ip
                        call.respondText("Game room '$name' added with IP: $ip")
                    }
                }
            }

            get("/end") {
                val name = call.request.queryParameters["name"]

                when {
                    name.isNullOrBlank() -> {
                        call.errorMissingParameter("name")
                    }

                    // Remove the game room entry if it exists.
                    games.remove(name) != null -> {
                        call.respondText("Game room '$name' removed.")
                    }

                    else -> {
                        call.respondText("Game room '$name' not found.", status = HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }.start(wait = true)
}
