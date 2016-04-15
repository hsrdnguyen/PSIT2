package ch.avocado.share.servlet.resources;

import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.controller.UserBean;
import ch.avocado.share.model.data.User;
import ch.avocado.share.servlet.resources.base.HtmlRenderer;
import ch.avocado.share.servlet.resources.base.ResourceServlet;
import ch.avocado.share.servlet.resources.base.ViewRenderer;

import javax.servlet.annotation.WebServlet;

@WebServlet("/user")
public class UserServlet extends ResourceServlet<User> {

    @Override
    protected Class<? extends ResourceBean<User>> getBeanClass() {
        return UserBean.class;
    }

    @Override
    protected ViewRenderer getHtmlRenderer() {
        return new HtmlRenderer("templates/user/");
    }
}
