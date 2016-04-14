package ch.avocado.share.servlet.resources.base;

import javax.servlet.ServletException;
import java.io.IOException;

public interface ViewRenderer {
    void renderView(ViewConfig config) throws ServletException, IOException;
}
