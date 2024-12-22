package pixformer.serialization

import kotlinx.serialization.Serializable

/**
 *
 */
@Serializable
data class SerializablePlayerData(
    val index: Int,
    val entityData: SerializableEntityData,
)
