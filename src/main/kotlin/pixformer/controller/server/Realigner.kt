package pixformer.controller.server

import pixformer.model.Level
import pixformer.model.World
import pixformer.model.entity.MutableEntity
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.statics.Block
import pixformer.serialization.SerializableEntityData
import pixformer.serialization.SerializableLevelData

/**
 *
 */
class Realigner(
    private val manager: ServerManager,
) {
    companion object {
        fun alignableEntities(world: World) =
            world.entities
                .asSequence()
                .filterIsInstance<MutableEntity>()
                .filter { it !is Block }
                .filter { it !is Player }

        fun alignablePlayers(world: World) = world.entities.asSequence().filterIsInstance<Player>()
    }

    private fun realignEntity(
        serializable: SerializableEntityData,
        entity: MutableEntity,
    ) {
        entity.x = serializable.x
        entity.y = serializable.y
    }

    fun realign(
        data: SerializableLevelData,
        level: Level,
    ) {
        val world: World = level.world

        // Map entities by id.
        val entities: Map<Int, MutableEntity> = alignableEntities(world).map { it.id to it }.toMap()

        for (serializable in data.entities) {
            val entity = entities[serializable.id] ?: continue
            realignEntity(serializable, entity)
        }

        // Map players by index.
        val players: Map<Int, Player> = alignablePlayers(world).map { it.index to it }.toMap()

        for ((index, entityData) in data.players) {
            val player = players[index] ?: level.createPlayer(index, false)
            realignEntity(entityData, player)
        }
    }
}
