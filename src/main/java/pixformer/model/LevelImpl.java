package pixformer.model;

import pixformer.controller.input.ModelInputAdapter;
import pixformer.model.entity.AbstractEntity;
import pixformer.model.entity.Entity;
import pixformer.model.entity.EntityFactory;
import pixformer.model.entity.statics.Block;
import pixformer.model.modelinput.CompleteModelInput;
import pixformer.serialization.SerializableEntityData;
import pixformer.serialization.SerializableLevelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of a game {@link Level}.
 */
public class LevelImpl implements Level {

    private static final int PLAYER_1_INDEX = 0;
    private static final int PLAYER_2_INDEX = 1;
    private static final int PLAYER_3_INDEX = 2;
    private static final int PLAYER_4_INDEX = 3;

    private final LevelData data;
    private final World world;

    private final List<CompleteModelInput> players;

    /**
     * Constructor for the level.
     *
     * @param data level data
     * @param world world
     */
    public LevelImpl(final LevelData data, final World world) {
        this.data = data;
        this.world = world;
        this.players = new ArrayList<>();
    }

    /**
     * Constructor for the level.
     *
     * @param data level data
     */
    public LevelImpl(final LevelData data) {
        this(data, new WorldImpl(WorldOptionsFactory.defaultOptions()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LevelData getData() {
        return this.data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getWorld() {
        return this.world;
    }

    /**
     * @param index player index (starting from 0)
     * @return the corresponding player if it exists
     */
    private Optional<CompleteModelInput> getPlayer(final int index) {
        return index < this.players.size() ? Optional.of(this.players.get(index)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CompleteModelInput> getPlayer1() {
        return this.getPlayer(PLAYER_1_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CompleteModelInput> getPlayer2() {
        return this.getPlayer(PLAYER_2_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CompleteModelInput> getPlayer3() {
        return this.getPlayer(PLAYER_3_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CompleteModelInput> getPlayer4() {
        return this.getPlayer(PLAYER_4_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        this.data.entities().forEach(this.world::spawnEntity);

        createPlayer();
    }

    @Override
    public CompleteModelInput createPlayer() {
        final Entity player = this.createPlayer(players.size(), data.spawnPointX(), data.spawnPointY(), data.entityFactory());
        this.world.spawnEntity(player);

        return player.getInputComponent().map(inputComponent -> {
            final CompleteModelInput input = ModelInputAdapter.from(inputComponent);
            this.players.add(input);
            return input;
        }).orElseThrow(() -> new IllegalStateException("Player entity must have an input component."));
    }

    /**
     * @param playerIndex index of the player, starting from 0
     * @param x x coordinate start position
     * @param y y coordinate start position
     * @param entityFactory factory who create entities
     * @return a new player entity
     */
    private Entity createPlayer(final int playerIndex, final double x, final double y, final EntityFactory entityFactory) {
         return entityFactory.createMainCharacter(x, y, playerIndex);
    }

    @Override
    public void realign(final SerializableLevelData data) {
        final World world = this.getWorld();
        // Map entities by id.
        Map<Integer, Entity> entities = world.getEntities().stream()
                .filter(e -> e instanceof AbstractEntity)
                .filter(e -> !(e instanceof Block))
                .collect(Collectors.toMap(Entity::getId, Function.identity()));

        for (SerializableEntityData serializable : data.getEntities()) {
            final AbstractEntity entity = (AbstractEntity) entities.get(serializable.getId());

            entity.setX(serializable.getX());
            entity.setY(serializable.getY());
        }
    }
}
