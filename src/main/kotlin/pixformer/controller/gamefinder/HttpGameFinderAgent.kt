package pixformer.controller.gamefinder

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

/**
 * Implementation of [GameFinderAgent] that communicates with the game finder server via HTTP.
 */
@Suppress("HttpUrlsUsage")
class HttpGameFinderAgent : GameFinderAgent {
    private val client = HttpClient(CIO)

    private val url: String
        get() = "http://$ip:$PORT"

    override fun isAccessible(): Boolean =
        runBlocking {
            try {
                val url = "$url/check"
                client.get(url).status.value == 200
            } catch (e: Exception) {
                false
            }
        }

    override fun getGameIp(name: String): String? =
        runBlocking {
            try {
                val response: HttpResponse =
                    client.get("$url/get") {
                        url { parameters.append("name", name) }
                    }
                response.bodyAsText().takeIf { response.status.value == 200 }
            } catch (e: Exception) {
                null
            }
        }

    override fun addGame(
        name: String,
        ip: String,
    ): Boolean =
        runBlocking {
            try {
                client
                    .get("$url/add") {
                        url {
                            parameters.append("name", name)
                            parameters.append("ip", ip)
                        }
                    }.status.value == 200
            } catch (e: Exception) {
                false
            }
        }

    override fun removeGame(name: String): Boolean =
        runBlocking {
            try {
                client
                    .get("$url/remove") {
                        url { parameters.append("name", name) }
                    }.status.value == 200
            } catch (e: Exception) {
                false
            }
        }

    companion object {
        const val PORT = 8083
        var ip = "localhost"
    }
}
