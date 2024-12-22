package pixformer.controller.server

import pixformer.model.Level
import pixformer.model.modelinput.CompleteModelInput
import pixformer.serialization.SerializableLevelData
import java.util.UUID

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    val players: Map<UUID, CompleteModelInput>

    var onPlayerConnect: (UUID) -> Unit
    var onRealign: (SerializableLevelData) -> Unit
    var levelSupplier: () -> Level?

    fun startServer(port: Int)

    fun connectToServer(port: Int)

    fun connectOrStart(port: Int)
}
