package pixformer.model;

import pixformer.common.Updatable;
import pixformer.model.entity.Entity;
import pixformer.model.entity.collision.EntityCollisionManager;
import pixformer.model.score.ScoreManager;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The environment where entities live, interact and die.
 */
public interface World extends Updatable {

    /**
     * @return the entities living within this world
     */
    Set<Entity> getEntities();

    /**
     * @return entities within the 'update range' from any player
     */
    Stream<Entity> getUpdatableEntities();

    /**
     * @return this world's options
     */
    WorldOptions getOptions();

    /**
     * @return the score manager of the game world
     */
    ScoreManager getScoreManager();

    /**
     * @return an immutable subset of {@link World#getEntities()} with only user-controlled entities.
     */
    Set<Entity> getUserControlledEntities();

    /**
     * Adds an entity to the game world.
     * 
     * @param entity entity to add
     */
    void spawnEntity(Entity entity);

    /**
     * Adds an entity to spawn in the game world.
     * The action is queued to prevent concurrency errors.
     *
     * @param entity entity to spawn
     */
    void queueEntitySpawn(Entity entity);

    /**
     * Removes an entity from the game world and
     * registers the performed kill.
     * The action is queued to prevent concurrency errors.
     *
     * @param killed killed entity
     * @param killer entity who performed the kill
     */
    void queueEntityKill(Entity killed, Entity killer);

    /**
     * Removes an entity from the game world.
     * The action is queued to prevent concurrency errors.
     *
     * @param entity entity to remove
     */
    void queueEntityDrop(Entity entity);

    /**
     * @return the handler of entity collisions for this world
     */
    EntityCollisionManager getCollisionManager();

    /**
     * @return the indexed leaderboard
     */
    List<Integer> getIndexLeaderboard();

    /**
     * End the game because someone have won.
     *
     * @param winner player who won the game
     */
    void endGame(Entity winner);

    /**
     * Replaces entities in the world.
     *
     * @param entities entities to add
     * @param filterToAdd filter to add entities, executed on the `entities` parameter
     * @param filterToRemove filter to remove entities, executed on `getEntities()`
     */
    void replaceEntities(Set<Entity> entities, Predicate<Entity> filterToAdd, Predicate<Entity> filterToRemove);
}
