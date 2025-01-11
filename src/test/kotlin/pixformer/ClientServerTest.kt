package pixformer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pixformer.controller.server.ServerManager
import pixformer.controller.server.ServerManagerImpl
import pixformer.controller.server.command.JumpCommand
import pixformer.model.Level
import pixformer.model.LevelData
import pixformer.model.LevelImpl
import pixformer.model.WorldImpl
import pixformer.model.WorldOptionsFactory
import pixformer.model.entity.EntityFactory
import pixformer.model.entity.EntityFactoryImpl
import pixformer.server.MessageToServer
import pixformer.server.PlayerJumpMessage
import pixformer.view.entity.NullGraphicsComponentFactory
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

/**
 *
 */
class ClientServerTest {
    private val world = WorldImpl(WorldOptionsFactory.testOptions())
    private val entityFactory: EntityFactory = EntityFactoryImpl(NullGraphicsComponentFactory(), world)
    private val level: Level = LevelImpl(LevelData("test", entityFactory, emptySet(), 3, 0, emptyMap()))

    private val server: ServerManager = ServerManagerImpl()
    private val client1: ServerManager = ServerManagerImpl()
    private val client2: ServerManager = ServerManagerImpl()

    @BeforeTest
    fun setUp() {
        server.levelSupplier = { level }
        client1.levelSupplier = { level }
    }

    // C1 -> S <- C2
    // C1: jump
    // S: jump
    // S: broadcast to C1 and C2
    // C1: ignore
    // C2: jump
    @Test
    fun commandExecution() {
        runBlocking {
            var dispatchedOnServer = false
            server.addOnDispatch { command ->
                assertIs<JumpCommand>(command)
                dispatchedOnServer = true
            }

            var dispatchedOnOtherClient = false
            client2.addOnDispatch { command ->
                assertIs<JumpCommand>(command)
                dispatchedOnOtherClient = true
            }

            server.connectOrStart()
            delay(2000)
            launch(Dispatchers.IO) { client1.connectOrStart() }
            delay(2000)
            launch(Dispatchers.IO) { client2.connectOrStart() }
            delay(2000)
            MessageToServer(PlayerJumpMessage).send(client1)
            delay(2000)
            assert(dispatchedOnServer)
            assert(dispatchedOnOtherClient)
        }
    }
}
