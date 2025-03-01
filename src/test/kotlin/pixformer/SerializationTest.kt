import pixformer.common.Vector2D
import pixformer.model.LevelData
import pixformer.model.LevelImpl
import pixformer.model.WorldImpl
import pixformer.model.WorldOptionsFactory
import pixformer.model.entity.EntityFactory
import pixformer.model.entity.EntityFactoryImpl
import pixformer.model.entity.MutableEntity
import pixformer.model.entity.dynamic.enemy.goomba.Goomba
import pixformer.model.entity.dynamic.player.Player
import pixformer.model.entity.statics.surprise.Surprise
import pixformer.model.score.Score
import pixformer.serialization.LevelSerialization
import pixformer.view.entity.NullGraphicsComponentFactory
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 *
 */
class SerializationTest {
    private val world = WorldImpl(WorldOptionsFactory.testOptions())
    private val entityFactory: EntityFactory = EntityFactoryImpl(NullGraphicsComponentFactory(), world)

    @Test
    fun commonSerialization() {
        val entities =
            setOf(
                entityFactory.createGoomba(2, 1).also {
                    (it as MutableEntity).velocity = Vector2D(3.2, 1.0)
                },
            )

        val level = LevelImpl(LevelData("test", entityFactory, entities, 3, 0, emptyMap()))
        level.init()

        val json = LevelSerialization.serialize(level)
        assertEquals(
            """
            {"name":"test","spawnPointX":3,"spawnPointY":0,"scores":{},"entities":[{"type":"goomba","x":2.0,"y":1.0,"velocity":{"x":3.2,"y":1.0},"width":1.0,"height":1.0}]}
            """.trimIndent(),
            json,
        )

        val deserialized = LevelSerialization.deserialize(json, entityFactory)!!
        assertEquals(level.data.name, deserialized.name)
        assertEquals(level.data.spawnPointX, deserialized.spawnPointX)
        assertEquals(level.data.spawnPointY, deserialized.spawnPointY)
        assertEquals(level.data.entities.size, deserialized.entities?.size)

        val first = deserialized.entities.first()
        assertIs<Goomba>(first)
        assertEquals(2.0, first.x)
        assertEquals(3.2, first.velocity.x)
        assertEquals(1.0, first.velocity.y)
    }

    @Test
    fun propertySpecificSerialization() {
        val entities =
            setOf(
                entityFactory.createSurpriseBlock(0, 1).also {
                    (it as Surprise).setHasCollided(true)
                },
            )

        val level = LevelImpl(LevelData("test", entityFactory, entities, 3, 0, emptyMap()))
        level.init()

        val json = LevelSerialization.serialize(level)
        assertEquals(
            """
            {"name":"test","spawnPointX":3,"spawnPointY":0,"scores":{},"entities":[{"type":"surprise","x":0.0,"y":1.0,"velocity":{"x":0.0,"y":0.0},"width":1.0,"height":1.0,"hasCollided":true}]}
            """.trimIndent(),
            json,
        )

        val deserialized = LevelSerialization.deserialize(json, entityFactory)!!
        assertEquals(level.data.name, deserialized.name)
        assertEquals(level.data.spawnPointX, deserialized.spawnPointX)
        assertEquals(level.data.spawnPointY, deserialized.spawnPointY)
        assertEquals(level.data.entities.size, deserialized.entities?.size)

        val first = deserialized.entities.first()
        assertIs<Surprise>(first)
        assertEquals(0.0, first.x)
        assertTrue(first.hasCollided())
    }

    @Test
    fun playerSerialization() {
        val scores = mapOf(1 to Score(2, 10), 3 to Score(5, 20))

        val level = LevelImpl(LevelData("test", entityFactory, emptySet(), 5, 2, scores))
        level.init()
        level.createPlayer(3, true) { it }

        val json = LevelSerialization.serialize(level)
        assertEquals(
            """
            {"name":"test","spawnPointX":5,"spawnPointY":2,"scores":{"1":{"points":2,"coinsNumber":10},"3":{"points":5,"coinsNumber":20}},"entities":[{"type":"player","x":5.0,"y":2.0,"velocity":{"x":0.0,"y":0.0},"width":0.94,"height":1.0,"playerIndex":3,"powerup":null}]}
            """.trimIndent(),
            json,
        )

        val deserialized = LevelSerialization.deserialize(json, entityFactory)!!
        assertEquals(level.data.name, deserialized.name)
        assertEquals(level.data.spawnPointX, deserialized.spawnPointX)
        assertEquals(level.data.spawnPointY, deserialized.spawnPointY)
        assertEquals(1, deserialized.entities.size)

        val first = deserialized.entities.first()
        assertIs<Player>(first)
        assertEquals(5.0, first.x)
        assertEquals(3, first.index)
        assertEquals(null, first.powerup.behaviour.getOrNull())

        assertEquals(2, deserialized.scores.size)
        assertEquals(2, deserialized.scores[1]?.points)
        assertEquals(10, deserialized.scores[1]?.coinsNumber)
        assertEquals(5, deserialized.scores[3]?.points)
        assertEquals(20, deserialized.scores[3]?.coinsNumber)
    }
}
