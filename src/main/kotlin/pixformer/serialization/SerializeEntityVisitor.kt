package pixformer.serialization

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import pixformer.model.entity.Entity
import pixformer.model.entity.EntityVisitor
import pixformer.model.entity.dynamic.enemy.goomba.Goomba
import pixformer.model.entity.dynamic.enemy.koopa.Koopa
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.dynamic.powerup.AbstractPhysicalPowerup
import pixformer.model.entity.powerup.other.fireball.Fireball
import pixformer.model.entity.statics.Barrier
import pixformer.model.entity.statics.Block
import pixformer.model.entity.statics.brick.Brick
import pixformer.model.entity.statics.coin.Coin
import pixformer.model.entity.statics.pole.Pole
import pixformer.model.entity.statics.surprise.Surprise
import kotlin.jvm.optionals.getOrNull

/**
 * A visitor that serializes entities to JSON.
 */
class SerializeEntityVisitor : EntityVisitor<JsonObject> {
    private fun serialize(
        type: String,
        entity: Entity,
        block: JsonObjectBuilder.() -> Unit = {},
    ) = buildJsonObject {
        put("type", type)
        put("x", entity.x)
        put("y", entity.y)
        put(
            "velocity",
            buildJsonObject {
                put("x", entity.velocity.x)
                put("y", entity.velocity.y)
            },
        )
        // put("width", entity.width)
        // put("height", entity.height)
        block()
    }

    override fun visit(block: Block) = serialize("block", block)

    override fun visit(brick: Brick) = serialize("brick", brick)

    override fun visit(surprise: Surprise) =
        serialize("surprise", surprise) {
            put("hasCollided", surprise.hasCollided())
        }

    override fun visit(barrier: Barrier) = serialize("barrier", barrier)

    override fun visit(coin: Coin) = serialize("coin", coin)

    override fun visit(pole: Pole) = serialize("pole", pole)

    override fun visit(goomba: Goomba) = serialize("goomba", goomba)

    override fun visit(koopa: Koopa) =
        serialize("koopa", koopa) {
            put("state", if (koopa.isWalking) "walking" else "shell")
        }

    override fun visit(player: Player) =
        serialize("player", player) {
            put("playerIndex", player.index)
            put(
                "powerup",
                player.powerup.behaviour
                    .getOrNull()
                    ?.javaClass
                    ?.simpleName,
            )
        }

    override fun visit(fireball: Fireball) = serialize("fireball", fireball)

    override fun visit(powerup: AbstractPhysicalPowerup) = serialize("powerup", powerup)
}
