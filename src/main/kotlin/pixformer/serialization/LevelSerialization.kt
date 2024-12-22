package pixformer.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pixformer.model.Level
import pixformer.model.entity.statics.Block

/**
 * A utility class to serialize and deserialize level information on-the-fly to allow reconciliation between server and client.
 */
object LevelSerialization {
    fun serialize(level: Level): String {
        val world = level.world

        val entities =
            world.entities
                .asSequence()
                .filter { it !is Block }
                .map { SerializableEntityData.from(it) }
                .toList()

        return Json.encodeToString(entities)
    }

    fun deserialize(json: String): SerializableLevelData = Json.decodeFromString(json)
}
