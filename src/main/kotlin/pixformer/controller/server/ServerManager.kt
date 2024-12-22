package pixformer.controller.server

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    fun startServer(port: Int)
    fun connectToServer(port: Int)

    fun connectOrStart(port: Int)
}