package pixformer.model.entity.dynamic.enemy.koopa;

import pixformer.model.entity.EntityVisitor;
import pixformer.model.entity.dynamic.enemy.Enemy;

/**
 * Tagging interface which must be implemented by Koopas.
 */
public interface Koopa extends KoopaState, Enemy {

    @Override
    default <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
