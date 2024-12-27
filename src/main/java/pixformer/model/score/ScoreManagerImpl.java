package pixformer.model.score;

import pixformer.model.entity.Entity;
import pixformer.model.entity.dynamic.player.Player;
import pixformer.model.entity.statics.coin.Coin;
import pixformer.model.event.EventSubscriber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@inheritDoc}.
 */
public class ScoreManagerImpl implements ScoreManager {
    private static final int DEFAULT_SCORE_INCREMENT = 100;
    private static final int POLE_POINTS_INCREMENT = 1_000;
    private final Map<Integer, Score> scoreMap;
    private final Set<Entity> winners;

    /**
     * Simple constructor for the score manager.
     *
     * @param eventSubscriber subscriber for the score manager
     */
    public ScoreManagerImpl(final EventSubscriber eventSubscriber) {
        this.scoreMap = new HashMap<>();
        this.winners = new HashSet<>();
        eventSubscriber.addPlayerOnKill(this::increaseScore);
    }

    /**
     * Method to update the score of a specific player.
     * @param player player to update the score
     * @param entity entity killed
     */
    private void increaseScore(final Entity player, final Entity entity) {
        if (player instanceof Player p) {
            final int index = p.getIndex();
            // Choosing the quantity of points to assign at the player, depending on what the player hit
            // a generic entity or the pole, in the second case we pass the player itself as the killed
            final int points = !entity.equals(player) ? DEFAULT_SCORE_INCREMENT : POLE_POINTS_INCREMENT / winners.size();
            if (scoreMap.containsKey(index)) {
                scoreMap.put(index, scoreMap.get(index).copyAddPoints(points));
            } else {
                scoreMap.put(index, new Score(points, 0));
            }
            if (entity instanceof Coin) {
                scoreMap.put(index, scoreMap.get(index).copyAddCoins(1));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Score getScoreByIndex(final int playerIndex) {
        return scoreMap.getOrDefault(playerIndex, new Score(0, 0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Score> getAllScores() {
        return Map.copyOf(scoreMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllScores(Map<Integer, Score> scores) {
        this.scoreMap.clear();
        this.scoreMap.putAll(scores);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalCoins() {
        return this.scoreMap.values().stream()
                .map(Score::coinsNumber)
                .filter(i -> i > 0)
                .reduce(Integer::sum).orElse(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passedFinishLine(final Entity player) {
        if (player instanceof Player && !winners.contains(player)) {
            this.winners.add(player);
            this.increaseScore(player, player);
        }
    }
}
