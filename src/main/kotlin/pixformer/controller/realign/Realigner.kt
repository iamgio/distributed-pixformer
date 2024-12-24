package pixformer.controller.realign

import pixformer.controller.server.ServerEventCompleteModelInputDecorator
import pixformer.controller.server.ServerManager
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.statics.Block

/**
 *
 */
class Realigner(
    private val manager: ServerManager,
) {
    fun realign(
        new: LevelData,
        current: Level,
    ) {
        current.world.replaceEntities(
            new.entities,
            { it !is Block && (it !is Player || it.index != manager.playablePlayerIndex) }, // Filter to add
            { it !is Block }, // Filter to remove
        )

        // todo fix duplicates
        val newPlayer = current.createPlayer(manager.playablePlayerIndex!!, true) { ServerEventCompleteModelInputDecorator(it, manager) }
    }
}
