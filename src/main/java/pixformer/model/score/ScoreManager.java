package pixformer.model.score;

import pixformer.model.entity.Entity;
import pixformer.model.entity.dynamic.player.Player;

import java.util.Map;

/**
 * Class to manage multiple {@link Score} for multiple player.
 */
public interface ScoreManager {
    /**
     * @param player player to get the score
     * @return the score of the player
     */
    default Score getScore(Player player) {
        return getScoreByIndex(player.getIndex());
    }

    /**
     * @param playerIndex index of the player
     * @return the score of the player with a certain index
     */
    Score getScoreByIndex(int playerIndex);

    /**
     * @return the scores of each player, with the player index as key
     */
    Map<Integer, Score> getAllScores();

    /**
     * Sets all the scores of the players.
     * @param scores map of the scores of the players with the player index as key
     */
    void setAllScores(Map<Integer, Score> scores);

    /**
     * @return the remaining coins in the game
     */
    int getTotalCoins();

    /**
     * Method to manage when a player hit the finish line.
     * @param player player who passed the end flag
     */
    void passedFinishLine(Entity player);

}
