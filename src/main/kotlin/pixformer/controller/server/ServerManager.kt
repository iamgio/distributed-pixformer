package pixformer.controller.server

import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.EntityFactory
import pixformer.model.entity.dynamic.player.Player

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    val players: Map<Int, Player>
    var playablePlayerIndex: Int?

    val port: Int

    var onPlayerConnect: (Int) -> Unit
    var onRealign: (LevelData) -> Unit
    var levelSupplier: () -> Level?

    fun startServer()

    fun connectToServer()

    fun connectOrStart()

    fun startRealignmentRoutine(entityFactory: EntityFactory)

    fun disconnect()
}
