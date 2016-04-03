package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Impl.UserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bergm on 24/03/2016.
 */
public class UserHandlerBean implements Serializable {
    private String id;
    private String name;
    private String prename;
    private String avatar;
    private String emailAddress;
    private String password;

    public void addUser()
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
            ServiceLocator.getService(IMailingService.class).sendVerificationEmail(user);
        } catch (ServiceNotFoundException | DataHandlerException e) {
            // todo: error handling
            e.printStackTrace();
        }
    }

    public void loadUser()
    {
        if(id == null) throw new IllegalArgumentException("id is null");

        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            User oldUser = userDataHandler.getUser(id);

            setEmailAddress(oldUser.getMail().getAddress());
            setPrename(oldUser.getPrename());
            setName(oldUser.getFullName());
        } catch (ServiceNotFoundException | DataHandlerException e) {
            // todo: error handling
            e.printStackTrace();
        }
    }

    public void updateUser() {
        if(id == null) throw new IllegalArgumentException("id is null");
        if(name == null) throw new IllegalArgumentException("name is null");
        if(prename == null) throw new IllegalArgumentException("prename is null");
        if(emailAddress == null) throw new IllegalArgumentException("emailAddress is null");

        try {
                IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
                User oldUser = userDataHandler.getUser(id);
                if(oldUser != null) {
                    if (!oldUser.getMail().getAddress().equals(emailAddress)) {
                        long theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
                        Date nextWeek = new Date(theFuture);

                        EmailAddressVerification verification = new EmailAddressVerification(nextWeek);
                        EmailAddress mail = new EmailAddress(false, emailAddress, verification);
                        oldUser.setMail(mail);
                        userDataHandler.addMail(oldUser);
                        ServiceLocator.getService(IMailingService.class).sendVerificationEmail(oldUser);
                    }
                    oldUser.setPrename(prename);
                    oldUser.setSurname(name);
                    userDataHandler.updateUser(oldUser);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        if (prename == null) throw new IllegalArgumentException("prename is null");
        this.prename = prename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if (avatar == null) throw new IllegalArgumentException("avatar is null");
        this.avatar = avatar;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        if (emailAddress == null ) throw new IllegalArgumentException("emailAddress is null");
        if (!emailAddress.toLowerCase().matches("[a-z0-9]+[_a-z0-9\\.-]*[a-z0-9]+@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) throw new IllegalArgumentException("emailAddress is not valid");

        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) throw new IllegalArgumentException("password is null");
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
