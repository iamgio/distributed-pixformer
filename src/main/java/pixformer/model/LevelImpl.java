package pixformer.model;

import pixformer.controller.input.ModelInputAdapter;
import pixformer.model.entity.Entity;
import pixformer.model.entity.EntityFactory;
import pixformer.model.entity.dynamic.player.Player;
import pixformer.model.modelinput.CompleteModelInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    // Keyboard-controlled players.
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
        if (this.data.scores() != null) {
            this.world.getScoreManager().setAllScores(this.data.scores());
        }
    }

    @Override
    public Player createPlayer(int index, boolean playable, Function<CompleteModelInput, CompleteModelInput> modelInputMapper) {
        final Entity player = this.createPlayer(index, data.spawnPointX(), data.spawnPointY(), data.entityFactory());
        this.world.spawnEntity(player);

        player.getInputComponent().ifPresent(inputComponent -> {
            final CompleteModelInput input = ModelInputAdapter.from(inputComponent);
            if (playable) {
                this.players.add(modelInputMapper.apply(input));
            }
        });

        return (Player) player;
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
}
