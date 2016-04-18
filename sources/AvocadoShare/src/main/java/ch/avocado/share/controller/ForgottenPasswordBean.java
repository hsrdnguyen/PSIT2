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


    private boolean storeVerification(User user, IUserDataHandler userDataHandler) {
        PasswordResetVerification verification = user.getPassword().getPasswordResetVerification();
        try {
            if(!userDataHandler.addPasswordResetVerification(verification, user.getId())) {
                errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
                return false;
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        return true;
    }


    private boolean sendResetMail(User user) {
        IMailingService mailingService;
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        if(!mailingService.sendPasswordResetEmail(user)) {
            errorMessage = ErrorMessageConstants.ERROR_SEND_MAIL_FAILED;
            return false;
        }
        return true;
    }

    /**
     * @return True if a password reset link was send to the user
     */
    public boolean requestNewPassword() {
        if (checkParameterEmail()) return false;

        IUserDataHandler userDataHandler = getUserDataHandler();
        if(userDataHandler == null) return false;

        User user = getUserFromEmail(userDataHandler, email);
        if (user == null) {
            // We don't want the requester to know if we found the email
            return true;
        }

        user.getPassword().setPasswordResetVerification(createPasswordResetVerification());
        if(!storeVerification(user, userDataHandler)) {
            return false;
        }

        return sendResetMail(user);
    }

    private PasswordResetVerification createPasswordResetVerification() {
        Date expiry = PasswordResetVerification.getDateFromExpiryInHours(24 * 2);
        return new PasswordResetVerification(expiry);
    }

    private boolean checkParameterEmail() {
        if (email == null || email.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_EMAIL;
            return true;
        }
        return false;
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

    /**
     * If an error occurs while retrieving the user the {@link #errorMessage} is set.
     * @param userDataHandler The data handler to use
     * @param email The email of the user
     * @return The user or null
     */
    private User getUserFromEmail(IUserDataHandler userDataHandler, String email) {
        if(userDataHandler == null) throw new IllegalArgumentException("userDataHandler is null");
        if(email == null) throw new IllegalArgumentException("email is null");
        User user;
        try {
            user = userDataHandler.getUserByEmailAddress(email);
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return null;
        }
        return user;
    }

    /**
     * If this function failes to retrieve the user data handler and returns null
     * the {@link #errorMessage} is set accordingly.
     * @return The user data handler or null
     */
    private IUserDataHandler getUserDataHandler() {
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return null;
        }
        return userDataHandler;
    }

    private User getUserFromEmailWithVerifications() {
        IUserDataHandler userDataHandler = getUserDataHandler();
        if(userDataHandler == null) return null;

        User user = getUserFromEmail(userDataHandler, email);
        if (user == null) {
            this.errorMessage = ErrorMessageConstants.ERROR_INVALID_CODE_OR_EMAIL;
            return null;
        }

        ArrayList<PasswordResetVerification> verifications;
        try {
            verifications = userDataHandler.getPasswordVerifications(user.getId());
        } catch (DataHandlerException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return null;
        }
        Date longest = verifications.get(0).getExpiry();
        user.getPassword().setPasswordResetVerification(verifications.get(0));

        for (PasswordResetVerification v : verifications) {
            if (v.getExpiry().compareTo(longest) > 0) {
                user.getPassword().setPasswordResetVerification(v);
                longest = v.getExpiry();
            }
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
            User user = getUserFromEmailWithVerifications();
            if (user != null && user.resetPassword(password, code)) {
                try {
                    ServiceLocator.getService(IUserDataHandler.class).updateUser(user);
                    return true;
                } catch (ServiceNotFoundException | DataHandlerException e) {
                    errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
                }
            } else {
                errorMessage = ErrorMessageConstants.ERROR_GENERAL_FAILURE;
            }
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
