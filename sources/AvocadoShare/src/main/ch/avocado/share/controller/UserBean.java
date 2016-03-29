package ch.avocado.share.controller;

import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanException;

/**
 * Created by bergm on 29/03/2016.
 */
public class UserBean extends ResourceBean<User> {

    @Override
    public User create() throws HttpBeanException {
        return null;
    }

    @Override
    public User get() throws HttpBeanException {
        return null;
    }

    @Override
    public User[] index() throws HttpBeanException {
        return new User[0];
    }

    @Override
    public void update() throws HttpBeanException {

    }

    @Override
    public void destroy() throws HttpBeanException {

    }

    @Override
    public String getAttributeName() {
        return null;
    }
}
