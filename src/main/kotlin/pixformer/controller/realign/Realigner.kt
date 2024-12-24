package pixformer.controller.realign

import pixformer.common.Vector2D
import pixformer.controller.server.ServerManager
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.World
import pixformer.model.entity.MutableEntity
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.statics.Block
import pixformer.serialization.SerializableEntityData

/**
 *
 */
class Realigner(
    private val manager: ServerManager,
) {
    companion object {
        fun alignableEntities(world: World) =
            world.entities
                .toSet()
                .asSequence()
                .filterIsInstance<MutableEntity>()
                .filter { it !is Block }
                .filter { it !is Player }

        fun alignablePlayers(world: World) =
            world.entities
                .toSet()
                .asSequence()
                .filterIsInstance<Player>()
    }

    private fun realignEntity(
        serializable: SerializableEntityData,
        entity: MutableEntity,
    ) {
        entity.x = serializable.x
        entity.y = serializable.y
        entity.velocity = Vector2D(serializable.velocityX, serializable.velocityY)
    }

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
