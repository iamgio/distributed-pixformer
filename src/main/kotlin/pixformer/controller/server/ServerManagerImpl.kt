package pixformer.controller.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.io.IOException
import pixformer.server.MessageToServer
import pixformer.server.PlayerConnectMessage
import pixformer.server.Server
import pixformer.server.ServerImpl

// todo temporary. allow custom port
const val PORT = 8082

class ServerManagerImpl : ServerManager {
    private var server: Server? = null

    override fun startServer(port: Int) {
        server = ServerImpl().also { it.start(port) }
    }

    override fun connectToServer(port: Int) {
        // todo: remove hardcoded player ID
        MessageToServer(PlayerConnectMessage(1)).send(port = port)
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
