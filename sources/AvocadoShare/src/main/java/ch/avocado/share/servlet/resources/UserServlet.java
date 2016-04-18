package ch.avocado.share.servlet.resources;

import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.controller.UserBean;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.servlet.resources.base.Action;
import ch.avocado.share.servlet.resources.base.HtmlRenderer;
import ch.avocado.share.servlet.resources.base.ResourceServlet;
import ch.avocado.share.servlet.resources.base.ViewRenderer;

import javax.servlet.annotation.WebServlet;

@WebServlet("/user")
public class UserServlet extends ResourceServlet<User> {

    @Override
    public ResourceBean<User> getBean() {
        return new UserBean();
    }

    @Override
    public ViewRenderer getHtmlRenderer() {
        return new HtmlRenderer("templates/user/");
    }

    @Override
    protected AccessLevelEnum getRequiredAccessForAction(Action action) {
        switch (action) {
            case VIEW:
                return AccessLevelEnum.READ;

            case UPDATE:
                return AccessLevelEnum.OWNER;

            case DELETE:
                return AccessLevelEnum.OWNER;

            case REPLACE:
                return AccessLevelEnum.OWNER;

            case CREATE:
                return null;

        }
        throw new RuntimeException();
    }

}