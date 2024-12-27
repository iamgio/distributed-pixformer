package pixformer.controller.server

import pixformer.model.modelinput.CompleteModelInput
import pixformer.server.MessageToServer
import pixformer.server.PlayerAbilityMessage
import pixformer.server.PlayerJumpMessage
import pixformer.server.PlayerMoveLeftMessage
import pixformer.server.PlayerMoveRightMessage
import pixformer.server.PlayerSprintMessage

/**
 * A [CompleteModelInput] decorator that sends server events when the player performs actions.
 */
class ServerEventCompleteModelInputDecorator(
    private val modelInput: CompleteModelInput,
    private val serverManager: ServerManager,
) : CompleteModelInput {
    override fun right() {
        modelInput.right()
        MessageToServer(PlayerMoveRightMessage).send(serverManager)
    }

    override fun left() {
        modelInput.left()
        MessageToServer(PlayerMoveLeftMessage).send(serverManager)
    }

// todo: - add other actions - align player score
    override fun jump() {
        modelInput.jump()
        MessageToServer(PlayerJumpMessage).send(serverManager)
    }

    override fun sprint() {
        modelInput.sprint()
        MessageToServer(PlayerSprintMessage).send(serverManager)
    }

    override fun ability() {
        modelInput.ability()
        MessageToServer(PlayerAbilityMessage).send(serverManager)
    }
}
