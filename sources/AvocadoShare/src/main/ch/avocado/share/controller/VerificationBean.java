package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.io.Serializable;

/**
 * Created by bergm on 23/03/2016.
 */
public class VerificationBean implements Serializable {

    public boolean verifyCode(String code, String email){
        try {
            IUserDataHandler userdata = ServiceLocator.getService(IUserDataHandler.class);

            User user = userdata.getUserByEmailAddress(email, true);

            if (user.getMail().isVerified() != true &&
                    user.getMail().getVerification().isExpired() &&
                    user.getMail().getVerification().getCode().equals(code)){
                userdata.verifyUser(user);
                return true;
            }

        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
