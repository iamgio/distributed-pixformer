package pixformer.controller.server

import pixformer.model.Level
import pixformer.model.entity.dynamic.player.Player
import pixformer.serialization.SerializableLevelData

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    val players: Map<Int, Player>

    val port: Int

    var onPlayerConnect: (Int) -> Unit
    var onRealign: (SerializableLevelData) -> Unit
    var levelSupplier: () -> Level?

    fun startServer()

    fun connectToServer()

    fun connectOrStart()
}
