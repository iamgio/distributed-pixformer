package pixformer.controller.server.command

import pixformer.model.modelinput.CompleteModelInput

/**
 * A player command exchanged between client and server.
 * @param playerIndex the index of the player that performs the command
 */
abstract class Command(
    val playerIndex: Int,
) {
    /**
     * Executes the command on the model input of the player of index [playerIndex].
     * @param model the model to execute the command on
     */
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

class SprintCommand(
    playerIndex: Int,
) : Command(playerIndex) {
    override fun execute(model: CompleteModelInput) {
        model.sprint()
    }
}

class AbilityCommand(
    playerIndex: Int,
) : Command(playerIndex) {
    override fun execute(model: CompleteModelInput) {
        model.ability()
    }
}
