package ch.avocado.share.servlet.resources.base;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by coffeemakr on 14.04.16.
 */
abstract class ResourceRenderer implements ViewRenderer {
    @Override
    public void renderView(ViewConfig config) throws ServletException, IOException {
        if(config == null) throw new NullPointerException("config is null");
        DetailViewConfig detailViewConfig;
        switch (config.getView()) {
            case EDIT:
                detailViewConfig = (DetailViewConfig) config;
                renderEdit(detailViewConfig);
                break;
            case CREATE:
                detailViewConfig = (DetailViewConfig) config;
                renderCreate(detailViewConfig);
                break;
            case DETAIL:
                detailViewConfig = (DetailViewConfig) config;
                renderDetails(detailViewConfig);
                break;
            case LIST:
                ListViewConfig listViewConfig = (ListViewConfig) config;
                renderList(listViewConfig);
                break;

        }
    }
    abstract protected void renderEdit(DetailViewConfig config) throws ServletException, IOException;
    abstract protected void renderDetails(DetailViewConfig config) throws ServletException, IOException;
    abstract protected void renderList(ListViewConfig config) throws ServletException, IOException;
    abstract protected void renderCreate(DetailViewConfig config) throws ServletException, IOException;
}
