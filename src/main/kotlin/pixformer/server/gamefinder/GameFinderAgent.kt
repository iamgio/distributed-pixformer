package pixformer.server.gamefinder

/**
 *
 */
interface GameFinderAgent {
    fun getGameIp(name: String): String?

    fun addGame(name: String, ip: String): Boolean

    fun removeGame(name: String): Boolean
}