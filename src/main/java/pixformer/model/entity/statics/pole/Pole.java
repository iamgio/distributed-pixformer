package pixformer.model.entity.statics.pole;

import pixformer.model.entity.AbstractEntity;
import pixformer.model.entity.DrawableEntity;
import pixformer.model.entity.EntityVisitor;
import pixformer.model.entity.GraphicsComponent;
import pixformer.model.entity.GraphicsComponentRetriever;
import pixformer.model.entity.collision.CollisionComponent;
import pixformer.model.entity.collision.DefaultRectangleBoundingBoxEntity;

import java.util.Optional;

/**
 * In game flag entity, representing the end of the level.
 */
public final class Pole extends AbstractEntity implements DefaultRectangleBoundingBoxEntity, DrawableEntity {

    private static final int HEIGHT = 10;
    private static final int WIDTH = 1;

    private final GraphicsComponent graphicsComponent;
    private final CollisionComponent collisionComponent;

    /**
     * Simple constructor for the flag.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param graphicsComponent retriever for the graphics component of the entity
     */
    public Pole(final int x, final int y, final GraphicsComponentRetriever graphicsComponent) {
        super(x, y, WIDTH, HEIGHT);
        this.graphicsComponent = graphicsComponent.apply(this);
        this.collisionComponent = new PoleCollisionComponent(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphicsComponent getGraphicsComponent() {
        return this.graphicsComponent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CollisionComponent> getCollisionComponent() {
        return Optional.of(this.collisionComponent);
    }

    @Override
    public <T> T accept(EntityVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
