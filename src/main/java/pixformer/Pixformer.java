package pixformer;

import pixformer.controller.gamefinder.HttpGameFinderAgent;
import pixformer.view.engine.ViewLauncher;
import pixformer.view.javafx.PixformerJavaFXViewLauncher;

/**
 * The main class.
 */
public final class Pixformer {

    private Pixformer() {

    }

    /**
     * Main method to launch the application.
     * @param args program arguments
     */
    public static void main(final String[] args) {
        // The optional argument is the IP address of the game finder server,
        // which defaults to "localhost".
        if (args.length > 0) {
            HttpGameFinderAgent.Companion.setIp(args[0]);
        }

        final ViewLauncher launcher = new PixformerJavaFXViewLauncher();
        launcher.launch();
    }
}
