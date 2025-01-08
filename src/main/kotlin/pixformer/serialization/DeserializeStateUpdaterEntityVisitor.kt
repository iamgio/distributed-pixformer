package pixformer.serialization

import pixformer.common.Vector2D
import pixformer.model.entity.EntityVisitor
import pixformer.model.entity.MutableEntity
import pixformer.model.entity.dynamic.enemy.goomba.Goomba
import pixformer.model.entity.dynamic.enemy.koopa.Koopa
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.dynamic.powerup.AbstractPhysicalPowerup
import pixformer.model.entity.powerup.other.fireball.Fireball
import pixformer.model.entity.powerup.powerups.FireFlower
import pixformer.model.entity.powerup.powerups.Mushroom
import pixformer.model.entity.statics.Barrier
import pixformer.model.entity.statics.Block
import pixformer.model.entity.statics.brick.Brick
import pixformer.model.entity.statics.coin.Coin
import pixformer.model.entity.statics.pole.Pole
import pixformer.model.entity.statics.surprise.Surprise

/**
 * A visitor that reads JSON and updates entity properties.
 * This does not count as deserialization, as it does not create new entities,
 * but rather updates existing ones.
 */
class DeserializeStateUpdaterEntityVisitor(
    private val json: com.google.gson.JsonObject,
) : EntityVisitor<Unit> {
    private fun updateCommonProperties(entity: MutableEntity) {
        json["velocity"]?.asJsonObject?.let { velocity ->
            entity.velocity = Vector2D(velocity["x"]!!.asDouble, velocity["y"]!!.asDouble)
        }
    }

    override fun visit(block: Block) = updateCommonProperties(block)

    override fun visit(brick: Brick) = updateCommonProperties(brick)

    override fun visit(surprise: Surprise) =
        updateCommonProperties(surprise).also {
            json["hasCollided"]?.asBoolean?.let(surprise::setHasCollided)
        }

    override fun visit(barrier: Barrier) = updateCommonProperties(barrier)

    override fun visit(coin: Coin) = updateCommonProperties(coin)

    override fun visit(pole: Pole) = updateCommonProperties(pole)

    override fun visit(goomba: Goomba) = updateCommonProperties(goomba)

    override fun visit(koopa: Koopa) = updateCommonProperties(koopa)

    override fun visit(player: Player) =
        updateCommonProperties(player).also {
            json["powerup"]?.takeUnless { it.isJsonNull }?.asString?.let {
                val powerup =
                    when (it) {
                        "Mushroom" -> Mushroom()
                        "FireFlower" -> FireFlower()
                        else -> null
                    }
                player.setPowerup(powerup)
            }
        }

    override fun visit(fireball: Fireball) {}

    override fun visit(powerup: AbstractPhysicalPowerup) = updateCommonProperties(powerup)
}
