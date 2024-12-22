package pixformer.controller.server

import pixformer.model.modelinput.CompleteModelInput
import pixformer.server.MessageToServer
import pixformer.server.PlayerJumpMessage

/**
 * A [CompleteModelInput] decorator that sends server events when the player performs actions.
 */
class ServerEventCompleteModelInputDecorator(
    private val modelInput: CompleteModelInput,
    private val serverManager: ServerManager,
) : CompleteModelInput by modelInput {
    override fun jump() {
        modelInput.jump()
        MessageToServer(PlayerJumpMessage).send(serverManager)
    }
}
