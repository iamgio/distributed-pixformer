package pixformer.controller.server

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
    val players: Map<Int, Player>
    var playablePlayerIndex: Int?

    val isLeader: Boolean
    val port: Int

    var onPlayerConnect: (Int) -> Unit
    var onRealign: (LevelData) -> Unit
    var levelSupplier: () -> Level?

    fun startServer()

    fun connectToServer()

    fun connectOrStart()

    fun startRealignmentRoutine(entityFactory: EntityFactory)

    fun disconnect()

    fun dispatch(command: Command)

    fun modelInputMapper(): java.util.function.Function<CompleteModelInput, CompleteModelInput>
}
