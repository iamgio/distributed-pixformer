package pixformer.controller.server

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import pixformer.controller.server.command.Command
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.EntityFactory
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.modelinput.CompleteModelInput

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    val players: MutableMap<Int, Player>
    var playablePlayerIndex: Int?

    var session: DefaultClientWebSocketSession?

    val isLeader: Boolean
    val port: Int

    val onRealign: (LevelData) -> Unit
    val onPlayerConnect: (Int) -> Unit
    var levelSupplier: () -> Level?

    fun startServer()

    fun connectToServer()

    fun connectOrStart()

    fun startRealignmentRoutine(entityFactory: EntityFactory)

    fun disconnect()

    fun dispatch(command: Command)

    fun addOnDispatch(onDispatch: (Command) -> Unit)

    fun playerDisconnected(playerIndex: Int)

    fun modelInputMapper(): java.util.function.Function<CompleteModelInput, CompleteModelInput>
}
