package pixformer.controller.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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
import pixformer.server.gamefinder.GameFinderAgent
import pixformer.server.gamefinder.HttpGameFinderAgent
import kotlin.concurrent.thread
import kotlin.jvm.optionals.getOrNull

// todo temporary. allow custom port
const val PORT = 8082

private const val ALIGNMENT_INTERVAL = 3000

private const val MAX_ATTEMPTS = 1

/**
 * Implementation of [ServerManager].
 */
@Suppress("DeferredResultUnused")
@OptIn(DelicateCoroutinesApi::class)
class ServerManagerImpl(
    override val gameName: String,
) : ServerManager {
    private var running = false
    private var server: Server? = null
    private val realigner = Realigner(this)
    private val gameFinder: GameFinderAgent = HttpGameFinderAgent()

    private val onDispatchListeners = mutableListOf<(Command) -> Unit>()

    private val shutdownHookThreads = mutableSetOf<Thread>()
    private var realignmentThread: Thread? = null

    override var host: String = "localhost"
    override val port: Int = PORT

    override val isLeader: Boolean
        get() = server != null

    override val players = mutableMapOf<Int, Player>()
    override var playablePlayerIndex: Int? = null

    override var session: DefaultClientWebSocketSession? = null

    override val onPlayerConnect: (Int) -> Unit
    override val onRealign: (LevelData) -> Unit
    override lateinit var levelSupplier: () -> Level?

    init {
        onRealign = { data ->
            levelSupplier()?.let { level ->
                realigner.realign(data, level)
            }
        }

        onPlayerConnect = { index ->
            println("Player connected: $index")
            levelSupplier()?.let { level ->
                val player: Player = level.createPlayer(index, true, modelInputMapper())
                players[index] = player
            }
        }
    }

    override fun startServer() {
        server = ServerImpl(this)
        server!!.start(port)

        /*if (!gameFinder.addGame(gameName, host)) {
            throw IOException("Could not add game to game finder.")
        }*/
    }

    override fun connectToServer() {
        running = true

        /*if (!isLeader) {
            gameFinder.getGameIp("pixformer")
                ?.let { host = it }
                ?: throw IOException("Could not retrieve game IP.")

            println("Connecting to server at $address")
        }*/

        // Add a shutdown hook to disconnect from the server.
        Thread { disconnect() }.let {
            shutdownHookThreads += it
            Runtime.getRuntime().addShutdownHook(it)
        }

        // Message to announce a connection, and to keep the session alive to exchange commands.
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
        realignmentThread =
            thread(start = true) {
                var attempt = 0

                // Each N seconds, send a request to /align
                // If the client is the leader, thus it's also the server, the realignment is not needed.
                while (!isLeader) {
                    println("Aligning with server")

                    try {
                        RealignRequest().send(entityFactory, this)
                        attempt = 0
                    } catch (e: IOException) {
                        attempt++
                        System.err.println("Could not realign with server. Attempt $attempt")
                        if (attempt > MAX_ATTEMPTS) {
                            System.err.println("Max attempts reached. Disconnecting from server")
                            disconnect()
                        }
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
        if (!running) return

        println("Disconnecting from server")

        running = false

        // Remove all players. This causes the game to end.
        levelSupplier()?.world?.replaceEntities(emptySet(), { false }, { it is Player })

        // Close the session.
        runBlocking {
            val reason = CloseReason(CloseReason.Codes.NORMAL, "Client disconnected")
            session?.let {
                println("Closing session")
                it.close(reason)
            } ?: System.err.println("Session is null")
        }

        // Stop the realignment routine.
        realignmentThread?.interrupt()

        // Stop the server if this client is the leader.
        server?.let {
            gameFinder.removeGame(gameName)
            thread { it.stop() }
        }

        // Remove the shutdown hooks.
        try {
            shutdownHookThreads.forEach(Runtime.getRuntime()::removeShutdownHook)
        } catch (ignored: IllegalStateException) {
            // Call to disconnect() matches with shutdown.
        }
    }

    override fun dispatch(command: Command) {
        if (command.playerIndex == playablePlayerIndex) return

        println("Dispatching command: $command on player index ${command.playerIndex}")

        onDispatchListeners.forEach { it(command) }

        val player = players[command.playerIndex] ?: return

        val model = player.inputComponent.getOrNull() as? CompleteModelInput ?: return
        command.execute(model)
    }

    override fun addOnDispatch(onDispatch: (Command) -> Unit) {
        onDispatchListeners.add(onDispatch)
    }

    override fun playerDisconnected(playerIndex: Int) {
        val player = players.remove(playerIndex) ?: return
        levelSupplier()?.world?.queueEntityDrop(player)
    }

    override fun modelInputMapper(): java.util.function.Function<CompleteModelInput, CompleteModelInput> =
        java.util.function.Function {
            ServerEventCompleteModelInputDecorator(it, this)
        }
}
