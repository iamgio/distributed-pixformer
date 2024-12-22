package pixformer.server

/**
 *
 */
interface Server {
    fun start(port: Int)

    fun stop()
}
