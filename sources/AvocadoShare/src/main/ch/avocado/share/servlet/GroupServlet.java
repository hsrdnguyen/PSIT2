package ch.avocado.share.servlet;

import ch.avocado.share.controller.GroupBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.Group;

import javax.servlet.annotation.WebServlet;

@WebServlet("/group")
public class GroupServlet extends ResourceServlet<Group> {

    @Override
    protected Class<? extends ResourceBean<Group>> getBeanClass() {
        return GroupBean.class;
    }
}
