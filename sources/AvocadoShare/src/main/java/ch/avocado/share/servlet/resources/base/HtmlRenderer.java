package ch.avocado.share.servlet.resources.base;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Renderer for HTML content.
 */
public class HtmlRenderer extends ResourceRenderer {

    private final String templateFolder;

    /**
     * The template for the edit view.
     */
    private final static String TEMPLATE_EDIT = "edit.jsp";
    /**
     * The template for the details view.
     */
    private final static String TEMPLATE_DETAILS = "view.jsp";
    /**
     * The template for the list view.
     */
    private final static String TEMPLATE_LIST = "list.jsp";
    /**
     * The template for the create view.
     */
    private final static String TEMPLATE_CREATE = "create.jsp";

    /**
     * If a detail view (all except list view) is rendered the attribute with this name will be set containing a
     * {@link DetailViewConfig} object with all required information.
     */
    public final static String ATTRIBUTE_DETAIL_VIEW_CONFIG = "ch.avocado.share.servlet.resources.base.HtmlRenderer.DetailViewConfig";

    /**
     * If the list view is rendered an attribute with this name is set in the request.
     * The attribute contains an {@link ListViewConfig} object with all required information.
     */
    public final static String ATTRIBUTE_LIST_VIEW_CONFIG = "ch.avocado.share.servlet.resources.base.HtmlRenderer.ListViewConfig";

    /**
     * Create a new HTML renderer. Make sure in the template folder are the following files:
     * <pre>
     *     * {@value TEMPLATE_CREATE}
     *     * {@value TEMPLATE_DETAILS}
     *     * {@value TEMPLATE_EDIT}
     *     * {@value TEMPLATE_LIST}
     * </pre>
     * @param templateFolder The folder in which the templates can be found.
     */
    public HtmlRenderer(String templateFolder) {
        super();
        this.templateFolder = templateFolder;
    }

    private void setContentType(ViewConfig config) {
        config.getResponse().setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private RequestDispatcher getDispatcherForTemplate(ViewConfig config, String template) {
        return config.getRequest().getRequestDispatcher(templateFolder + template);
    }

    private void includeHeader(ViewConfig config) throws ServletException, IOException {
        config.getRequest().getRequestDispatcher("includes/header.jsp").include(config.getRequest(), config.getResponse());
    }

    private void includeFooter(ViewConfig config) throws ServletException, IOException {
        config.getRequest().getRequestDispatcher("includes/footer.jsp").include(config.getRequest(), config.getResponse());
    }

    private void renderDetailsWithTemplate(DetailViewConfig config, String template) throws ServletException, IOException {
        setContentType(config);
        RequestDispatcher dispatcher = getDispatcherForTemplate(config, template);
        includeHeader(config);
        config.getRequest().setAttribute(ATTRIBUTE_DETAIL_VIEW_CONFIG, config);
        dispatcher.include(config.getRequest(), config.getResponse());
        includeFooter(config);
    }

    @Override
    public void renderEdit(DetailViewConfig config) throws ServletException, IOException {
        renderDetailsWithTemplate(config, TEMPLATE_EDIT);
    }

    @Override
    public void renderDetails(DetailViewConfig config) throws ServletException, IOException {
        renderDetailsWithTemplate(config, TEMPLATE_DETAILS);
    }

    @Override
    public void renderCreate(DetailViewConfig config) throws ServletException, IOException {
        renderDetailsWithTemplate(config, TEMPLATE_CREATE);
    }

    @Override
    public void renderList(ListViewConfig config) throws ServletException, IOException {
        setContentType(config);
        includeHeader(config);
        RequestDispatcher dispatcher = getDispatcherForTemplate(config, TEMPLATE_LIST);
        config.getRequest().setAttribute(ATTRIBUTE_LIST_VIEW_CONFIG, config);
        dispatcher.include(config.getRequest(), config.getResponse());
        includeFooter(config);
    }
}
