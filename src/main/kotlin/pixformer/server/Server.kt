package pixformer.server

/**
 * A generic server that can be started and stopped.
 */
interface Server {
    /**
     * Starts the server on the given port.
     */
    fun start(port: Int)

    /**
     * Stops the server.
     */
    fun stop()
}
