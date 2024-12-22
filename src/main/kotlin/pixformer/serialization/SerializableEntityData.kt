package pixformer.serialization

import kotlinx.serialization.Serializable
import pixformer.model.entity.Entity

/**
 * A serializable representation of an entity.
 */
@Serializable
data class SerializableEntityData(
    val id: Int,
    val x: Double,
    val y: Double,
) {
    companion object {
        fun from(entity: Entity): SerializableEntityData =
            SerializableEntityData(
                entity.id,
                entity.x,
                entity.y,
            )
    }
}
