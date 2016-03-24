package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;

import java.util.Date;

/**
 * Created by bergm on 24/03/2016.
 */
public class UserRegistrationBean {

    public void addUser(String name, String prename, String avatar, String emailAddress, String password)
    {
        if(name == null) throw new IllegalArgumentException("name is null");
        if(prename == null) throw new IllegalArgumentException("prename is null");
        if(avatar == null) throw new IllegalArgumentException("avatar is null");
        if(emailAddress == null) throw new IllegalArgumentException("emailAddress is null");
        if(password == null) throw new IllegalArgumentException("password is null");

        long theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
        Date nextWeek = new Date(theFuture);

        EmailAddressVerification verification = new EmailAddressVerification(nextWeek);
        EmailAddress mail = new EmailAddress(false, emailAddress, verification);
        User user = new User(new UserPassword(password), prename, name, avatar, mail);

        try {
            user.setId(ServiceLocator.getService(IUserDataHandler.class).addUser(user));
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }

    }
}
