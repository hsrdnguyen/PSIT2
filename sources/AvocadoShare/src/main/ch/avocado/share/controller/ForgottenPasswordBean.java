package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.PasswordResetVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by coffeemakr on 21.03.16.
 */
public class ForgottenPasswordBean implements Serializable {
    public static final String ERROR_INVALID_REQUEST = "Ungültige Anfrage.";
    public static final String ERROR_EMPTY_PASSWORD = "Passwort darf nicht leer sein.";
    public static final String ERROR_EMPTY_PASSWORD_CONFIRMATION = "Passwort-Bestätigung darf nicht leer sein.";
    public static final String ERROR_INTERNAL_SERVER = "Interner Server Fehler.";
    public static final String ERROR_INVALID_CODE_OR_EMAIL = "Bestätigungscode oder E-Mail-Adresse stimmen nicht.";
    public static final String ERROR_PASSWORDS_DO_NOT_MATCH = "Passwörter stimmen nicht überein.";
    public static final String ERROR_SEND_MAIL_FAILED = "Sender des E-Mail fehlgeschlagen.";
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

    public boolean sendEmail() {
        IMailingService mailingService;
        IUserDataHandler userDataHandler;
        User user;
        if (email == null) {
            errorMessage = ERROR_INVALID_REQUEST;
            return false;
        }
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ERROR_INTERNAL_SERVER;
            return false;
        }
        user = userDataHandler.getUserByEmailAddress(email);
        if(user == null) {
            return true;
        }
        Date expiry = PasswordResetVerification.getDateFromExpiryInHours(24*2);
        PasswordResetVerification passwordResetVerification = new PasswordResetVerification(expiry);
        user.getPassword().setPasswordResetVerification(passwordResetVerification);
        if(!mailingService.sendPasswordResetEmail(user)) {
            errorMessage = ERROR_SEND_MAIL_FAILED;
            return false;
        }
        userDataHandler.updateUser(user);
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
            this.errorMessage = ERROR_INTERNAL_SERVER;
            return null;
        }
        User user = userDataHandler.getUserByEmailAddress(this.email);
        if (user == null) {
            this.errorMessage = ERROR_INVALID_CODE_OR_EMAIL;
            return null;
        }
        return user;
    }

    private boolean verifyCode(String code, User user) {
        if (code == null) {
            this.errorMessage = ERROR_INVALID_REQUEST;
        }
        for (PasswordResetVerification verification : user.getPassword().getPasswordResetVerification()) {
            if (!verification.isExpired() && verification.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reset the password. If the change does fail you can query the error message by calling getErrorMessage().
     * @return {@code true} if the password was changed successfully. Otherwise {@code false} is returned.
     */
    public boolean resetPassword() {
        if (password == null || passwordConfirmation == null || email == null || code == null) {
            errorMessage = ERROR_INVALID_REQUEST;
        } else if (password.isEmpty()) {
            errorMessage = ERROR_EMPTY_PASSWORD;
        } else if (passwordConfirmation.isEmpty()) {
            errorMessage = ERROR_EMPTY_PASSWORD_CONFIRMATION;
        } else if (email.isEmpty()) {
            errorMessage = ERROR_INVALID_CODE_OR_EMAIL;
        } else if (!password.equals(passwordConfirmation)) {
            errorMessage = ERROR_PASSWORDS_DO_NOT_MATCH;
        } else {
            User user = getUser();
            if (user != null && verifyCode(code, user)) {
                user.setPassword(password);
                return true;
            }
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
