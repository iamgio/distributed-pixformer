package pixformer;

import org.junit.jupiter.api.BeforeEach;
import pixformer.model.World;
import pixformer.model.WorldImpl;
import pixformer.model.WorldOptionsFactory;
import pixformer.model.entity.Entity;
import pixformer.model.entity.EntityFactoryImpl;
import pixformer.view.entity.NullGraphicsComponentFactory;

/**
 * Test for the fall from the world of the entities.
 */
final class WorldFallTest {

    private static final double DT = 5000;

    private final World world = new WorldImpl(WorldOptionsFactory.testOptions());

    @BeforeEach
    void setup() {
        final EntityFactoryImpl entityFactory = new EntityFactoryImpl(new NullGraphicsComponentFactory(), world);
        final Entity entity = entityFactory.createGoomba(0, world.getOptions().yFallThreshold() - 1);
        world.spawnEntity(entity);
    }

    /*
    @Test
    void testOutOfWorld() {
        // The entity is affected by gravity.
        assertFalse(world.getEntities().isEmpty());
        world.update(DT); // Threshold reached: entity is killed
        assertTrue(world.getEntities().isEmpty());
        world.update(DT);
        assertTrue(world.getEntities().isEmpty());
    }
    */
}
