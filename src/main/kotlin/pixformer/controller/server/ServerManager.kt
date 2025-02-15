package pixformer.controller.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import pixformer.controller.server.command.Command
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.modelinput.CompleteModelInput

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    /**
     * The name of the game (level) that is being played.
     */
    val gameName: String

    /**
     * Players that have connected to the server.
     * Associates player index with its player object.
     */
    val players: MutableMap<Int, Player>

    /**
     * Index of the player that is currently being played by the client.
     */
    var playablePlayerIndex: Int?

    /**
     * The session object that is used to communicate with the server.
     */
    var session: DefaultClientWebSocketSession?

    /**
     * Whether this client is also the server.
     */
    val isLeader: Boolean

    /**
     * The host on which the server is running.
     */
    val host: String

    /**
     * The port on which the server is running.
     */
    val port: Int

    /**
     * The address of the server in the format "host:port".
     */
    val address: String
        get() = "$host:$port"

    /**
     * Action run the reconciliation process is performed.
     */
    val onRealign: (LevelData) -> Unit

    /**
     * Action run when a player connects to the server.
     * Runs only if [isLeader] is true.
     */
    val onPlayerConnect: (Int) -> Unit

    /**
     * Supplier of the level that is currently being played.
     */
    var levelSupplier: () -> Level?

    /**
     * Starts the server.
     */
    fun startServer()

    /**
     * Connects to the server.
     */
    fun connectToServer()

    /**
     * Attempts a connection to the server,
     * otherwise starts the server, becomes leader and connects to the server.
     */
    fun connectOrStart()

    /**
     * Sets the periodic reconciliation process up.
     */
    fun startRealignmentRoutine()

    /**
     * Disconnects from the server, and also stops the server if this client is the leader.
     */
    fun disconnect()

    /**
     * Dispatches a command (e.g. player movement) received from the server.
     * @param command the command to dispatch
     */
    fun dispatch(command: Command)

    /**
     * Adds a listener to the dispatch event.
     * @param onDispatch the listener to add
     */
    fun addOnDispatch(onDispatch: (Command) -> Unit)

    /**
     * Gracefully disconnects a player from the server.
     */
    fun playerDisconnected(playerIndex: Int)

    /**
     * Given a model input, returns a wrapper (decorator) of it that also sends command messages to the server.
     * @see ServerEventCompleteModelInputDecorator
     */
    fun modelInputMapper(): java.util.function.Function<CompleteModelInput, CompleteModelInput>
}
