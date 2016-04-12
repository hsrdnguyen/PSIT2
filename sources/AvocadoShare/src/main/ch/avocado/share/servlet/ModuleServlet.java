package ch.avocado.share.servlet;

import ch.avocado.share.controller.ModuleBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.Module;

import javax.servlet.annotation.WebServlet;

@WebServlet("/module")
public class ModuleServlet extends ResourceServlet<Module>{

    @Override
    protected Class<? extends ResourceBean<Module>> getBeanClass() {
        return ModuleBean.class;
    }
}
