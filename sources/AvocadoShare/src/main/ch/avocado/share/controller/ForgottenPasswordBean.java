package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.PasswordResetVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by coffeemakr on 21.03.16.
 */
public class ForgottenPasswordBean implements Serializable {
    private String email;
    private String code;
    private String password;
    private String passwordConfirmation;
    private String errorMessage;

    public ForgottenPasswordBean() {
        errorMessage = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) throw new IllegalArgumentException("email can't be null");
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    /**
     * @todo: this method needs some refactoring
     * @return True if a password reset link was send to the user
     */
    public boolean createNewPassword() {
        IMailingService mailingService;
        IUserDataHandler userDataHandler;
        User user;
        if (email == null || email.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_EMAIL;
            return false;
        }
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        try {
            user = userDataHandler.getUserByEmailAddress(email);
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        if (user == null) {
            return true;
        }
        Date expiry = PasswordResetVerification.getDateFromExpiryInHours(24 * 2);
        PasswordResetVerification passwordResetVerification = new PasswordResetVerification(expiry);
        user.getPassword().setPasswordResetVerification(passwordResetVerification);
        try {
            userDataHandler.addPasswordResetVerififcation(passwordResetVerification, user.getId());
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        if (!mailingService.sendPasswordResetEmail(user)) {
            errorMessage = ErrorMessageConstants.ERROR_SEND_MAIL_FAILED;
            return false;
        }
        try {
            userDataHandler.updateUser(user);
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        return true;
    }

    public void setCode(String code) {
        if (code == null) throw new IllegalArgumentException("code can't be null");
        this.code = code;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        if (passwordConfirmation == null) throw new IllegalArgumentException("passwordConfirmation can't be null");
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) throw new IllegalArgumentException("password can't be null");
        this.password = password;
    }

    private User getUser() {
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return null;
        }
        User user = null;
        try {
            user = userDataHandler.getUserByEmailAddress(this.email);
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;;
            return null;
        }
        ArrayList<PasswordResetVerification> verifications = null;
        try {
            verifications = userDataHandler.getPasswordVerifications(user.getId());
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return null;
        }
        Date longest = verifications.get(0).getExpiry();
        user.getPassword().setPasswordResetVerification(verifications.get(0));

        for (PasswordResetVerification v: verifications) {
            if (v.getExpiry().compareTo(longest) > 0){
                user.getPassword().setPasswordResetVerification(v);
                longest = v.getExpiry();
            }
        }
        if (user == null) {
            this.errorMessage = ErrorMessageConstants.ERROR_INVALID_CODE_OR_EMAIL;
            return null;
        }
        return user;
    }


    /**
     * Reset the password. If the change does fail you can query the error message by calling getErrorMessage().
     *
     * @return {@code true} if the password was changed successfully. Otherwise {@code false} is returned.
     */
    public boolean resetPassword() {
        if (password == null || password.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_PASSWORD;
        } else if (passwordConfirmation == null || passwordConfirmation.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_PASSWORD_CONFIRMATION;
        } else if (email == null || email.isEmpty() || code == null || code.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_INVALID_CODE_OR_EMAIL;
        } else if (!password.equals(passwordConfirmation)) {
            errorMessage = ErrorMessageConstants.ERROR_PASSWORDS_DO_NOT_MATCH;
        } else {
            User user = getUser();
            if (user != null && user.resetPassword(password, code)) {
                try {
                    ServiceLocator.getService(IUserDataHandler.class).updateUser(user);
                    return true;
                } catch (ServiceNotFoundException | DataHandlerException e) {
                    errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
                }
            }else {
                errorMessage = ErrorMessageConstants.ERROR_GENERAL_FAILURE;
            }
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
