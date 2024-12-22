package pixformer.controller.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.io.IOException
import pixformer.model.modelinput.CompleteModelInput
import pixformer.server.MessageToServer
import pixformer.server.PlayerConnectMessage
import pixformer.server.Server
import pixformer.server.ServerImpl
import java.util.*

// todo temporary. allow custom port
const val PORT = 8082

class ServerManagerImpl : ServerManager {
    private var server: Server? = null

    override val players = mutableMapOf<UUID, CompleteModelInput>()

    override var onPlayerConnect: (UUID) -> Unit = {}

    override fun startServer(port: Int) {
        server = ServerImpl(this).also { it.start(port) }
    }

    override fun connectToServer(port: Int) {
        val uuid = UUID.randomUUID()
        MessageToServer(PlayerConnectMessage).send(port = port)
    }

    @Suppress("DeferredResultUnused")
    @OptIn(DelicateCoroutinesApi::class)
    override fun connectOrStart(port: Int) {
        GlobalScope.async {
            try {
                connectToServer(port)
            } catch (e: IOException) {
                startServer(port)
                connectToServer(port)
            }
        }
    }
}
