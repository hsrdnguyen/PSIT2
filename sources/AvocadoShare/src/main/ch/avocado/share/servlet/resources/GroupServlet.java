package ch.avocado.share.servlet.resources;

import ch.avocado.share.controller.GroupBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.servlet.resources.base.HtmlRenderer;
import ch.avocado.share.servlet.resources.base.ResourceServlet;
import ch.avocado.share.servlet.resources.base.ViewRenderer;

import javax.servlet.annotation.WebServlet;

@WebServlet("/group")
public class GroupServlet extends ResourceServlet<Group> {
    @Override
    protected ResourceBean<Group> getBean() {
        return new GroupBean();
    }

    @Override
    protected ViewRenderer getHtmlRenderer() {
        return new HtmlRenderer("templates/group/");
    }

}
