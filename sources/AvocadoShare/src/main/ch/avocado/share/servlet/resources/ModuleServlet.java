package ch.avocado.share.servlet.resources;

import ch.avocado.share.controller.ModuleBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.servlet.resources.base.HtmlRenderer;
import ch.avocado.share.servlet.resources.base.ResourceServlet;
import ch.avocado.share.servlet.resources.base.ViewRenderer;

import javax.servlet.annotation.WebServlet;

@WebServlet("/module")
public class ModuleServlet extends ResourceServlet<Module> {

    @Override
    protected Class<? extends ResourceBean<Module>> getBeanClass() {
        return ModuleBean.class;
    }

    @Override
    protected ViewRenderer getHtmlRenderer() {
        return new HtmlRenderer("module_templates/");
    }
}
