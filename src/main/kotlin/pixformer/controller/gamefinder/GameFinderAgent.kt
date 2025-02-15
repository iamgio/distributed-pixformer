package pixformer.controller.gamefinder

/**
 * A communication bridge for finding games.
 */
interface GameFinderAgent {
    /**
     * Checks if the game finder is available.
     */
    fun isAccessible(): Boolean

    /**
     * Returns the IP address of the game with the given name, if available.
     */
    fun getGameIp(name: String): String?

    /**
     * Adds a new game with the given name and IP.
     * @return whether the game was successfully added
     */
    fun addGame(
        name: String,
        ip: String,
    ): Boolean

    /**
     * Removes the game with the given name.
     * @return whether the game was successfully removed
     */
    fun removeGame(name: String): Boolean
}
