package pixformer.serialization

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import pixformer.controller.deserialization.level.JsonLevelDataDeserializer
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.EntityFactory
import pixformer.model.entity.statics.Block

/**
 * A utility class to serialize and deserialize level information on-the-fly to allow reconciliation between server and client.
 */
object LevelSerialization {
    /**
     * Serializes the level to a JSON string.
     * @param level level to serialize
     * @return JSON string
     */
    fun serialize(level: Level): String {
        val world = level.world
        val jsonObjs = world.entities.filter { it !is Block }.map { it.accept(SerializeEntityVisitor()) }

        val json =
            buildJsonObject {
                put("name", level.data.name)
                put("spawnPointX", level.data.spawnPointX)
                put("spawnPointY", level.data.spawnPointY)
                putJsonObject("scores") {
                    world.scoreManager.allScores
                        .asSequence()
                        .sortedBy { (index, _) -> index }
                        .forEach { (index, score) ->
                            putJsonObject(index.toString()) {
                                put("points", score.points)
                                put("coinsNumber", score.coinsNumber)
                            }
                        }
                }
                put("entities", JsonArray(jsonObjs))
            }

        return Json.encodeToString(json)
    }

    /**
     * Deserializes level information from a JSON string.
     * @param json JSON string
     * @param entityFactory entity factory to create entities from
     * @return the generated level data, or `null` if deserialization failed
     */
    fun deserialize(
        json: String,
        entityFactory: EntityFactory,
    ): LevelData? =
        try {
            JsonLevelDataDeserializer(entityFactory).deserialize(json.byteInputStream())
        } catch (e: SerializationException) {
            null
        }
}
