package pixformer.serialization

import kotlinx.serialization.Serializable

/**
 * A serializable representation of a level.
 */
@Serializable
data class SerializableLevelData(
    val entities: List<SerializableEntityData>,
    val players: List<SerializablePlayerData>,
)
