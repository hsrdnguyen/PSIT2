package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;
import org.w3c.dom.UserDataHandler;

import java.io.Serializable;

/**
 * Created by coffeemakr on 21.03.16.
 */
public class EmailAddressVerificationBean implements Serializable {
    private String code;
    private String email;

    public EmailAddressVerificationBean() {

    }

    /**
     * Verify the users email address
     * @return
     */
    public boolean verify() {
        IUserDataHandler userDataHandler = null;
        boolean isVerified = false;
        try {
            userDataHandler = ServiceLocator.GetService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
        }
        if(userDataHandler != null) {
            if(email != null && code != null) {
                User user = userDataHandler.getUserByEmailAddress(email);
                if(user != null) {
                    userDataHandler.verifyUser(user, code);
                }
            }
        }
        return isVerified;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if(code == null) throw new IllegalArgumentException("code can't be null");
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email == null) throw new IllegalArgumentException("email can't be null");
        this.email = email;
    }
}
