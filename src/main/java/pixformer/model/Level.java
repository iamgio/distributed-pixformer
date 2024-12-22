package pixformer.model;

import pixformer.model.entity.dynamic.player.Player;
import pixformer.model.modelinput.CompleteModelInput;
import pixformer.serialization.SerializableLevelData;

import java.util.Optional;

/**
 * A playable level.
 */
public interface Level {

    /**
     * @return read-only level data
     */
    LevelData getData();

    /**
     * The level world is a mutable container where entities' lifecycles run as time passes and events happen.
     * @return the world of this level.
     */
    World getWorld();

    /**
     * @return player 1 if it exists
     */
    Optional<CompleteModelInput> getPlayer1();

    /**
     * @return player 2 if it exists
     */
    Optional<CompleteModelInput> getPlayer2();

    /**
     * @return player 3 if it exists
     */
    Optional<CompleteModelInput> getPlayer3();

    /**
     * @return player 4 if it exists
     */
    Optional<CompleteModelInput> getPlayer4();

    /**
     * Sets up the game world with zero players.
     */
    void init();

    Player createPlayer();

    void realign(SerializableLevelData data);
}
