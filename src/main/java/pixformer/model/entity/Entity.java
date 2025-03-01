package pixformer.model.entity;

import pixformer.common.Updatable;
import pixformer.common.Vector2D;
import pixformer.model.World;
import pixformer.model.entity.collision.BoundingBox;
import pixformer.model.entity.collision.CollisionComponent;
import pixformer.model.input.InputComponent;
import pixformer.model.physics.PhysicsComponent;

import java.util.Optional;

/**
 * In-Game entity.
 */
public interface Entity extends Updatable {
    /**
     * @return the world this entity lives in, if it exists
     */
    Optional<World> getWorld();

    /**
     * @return the unique identifier of the entity
     */
    int getId();

    /**
     * @param id the unique identifier of the entity
     */
    void setId(int id);

    /**
     * @return X coordinate
     */
    double getX();

    /**
     * @return Y coordinate
     */
    double getY();

    /**
     * @return the width of the entity
     */
    double getWidth();

    /**
     * @return the height of the entity
     */
    double getHeight();

    /**
     * @return the velocity vector
     */
    Vector2D getVelocity();

    /**
     * @return the bounding box of the entity
     */
    BoundingBox getBoundingBox();

    /**
     * @return whether the entity is solid, meaning it cannot be traversed by other entities
     */
    boolean isSolid();

    <T> T accept(EntityVisitor<T> visitor);

    /**
     * @param other other entity
     * @return distance between the two entities
     */
    default double getDistanceFrom(final Entity other) {
        if (!getWorld().equals(other.getWorld())) {
            return Double.POSITIVE_INFINITY;
        }

        final double dx = getX() - other.getX();
        final double dy = getY() - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @return the physics component of the entity
     */
    default Optional<PhysicsComponent> getPhysicsComponent() {
        return Optional.empty();
    }

    /**
     * @return the input component of the entity
     */
    default Optional<InputComponent> getInputComponent() {
        return Optional.empty();
    }

    /**
     * @return the collision component of the entity
     */
    default Optional<CollisionComponent> getCollisionComponent() {
        return Optional.empty();
    }

    /**
     * Called when this entity is added onto a game world.
     * 
     * @param world game world the entity spawned on
     */
    default void onSpawn(final World world) {

    }

    /**
     * Called when this entity is removed from a game world.
     *
     * @param world game world the entity despawned from
     */
    default void onDespawn(final World world) {

    }
}
