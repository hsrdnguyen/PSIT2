package ch.avocado.share.servlet.resources.base;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * The view renderer is able to produce a form of output in the response.
 */
public interface ViewRenderer {
    /**
     * Render the view according to the config.
     * @param config The configuration of the view.
     * @throws ServletException If something goes wrong.
     * @throws IOException if the response cannot be written or similar.
     */
    void renderView(ViewConfig config) throws ServletException, IOException;
}
