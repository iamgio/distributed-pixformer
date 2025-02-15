package pixformer.server.gamefinder

/**
 * Mock implementation of [GameFinderAgent] that does not store any games,
 * and always points to localhost.
 * This implementation is used when the game finder server is not available.
 */
class LocalGameFinderAgent : GameFinderAgent {
    override fun isAccessible(): Boolean = true

    override fun getGameIp(name: String) = "localhost"

    override fun addGame(
        name: String,
        ip: String,
    ): Boolean = true

    override fun removeGame(name: String): Boolean = true
}
