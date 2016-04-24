package ch.avocado.share.controller;

import ch.avocado.share.service.ICaptchaVerifier;


import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.PasswordResetVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;

/**
 * Bean to reset a forgotten password.
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
        try {
            if (!userDataHandler.updateUser(user)) {
                errorMessage = ErrorMessageConstants.UPDATE_USER_FAILED;
                return false;
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        return true;
    }

    /**
     * Send the reset link to the user
     *
     * @param user The user who should receive the mail
     * @return True if the mail could be sent successfully. Otherwise {@code false} is returned and
     * the {@link #errorMessage} is set accordingly.
     */
    private boolean sendResetMail(User user) {
        IMailingService mailingService;
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
            return false;
        }
        if (!mailingService.sendPasswordResetEmail(user)) {
            errorMessage = ErrorMessageConstants.ERROR_SEND_MAIL_FAILED;
            return false;
        }
        return true;
    }

    /**
     * Make sure you call {@link #setEmail(String)} before calling this method.
     *
     * @return True if a password reset link was send to the user
     * @param request
     */
    public boolean requestNewPassword(HttpServletRequest request) {
        if (!checkParameterEmail()) return false;
        if (!validateCaptcha(request)) return false;
        IUserDataHandler userDataHandler = getUserDataHandler();
        if (userDataHandler == null) return false;

        User user = getUserFromEmail(userDataHandler, email);
        if (user == null) {
            // We don't want the requester to know if we found the email
            return true;
        }

        user.getPassword().setResetVerification(createPasswordResetVerification());
        return storeVerification(user, userDataHandler) && sendResetMail(user);
    }

    private PasswordResetVerification createPasswordResetVerification() {
        Date expiry = PasswordResetVerification.getDateFromExpiryInHours(24 * 2);
        return new PasswordResetVerification(expiry);
    }

    /**
     * Checks if the email is set and not empty
     *
     * @return True is the email is valid. Otherwise false is returned and {@link #errorMessage} is set accordingly.
     */
    private boolean checkParameterEmail() {
        if (email == null || email.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_EMAIL;
            return false;
        }
        return true;
    }

    /**
     * Set the password reset code.
     *
     * @param code The password reset code.
     */
    public void setCode(String code) {
        if (code == null) throw new IllegalArgumentException("code can't be null");
        this.code = code;
    }

    /**
     * @param passwordConfirmation The repeated password
     */
    public void setPasswordConfirmation(String passwordConfirmation) {
        if (passwordConfirmation == null) throw new IllegalArgumentException("passwordConfirmation can't be null");
        this.passwordConfirmation = passwordConfirmation;
    }

    /**
     * @param password The new password
     */
    public void setPassword(String password) {
        if (password == null) throw new IllegalArgumentException("password can't be null");
        this.password = password;
    }

    /**
     * If an error occurs while retrieving the user the {@link #errorMessage} is set.
     *
     * @param userDataHandler The data handler to use
     * @param email           The email of the user
     * @return The user or null
     */
    private User getUserFromEmail(IUserDataHandler userDataHandler, String email) {
        if (userDataHandler == null) throw new IllegalArgumentException("userDataHandler is null");
        if (email == null) throw new IllegalArgumentException("email is null");
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
     * If this function fails to retrieve the user data handler and returns null
     * the {@link #errorMessage} is set accordingly.
     *
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


    private User getUserFromEmailAndCode(String email, String code) {
        IUserDataHandler userDataHandler = getUserDataHandler();
        if (userDataHandler == null) return null;

        User user = getUserFromEmail(userDataHandler, email);
        if (user == null || user.getPassword().getResetVerification() == null|| !user.getPassword().getResetVerification().getCode().equals(code)) {
            this.errorMessage = ErrorMessageConstants.ERROR_INVALID_CODE_OR_EMAIL;
            return null;
        }
        return user;
    }


    private boolean validateCaptcha(HttpServletRequest request) {
        ICaptchaVerifier captchaVerifier;
        try {
            captchaVerifier = ServiceLocator.getService(ICaptchaVerifier.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService();
            e.printStackTrace();
            return false;
        }
        errorMessage = ErrorMessageConstants.CAPTCHA_INCORRECT;
        return captchaVerifier.verifyRequest(request);
    }

    /**
     * Reset the password. If the change does fail you can query the error message by calling getErrorMessage().
     *
     * @return {@code true} if the password was changed successfully. Otherwise {@code false} is returned.
     */
    public boolean resetPassword() {
        errorMessage = "";
        if (password == null || password.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_PASSWORD;
        } else if (passwordConfirmation == null || passwordConfirmation.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_EMPTY_PASSWORD_CONFIRMATION;
        } else if (email == null || email.isEmpty() || code == null || code.isEmpty()) {
            errorMessage = ErrorMessageConstants.ERROR_INVALID_CODE_OR_EMAIL;
        } else if (!password.equals(passwordConfirmation)) {
            errorMessage = ErrorMessageConstants.ERROR_PASSWORDS_DO_NOT_MATCH;
        } else {
            User user = getUserFromEmailAndCode(email, code);
            if (user != null) {
                if (user.resetPassword(password, code)) {
                    try {
                        ServiceLocator.getService(IUserDataHandler.class).updateUser(user);
                        return true;
                    } catch (ServiceNotFoundException | DataHandlerException e) {
                        errorMessage = ErrorMessageConstants.ERROR_INTERNAL_SERVER;
                    }
                } else {
                    errorMessage = ErrorMessageConstants.ERROR_CODE_EXPIRED;
                    return false;
                }
            }
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
