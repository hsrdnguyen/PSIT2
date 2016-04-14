package ch.avocado.share.servlet.resources.base;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by coffeemakr on 14.04.16.
 */
public class HtmlRenderer extends ResourceRenderer {

    private final String templateFolder;

    private final static String TEMPLATE_EDIT = "edit.jsp";
    private final static String TEMPLATE_DETAILS = "view.jsp";
    private final static String TEMPLATE_LIST = "list.jsp";
    private final static String TEMPLATE_CREATE = "create.jsp";

    public final static String ATTRIBUTE_DETAIL_VIEW_CONFIG = "ch.avocado.share.servlet.resources.base.HtmlRenderer.DetailViewConfig";
    public final static String ATTRIBUTE_LIST_VIEW_CONFIG = "ch.avocado.share.servlet.resources.base.HtmlRenderer.ListViewConfig";


    public HtmlRenderer(String templateFolder) {
        this.templateFolder = templateFolder;
    }

    private RequestDispatcher getDispatcherForTemplate(ViewConfig config, String template) {
        return config.getRequest().getRequestDispatcher(templateFolder + template);
    }

    private void renderDetailsWithTemplate(DetailViewConfig config, String template) throws ServletException, IOException {
        RequestDispatcher dispatcher = getDispatcherForTemplate(config, template);
        config.getRequest().setAttribute(ATTRIBUTE_DETAIL_VIEW_CONFIG, config);
        dispatcher.include(config.getRequest(), config.getResponse());
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
    public void renderList(ListViewConfig config) throws ServletException, IOException {
        RequestDispatcher dispatcher = getDispatcherForTemplate(config, TEMPLATE_LIST);
        config.getRequest().setAttribute(ATTRIBUTE_LIST_VIEW_CONFIG, ATTRIBUTE_LIST_VIEW_CONFIG);
        dispatcher.include(config.getRequest(), config.getResponse());
    }

    @Override
    public void renderCreate(DetailViewConfig config) throws ServletException, IOException {
        renderDetailsWithTemplate(config, TEMPLATE_CREATE);
    }
}
