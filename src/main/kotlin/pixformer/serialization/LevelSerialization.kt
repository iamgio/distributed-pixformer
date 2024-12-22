package pixformer.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pixformer.model.Level
import pixformer.model.LevelImpl
import pixformer.model.entity.dynamic.player.Player
import kotlin.streams.asSequence

/**
 * A utility class to serialize and deserialize level information on-the-fly to allow reconciliation between server and client.
 */
object LevelSerialization {
    fun serialize(level: Level): String {
        val world = level.world

        val entities =
            LevelImpl
                .alignableEntities(world)
                .asSequence()
                .map { SerializableEntityData.from(it) }
                .toList()

        val players =
            LevelImpl
                .alignablePlayers(world)
                .asSequence()
                .map { SerializablePlayerData((it as Player).index, SerializableEntityData.from(it)) }
                .toList()

        val levelData = SerializableLevelData(entities, players)

        return Json.encodeToString(levelData)
    }

    fun deserialize(json: String): SerializableLevelData = Json.decodeFromString(json)
}
