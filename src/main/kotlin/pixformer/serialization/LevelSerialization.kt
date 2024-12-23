package pixformer.serialization

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import pixformer.controller.deserialization.level.JsonLevelDataDeserializer
import pixformer.controller.realign.Realigner
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.entity.EntityFactory

/**
 * A utility class to serialize and deserialize level information on-the-fly to allow reconciliation between server and client.
 */
object LevelSerialization {
    fun serialize(level: Level): String {
        val world = level.world

        val entities =
            Realigner
                .alignableEntities(world)
                .map { SerializableEntityData.from(it) }
                .toList()

        val players =
            Realigner
                .alignablePlayers(world)
                .map { SerializablePlayerData(it.index, SerializableEntityData.from(it)) }
                .toList()

        val levelData = SerializableLevelData(entities, players)

        return Json.encodeToString(levelData)
    }

    fun serializeNEW(level: Level): String {
        val world = level.world
        val jsonObjs = world.entities.map { it.accept(SerializeEntityVisitor()) }

        val json =
            buildJsonObject {
                put("name", level.data.name)
                put("spawnPointX", level.data.spawnPointX)
                put("spawnPointY", level.data.spawnPointY)
                put("entities", JsonArray(jsonObjs))
            }

        return Json.encodeToString(json)
    }

    fun deserializeNEW(
        json: String,
        entityFactory: EntityFactory,
    ): LevelData? =
        try {
            JsonLevelDataDeserializer(entityFactory).deserialize(json.byteInputStream())
        } catch (e: SerializationException) {
            null
        }

    fun deserialize(json: String): SerializableLevelData? =
        try {
            Json.decodeFromString(json)
        } catch (e: SerializationException) {
            null
        }
}
