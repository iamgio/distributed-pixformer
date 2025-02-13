package pixformer.model;

import pixformer.model.entity.Entity;
import pixformer.model.entity.collision.EntityCollisionManager;
import pixformer.model.entity.collision.EntityCollisionManagerImpl;
import pixformer.model.entity.dynamic.player.Player;
import pixformer.model.event.EventManager;
import pixformer.model.input.UserInputComponent;
import pixformer.model.score.Score;
import pixformer.model.score.ScoreManager;
import pixformer.model.score.ScoreManagerImpl;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The standard implementation of the game world.
 */
public class WorldImpl implements World {

    private final WorldOptions options;
    private final Set<Entity> entities;
    private final EntityCollisionManager collisionManager;
    private final EventManager eventManager;
    private final ScoreManager scoreManager;
    private final Queue<Runnable> commandQueue;

    private Set<Entity> lazyUserControlledEntity;

    private int lastId = 0;

    /**
     * Create a new World.
     *
     * @param options world options that affect this world's behavior
     */
    public WorldImpl(final WorldOptions options) {
        this.options = options;
        this.entities = new HashSet<>();
        this.commandQueue = new LinkedList<>();
        this.collisionManager = new EntityCollisionManagerImpl(this);
        this.eventManager = new EventManager();
        this.scoreManager = new ScoreManagerImpl(this.eventManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entity> getEntities() {
        return Collections.unmodifiableSet(this.entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreManager getScoreManager() {
        return this.scoreManager;
    }

    /**
     * {@inheritDoc}
     * @implNote lazily evaluated
     */
    @Override
    public Set<Entity> getUserControlledEntities() {
        if (this.lazyUserControlledEntity == null) {
            this.lazyUserControlledEntity = this.entities.stream()
                    .filter(entity -> entity.getInputComponent().isPresent())
                    .filter(entity -> entity.getInputComponent().get() instanceof UserInputComponent)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return Collections.unmodifiableSet(this.lazyUserControlledEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Entity> getUpdatableEntities() {
        if (this.options.updateRange() == WorldOptions.INFINITE_UPDATE_RANGE) {
            return this.getEntities().stream();
        }

        return this.getEntities().stream()
                .filter(entity ->
                        entity instanceof Player ||
                        getUserControlledEntities().stream()
                                .anyMatch(player -> entity.getDistanceFrom(player) < this.options.updateRange())
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldOptions getOptions() {
        return this.options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void spawnEntity(final Entity entity) {
        entity.setId(this.lastId++);

        this.entities.add(entity);
        entity.onSpawn(this);

        // Clear the lazy cache if a player joins.
        if (entity instanceof Player) {
            this.lazyUserControlledEntity = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueEntitySpawn(final Entity entity) {
        this.commandQueue.add(() -> spawnEntity(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueEntityKill(final Entity killed, final Entity killer) {
        this.queueEntityDrop(killed);
        eventManager.killed(killed, killer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueEntityDrop(final Entity entity) {
        this.commandQueue.add(() -> {
            this.entities.remove(entity);
            entity.onDespawn(this);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityCollisionManager getCollisionManager() {
        return this.collisionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIndexLeaderboard() {
        return this.scoreManager.getAllScores().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Score::points).reversed()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endGame(final Entity player) {
        this.scoreManager.passedFinishLine(player);
        this.queueEntityDrop(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final double dt) {
        this.getUpdatableEntities().forEach(entity -> {
            entity.getPhysicsComponent().ifPresent(physicsComponent -> {
                physicsComponent.update(dt);
            });
            entity.getCollisionComponent().ifPresent(collisionComponent -> {
                collisionComponent.update(dt, this.collisionManager.findCollisionsFor(entity));
            });
            entity.getInputComponent().ifPresent(ai -> ai.update(this));

            entity.update(dt);
        });

        final var command = this.commandQueue.poll();
        if (command != null) {
            command.run();
        }
    }

    @Override
    public void replaceEntities(final Set<Entity> entities, final Predicate<Entity> filterToAdd, final Predicate<Entity> filterToRemove) {
        this.commandQueue.add(() -> {
            // To remove.
            this.entities.stream()
                    .filter(filterToRemove)
                    .collect(Collectors.toSet())
                    .forEach(this.entities::remove);

            // To add.
            entities.stream()
                    .filter(filterToAdd)
                    .forEach(this::spawnEntity);

            this.lazyUserControlledEntity = null;
        });
    }
}
