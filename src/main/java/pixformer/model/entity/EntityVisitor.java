package pixformer.model.entity;

import pixformer.model.entity.dynamic.enemy.goomba.Goomba;
import pixformer.model.entity.dynamic.enemy.koopa.Koopa;
import pixformer.model.entity.dynamic.player.Player;
import pixformer.model.entity.dynamic.powerup.AbstractPhysicalPowerup;
import pixformer.model.entity.powerup.other.fireball.Fireball;
import pixformer.model.entity.statics.Barrier;
import pixformer.model.entity.statics.Block;
import pixformer.model.entity.statics.brick.Brick;
import pixformer.model.entity.statics.coin.Coin;
import pixformer.model.entity.statics.pole.Pole;
import pixformer.model.entity.statics.surprise.Surprise;

/**
 *
 */
public interface EntityVisitor<T> {
    T visit(Block block);
    T visit(Brick brick);
    T visit(Surprise surprise);
    T visit(Barrier barrier);
    T visit(Coin coin);
    T visit(Pole pole);
    T visit(Goomba goomba);
    T visit(Koopa koopa);
    T visit(Player player);
    T visit(Fireball fireball);
    T visit(AbstractPhysicalPowerup powerup);
}
