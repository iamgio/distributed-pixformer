package pixformer.controller.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.io.IOException
import pixformer.controller.realign.Realigner
import pixformer.controller.server.command.Command
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.EntityFactory
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.modelinput.CompleteModelInput
import pixformer.server.MessageToServer
import pixformer.server.PlayerConnectMessage
import pixformer.server.Server
import pixformer.server.ServerImpl
import kotlin.concurrent.thread
import kotlin.jvm.optionals.getOrNull

// todo temporary. allow custom port
const val PORT = 8082

private const val ALIGNMENT_INTERVAL = 1500

@Suppress("DeferredResultUnused")
@OptIn(DelicateCoroutinesApi::class)
class ServerManagerImpl : ServerManager {
    private var server: Server? = null
    private var alignmentThread: Thread? = null
    private val realigner = Realigner(this)

    override val port: Int = PORT
    override val isLeader: Boolean
        get() = server != null

    override val players = mutableMapOf<Int, Player>()
    override var playablePlayerIndex: Int? = null

    override var session: DefaultClientWebSocketSession? = null

    override val onRealign: (LevelData) -> Unit
    override var onPlayerConnect: (Int) -> Unit = {}
    override lateinit var levelSupplier: () -> Level?

    init {
        onRealign = { data ->
            levelSupplier()?.let { level ->
                realigner.realign(data, level)
            }
        }
    }

    override fun startServer() {
        server = ServerImpl(this)
        server!!.start(port)
    }

    override fun connectToServer() {
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
    }

    override fun startRealignmentRoutine(entityFactory: EntityFactory) {
        alignmentThread =
            thread(start = true) {
                // each 5 seconds, send a request to /align
                while (true) {
                    if (isLeader) {
                        // If the client is the leader, thus it's also the server, the realignment is not needed.
                        break
                    }

                    println("Aligning with server")

                    try {
                        RealignRequest().send(entityFactory, this)
                    } catch (ignored: IOException) {
                    }

                    try {
                        Thread.sleep(ALIGNMENT_INTERVAL.toLong())
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
    }

    override fun disconnect() {
        println("Disconnecting from server")
        server?.stop()
        alignmentThread?.interrupt()
    }

    override fun dispatch(command: Command) {
        if (command.playerIndex == playablePlayerIndex) return

        println("Dispatching command: $command on player index ${command.playerIndex}")

        val player = players[command.playerIndex] ?: return

        val model = player.inputComponent.getOrNull() as? CompleteModelInput ?: return
        command.execute(model)
    }

    override fun modelInputMapper(): java.util.function.Function<CompleteModelInput, CompleteModelInput> =
        java.util.function.Function {
            ServerEventCompleteModelInputDecorator(it, this)
        }
}
