package ch.avocado.share.servlet.resources;

import ch.avocado.share.controller.ModuleBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.servlet.resources.base.HtmlRenderer;
import ch.avocado.share.servlet.resources.base.ResourceServlet;
import ch.avocado.share.servlet.resources.base.ViewRenderer;

import javax.servlet.annotation.WebServlet;

@WebServlet(value = "/module", name = "Module")
public class ModuleServlet extends ResourceServlet<Module> {

    @Override
    protected ResourceBean<Module> getBean() {
        return new ModuleBean();
    }

    @Override
    protected ViewRenderer getHtmlRenderer() {
        return new HtmlRenderer("templates/module/");
    }
}
