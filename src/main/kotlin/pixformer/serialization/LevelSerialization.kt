package pixformer.serialization

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pixformer.controller.server.Realigner
import pixformer.model.Level

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

    fun deserialize(json: String): SerializableLevelData? =
        try {
            Json.decodeFromString(json)
        } catch (e: SerializationException) {
            null
        }
}
