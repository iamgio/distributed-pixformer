package pixformer.controller.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.io.IOException
import pixformer.model.Level
import pixformer.model.modelinput.CompleteModelInput
import pixformer.serialization.SerializableLevelData
import pixformer.server.MessageToServer
import pixformer.server.PlayerConnectMessage
import pixformer.server.Server
import pixformer.server.ServerImpl
import java.util.UUID
import kotlin.concurrent.thread

// todo temporary. allow custom port
const val PORT = 8082

@Suppress("DeferredResultUnused")
@OptIn(DelicateCoroutinesApi::class)
class ServerManagerImpl : ServerManager {
    private var server: Server? = null

    override val players = mutableMapOf<UUID, CompleteModelInput>()

    override var onPlayerConnect: (UUID) -> Unit = {}
    override var onRealign: (SerializableLevelData) -> Unit = {}
    override lateinit var levelSupplier: () -> Level?

    override fun startServer(port: Int) {
        server = ServerImpl(this).also { it.start(port) }
    }

    override fun connectToServer(port: Int) {
        val uuid = UUID.randomUUID()
        MessageToServer(PlayerConnectMessage).send(port = port)
    }

    override fun connectOrStart(port: Int) {
        GlobalScope.async {
            try {
                connectToServer(port)
            } catch (e: IOException) {
                startServer(port)
                connectToServer(port)
            }
        }

        thread(start = true) { setupAlignmentRoutine() }
    }

    private fun setupAlignmentRoutine() {
        // each 5 seconds, send a request to /align
        while (true) {
            println("REALIGNING")

            try {
                RealignRequest().send(port = PORT, manager = this)
            } catch (ignored: IOException) {
            }

            Thread.sleep(5000)
        }
    }
}
