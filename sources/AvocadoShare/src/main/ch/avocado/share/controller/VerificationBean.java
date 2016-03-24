package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;

import java.io.Serializable;

/**
 * Created by bergm on 23/03/2016.
 */
public class VerificationBean implements Serializable {

    private String code;
    private String email;

    public boolean verifyEmailCode(){

        IUserDataHandler userDataHandler = null;
        boolean isVerified = false;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
        }
        if(userDataHandler != null) {
            if(email != null && code != null) {
                User user = userDataHandler.getUserByEmailAddress(email);
                if(user != null) {
                    userDataHandler.verifyUser(user);
                }
            }
        }
        return isVerified;
    }


    public boolean verifyPasswordReset(){
        //todo implement password reset
        return true;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null) throw new IllegalArgumentException("code is null");
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) throw new IllegalArgumentException("email is null");
        this.email = email;
    }

}
