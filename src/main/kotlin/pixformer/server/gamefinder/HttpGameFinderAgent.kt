package pixformer.server.gamefinder

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

@Suppress("HttpUrlsUsage")
class HttpGameFinderAgent : GameFinderAgent {

    // Create an HTTP client instance.
    private val client = HttpClient(CIO)

    private val url: String
        get() = "http://$ip:$PORT/"

    /**
     * Queries the game finder server for the IP of the game room with the given name.
     *
     * This method calls the endpoint without an IP parameter.
     * If the room exists, the server will return its IP.
     * If it does not, the server will respond with a Bad Request, and we return null.
     */
    override fun getGameIp(name: String): String? = runBlocking {
        try {
            val url = "$url/get?name=$name"
            val response: HttpResponse = client.get(url)
            response.bodyAsText().takeIf { response.status.value == 200 }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Attempts to add a new game room with the provided name and ip.
     *
     * This method calls the /game endpoint including both 'name' and 'ip'.
     * If the room does not already exist, it will be added and the server will respond
     * with a confirmation message. If the room exists, the stored IP is returned instead.
     *
     * We interpret a response that includes the substring "added with IP" as a successful add.
     */
    override fun addGame(name: String, ip: String): Boolean = runBlocking {
        try {
            val url = "$url/game?name=$name&ip=$ip"
            client.get(url).status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Removes the game room with the given name by calling the /endgame endpoint.
     *
     * Returns true if the room was successfully removed, false otherwise.
     */
    override fun removeGame(name: String): Boolean = runBlocking {
        try {
            val url = "$url/endgame?name=$name"
            client.get(url).status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val PORT = 8083
        var ip = "localhost"
    }
}