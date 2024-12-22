package pixformer.controller.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.io.IOException
import pixformer.model.Level
import pixformer.model.entity.dynamic.player.Player
import pixformer.serialization.SerializableLevelData
import pixformer.server.MessageToServer
import pixformer.server.PlayerConnectMessage
import pixformer.server.Server
import pixformer.server.ServerImpl
import kotlin.concurrent.thread

// todo temporary. allow custom port
const val PORT = 8082

private const val ALIGNMENT_INTERVAL = 1500

@Suppress("DeferredResultUnused")
@OptIn(DelicateCoroutinesApi::class)
class ServerManagerImpl : ServerManager {
    private var server: Server? = null
    private var alignmentThread: Thread? = null

    override val port: Int = PORT

    override val players = mutableMapOf<Int, Player>()
    override var playablePlayerIndex: Int? = null

    override var onPlayerConnect: (Int) -> Unit = {}
    override var onRealign: (SerializableLevelData) -> Unit = {}
    override lateinit var levelSupplier: () -> Level?

    override fun startServer() {
        server = ServerImpl(this).also { it.start(port) }
    }

    override fun connectToServer() {
        // client = Client(port = port).also { it.connect() }
        MessageToServer(PlayerConnectMessage).send(this)
    }

    override fun connectOrStart() {
        GlobalScope.async {
            try {
                connectToServer()
            } catch (e: IOException) {
                startServer()
            }
        }

        alignmentThread = thread(start = true) { setupAlignmentRoutine() }
    }

    private fun setupAlignmentRoutine() {
        // each 5 seconds, send a request to /align
        while (true) {
            println("Aligning with server")

            try {
                RealignRequest().send(port = PORT, manager = this)
            } catch (ignored: IOException) {
            }

            try {
                Thread.sleep(ALIGNMENT_INTERVAL.toLong())
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    override fun disconnect() {
        println("Disconnecting from server")
        server?.stop()
        alignmentThread?.interrupt()
    }
}
