package pixformer.model.entity.dynamic.enemy.ai;

import pixformer.common.Vector2D;
import pixformer.model.World;
import pixformer.model.entity.MutableEntity;
import pixformer.model.entity.collision.Collision;
import pixformer.model.entity.collision.CollisionReactor;
import pixformer.model.entity.dynamic.OnlyXVelocitySetter;
import pixformer.model.entity.dynamic.enemy.HorizontalModelInputImpl;
import pixformer.model.input.AIInputComponent;
import pixformer.model.modelinput.HorizontalModelInput;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An AI which makes the controlled entity goes left and right.
 */
public class GeneralAIInputComponent extends AIInputComponent {
    private final CollisionReactor collisionReactor;
    private final double velocityModule;

    /**
     * @param entity to be controlled.
     * @param velocityModule module of the velocity to be given to the {@code entity}
     * @param initialBehaviour of the {@code entity}.
     * @param whichCollisions a predicate which says at which collisions the behaviour should change.
     */
    public GeneralAIInputComponent(
            final MutableEntity entity,
            final double velocityModule,
            final Consumer<HorizontalModelInput> initialBehaviour,
            final Predicate<Collision> whichCollisions) {
        super(entity);
        this.velocityModule = velocityModule;
        final var joystick = new HorizontalModelInputImpl(new OnlyXVelocitySetter(entity), velocityModule);
        collisionReactor = new GeneralAICollisionReactor(joystick, initialBehaviour, whichCollisions);
    }

    @Override
    public final void update(final World world) {
        final var collisions = world.getCollisionManager().findCollisionsFor(getEntity());
        if (getEntity().getVelocity().x() == 0) {
            getEntity().setVelocity(new Vector2D(-velocityModule, 0));
        }
        collisionReactor.react(collisions);
    }
}
