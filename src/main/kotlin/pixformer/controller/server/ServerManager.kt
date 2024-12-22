package pixformer.controller.server

import pixformer.model.modelinput.CompleteModelInput
import java.util.*

/**
 * A bridge between controller and server to enable client-server communication.
 */
interface ServerManager {
    val players: Map<UUID, CompleteModelInput>

    var onPlayerConnect: (UUID) -> Unit

    fun startServer(port: Int)

    fun connectToServer(port: Int)

    fun connectOrStart(port: Int)
}
