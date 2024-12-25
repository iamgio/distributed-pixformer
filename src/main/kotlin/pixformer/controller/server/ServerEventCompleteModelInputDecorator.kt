package pixformer.controller.server

import pixformer.model.modelinput.CompleteModelInput
import pixformer.server.MessageToServer
import pixformer.server.PlayerJumpMessage
import pixformer.server.PlayerMoveLeftMessage
import pixformer.server.PlayerMoveRightMessage

/**
 * A [CompleteModelInput] decorator that sends server events when the player performs actions.
 */
class ServerEventCompleteModelInputDecorator(
    private val modelInput: CompleteModelInput,
    private val serverManager: ServerManager,
) : CompleteModelInput by modelInput {
    override fun right() {
        modelInput.right()
        MessageToServer(PlayerMoveRightMessage).send(serverManager)
    }

    override fun left() {
        modelInput.left()
        MessageToServer(PlayerMoveLeftMessage).send(serverManager)
    }

// todo: - add other actions - broadcast events from leader too - fix realignment id pairing
    override fun jump() {
        modelInput.jump()
        MessageToServer(PlayerJumpMessage).send(serverManager)
    }
}
