package pixformer.controller.realign

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
        old: Level,
    ) {
        old.world.replaceEntities(new.entities) {
            it !is Block && (it !is Player || it.index != manager.playablePlayerIndex)
        }

        // todo remove all and spawn
    }
}
