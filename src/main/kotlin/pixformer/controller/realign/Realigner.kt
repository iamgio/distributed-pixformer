package pixformer.controller.realign

import pixformer.controller.server.ServerManager
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.statics.Block
import kotlin.jvm.optionals.getOrNull

/**
 *
 */
class Realigner(
    private val manager: ServerManager,
) {
    // The controlled player entity
    private var player: Player? = null

    fun realign(
        new: LevelData,
        current: Level,
    ) {
        // Create the player entity if it does not exist.
        if (player == null) {
            player =
                current.createPlayer(manager.playablePlayerIndex!!, true, manager.modelInputMapper())
        }

        // Replace all entities in the world with the new entities,
        // except for the player entity which is not replaced but rather updated.
        current.world.replaceEntities(
            new.entities,
            {
                // Filter to add
                if (it is Block) return@replaceEntities false
                if (it is Player) {
                    manager.players[it.index] = it
                    if (it.index == manager.playablePlayerIndex) {
                        // Update the player entity with the player state received from the server.
                        if (player != null) {
                            realignPlayer(it)
                        }
                        return@replaceEntities false
                    }
                }
                true
            },
            { it !is Block && (it !is Player || it.index != manager.playablePlayerIndex) }, // Filter to remove
        )

        // Update the player scores.
        current.world.scoreManager.allScores = new.scores
    }

    private fun realignPlayer(
        new: Player,
        current: Player = player!!,
    ) {
        current.x = new.x
        current.y = new.y
        current.setPowerup(new.powerup.behaviour.getOrNull())
    }
}
