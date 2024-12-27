package pixformer.controller.gameloop;

import pixformer.controller.Controller;
import pixformer.model.Level;
import pixformer.model.World;
import pixformer.model.entity.DrawableEntity;
import pixformer.model.entity.Entity;
import pixformer.model.entity.dynamic.player.Player;
import pixformer.view.View;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Factory of available game loops.
 */
public final class GameLoopFactory {

    private boolean initialized = false;
    private final Level level;
    private final Controller controller;
    private final View view;

    /**
     * Instantiates a new game loop factory.
     *
     * @param level      game level
     * @param controller game controller
     * @param view       game view
     */
    public GameLoopFactory(final Level level, final Controller controller, final View view) {
        this.level = level;
        this.view = view;
        this.controller = controller;
    }

    /**
     * @return a new default game loop
     */
    public GameLoop defaultLoop() {
        view.init();

        final World world = level.getWorld();

        return dt -> {
            final Set<Entity> playersEntities = world.getEntities().stream()
                    .filter(e -> e instanceof Player)
                    .collect(Collectors.toSet());

            if (!initialized && !playersEntities.isEmpty()) {
                initialized = true;
            }

            view.update(dt);
            if (this.controller.getGameLoopManager().isRunning()) {
                world.update(dt);
            }

            // Game over check
            if (initialized && playersEntities.stream().map(Entity::getWorld).allMatch(Optional::isEmpty)) {
                this.controller.getLevelManager().endCurrentLevel();
            }

            final double cameraPivotX = this.controller.calcEntitiesCommonPointX(playersEntities);
            final double cameraPivotY = this.controller.calcEntitiesCommonPointY(playersEntities);
            view.updateCamera(cameraPivotX, cameraPivotY);

            world.getUpdatableEntities()
                    .filter(DrawableEntity.class::isInstance)
                    .map(DrawableEntity.class::cast)
                    .forEach(entity -> {
                        view.getScene().getGraphics().setTranslate(entity.getX(), entity.getY());
                        entity.getGraphicsComponent().update(view.getScene());
                    });
        };
    }
}
