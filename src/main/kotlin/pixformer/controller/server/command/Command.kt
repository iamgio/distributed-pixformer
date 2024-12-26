package pixformer.controller.server.command

import pixformer.model.modelinput.CompleteModelInput

/**
 *
 */
abstract class Command(
    val playerIndex: Int,
) {
    abstract fun execute(model: CompleteModelInput)
}

class MoveRightCommand(
    playerIndex: Int,
) : Command(playerIndex) {
    override fun execute(model: CompleteModelInput) {
        model.right()
    }
}

class MoveLeftCommand(
    playerIndex: Int,
) : Command(playerIndex) {
    override fun execute(model: CompleteModelInput) {
        model.left()
    }
}

class JumpCommand(
    playerIndex: Int,
) : Command(playerIndex) {
    override fun execute(model: CompleteModelInput) {
        model.jump()
    }
}
