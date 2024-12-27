package pixformer.model;

import pixformer.model.entity.Entity;
import pixformer.model.entity.EntityFactory;
import pixformer.model.score.Score;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Read-only data of a {@link Level}.
 *
 * @param name        name of the level
 * @param entities    initial entities living in the level's world
 * @param entityFactory a factory that generate entities
 * @param spawnPointX X coordinate of the level's spawn point
 * @param spawnPointY Y coordinate of the level's spawn point
 */
public record LevelData(String name, EntityFactory entityFactory, Set<Entity> entities, int spawnPointX, int spawnPointY, Map<Integer, Score> scores) {

    /**
     * @param name        name of the level
     * @param entities    initial entities living in the level's world
     * @param spawnPointX X coordinate of the level's spawn point
     * @param spawnPointY Y coordinate of the level's spawn point
     */
    public LevelData(final String name, final EntityFactory entityFactory, 
                            final Set<Entity> entities, final int spawnPointX, final int spawnPointY,
                            final Map<Integer, Score> scores) {
        this.name = name;
        this.entityFactory = entityFactory;
        this.entities = Collections.unmodifiableSet(entities);
        this.spawnPointX = spawnPointX;
        this.spawnPointY = spawnPointY;
        this.scores = scores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entity> entities() {
        return Collections.unmodifiableSet(entities);
    }
}
