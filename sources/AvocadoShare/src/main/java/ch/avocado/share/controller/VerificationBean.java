package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.io.Serializable;

/**
 * Created by bergm on 23/03/2016.
 */
public class VerificationBean implements Serializable {

    private String code;
    private String email;
    private String errorMessage;

    public boolean verifyEmailCode() {
        IUserDataHandler userDataHandler = null;
        boolean isVerified = false;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (email != null && code != null) {
            User user = null;
            try {
                user = userDataHandler.getUserByEmailAddress(email);
            } catch (DataHandlerException e) {
                e.printStackTrace();
                return false;
            }
            if (user != null && !user.getMail().isVerified() && user.getMail().getVerification() != null) {
                if(user.getMail().getVerification().getCode().equals(code) && !user.getMail().getVerification().isExpired()) {
                    user.getMail().verify();
                    try {
                        isVerified = userDataHandler.updateUser(user);
                    } catch (DataHandlerException e) {
                        isVerified = false;
                    }
                }
            }
        }
        return isVerified;
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
