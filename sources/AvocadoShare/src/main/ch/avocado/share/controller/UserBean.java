package ch.avocado.share.controller;

import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserBean extends ResourceBean<User> {

    public static final String ERROR_EMPTY_PRENAME = "Vorname darf nicht leer sein.";
    public static final String ERROR_EMPTY_SURNAME = "Nachname darf nicht leer sein.";
    public static final String ERROR_PASSWORD_TOO_SHORT = "Das Passwort muss mindestens 8 Zeichen lang sein.";
    private static final String ERROR_EMPTY_EMAIL = "E-Mail-Adresse darf nicht leer sein.";
    private static final String ERROR_INVALID_EMAIL = "Die E-Mail-Adresse ist ungültig oder leider nicht erlaubt.";
    public static final String ERROR_EMPTY_PASSWORD = "ERROR_EMPTY_PASSWORD";
    public static final String ERROR_EMPTY_PASSWORD_CONFIRMATION = "Passwort-Bestätigung darf nicht leer sein.";
    public static final String ERROR_PASSWORD_CONFIRMATION_INCORRECT = "Passwörter stimmen nicht überein.";

    private String prename;
    private String surname;
    private String mail;
    private String password;
    private String passwordConfirmation;
    private String avatar = null;

    private void checkPrename() {
        if(getPrename() == null || getPrename().isEmpty()) {
            addFormError("prename", ERROR_EMPTY_PRENAME);
        }
    }

    private void checkSurname() {
        if(getSurname() == null || getSurname().isEmpty()) {
            addFormError("surname", ERROR_EMPTY_SURNAME);
        }
    }

    private void checkEmailAddress() {
        if (getMail() == null) {
            addFormError("mail", ERROR_EMPTY_EMAIL);
        } else if (!getMail().toLowerCase().matches("[a-z0-9]+[_a-z0-9\\.-]*[a-z0-9]+@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
            addFormError("mail", ERROR_INVALID_EMAIL);
        }
    }


    private void checkEmailAdressIsUnique() throws HttpBeanException, DataHandlerException {
        if(getMail() == null) {
            return;
        }
        if(getService(IUserDataHandler.class).getUserByEmailAddress(getMail()) != null) {
            // email already taken
            addFormError("mail", "E-Mail already taken");
        }
    }

    private void checkPasswords() {
        boolean invalid = false;
        if(password == null || password.isEmpty()) {
            addFormError("password", ERROR_EMPTY_PASSWORD);
            invalid = true;
        }
        if(passwordConfirmation == null ||  passwordConfirmation.isEmpty()) {
            addFormError("passwordConfirmation", ERROR_EMPTY_PASSWORD_CONFIRMATION);
            invalid = true;
        }
        if(!invalid) {
            if(!passwordConfirmation.equals(password)) {
                addFormError("password", ERROR_PASSWORD_CONFIRMATION_INCORRECT);
                invalid = true;
            }
            if(password.length() < 9 ) {
                addFormError("password",  ERROR_PASSWORD_TOO_SHORT);
            }
        }
    }

    @Override
    protected void ensureIsAuthenticatedToCreate() throws HttpBeanException {
        // I do nothing because unauthenticated users can create new users :)
    }

    @Override
    public User create() throws HttpBeanException, DataHandlerException {
        checkPrename();
        checkSurname();
        checkPasswords();
        checkEmailAddress();
        checkEmailAdressIsUnique();
        if(hasErrors()) {
            return null;
        }

        Date nextWeek = EmailAddressVerification.getDateFromExpiryInHours(24 * 7);
        EmailAddressVerification verification = new EmailAddressVerification(nextWeek);
        EmailAddress emailAddressObject = new EmailAddress(false, getMail(), verification);
        User user = new User(UserPassword.fromPassword(password), getPrename(), getSurname(), getAvatar(), emailAddressObject);
        user.setId(getService(IUserDataHandler.class).addUser(user));
        getService(IMailingService.class).sendVerificationEmail(user);
        return user;
    }

    @Override
    public User get() throws HttpBeanException, DataHandlerException {
        return getService(IUserDataHandler.class).getUser(getId());
    }

    @Override
    public List<User> index() {
        return new ArrayList<>();
    }

    @Override
    public void update() throws HttpBeanException, DataHandlerException {
        User user = getObject();
        if(prename != null) {
            checkPrename();
            user.setPrename(prename);
        }
        if(surname != null) {
            checkSurname();
            user.setSurname(surname);
        }
        if(password != null) {
            checkPasswords();
            user.setPassword(password);
        }
        if(mail != null) {
            checkEmailAddress();
            // TODO: update email?
        }
        if(!hasErrors()) {
            getService(IUserDataHandler.class).updateUser(user);
        }
    }

    @Override
    public void destroy() throws HttpBeanException, DataHandlerException {
        getService(IUserDataHandler.class).deleteUser(getObject());
    }

    @Override
    public String getAttributeName() {
        return "User";
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        if(prename != null) {
            prename = prename.trim();
        }
        this.prename = prename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        if(surname != null) {
            surname = surname.trim();
        }
        this.surname = surname;

        checkSurname();
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
        checkEmailAddress();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getAvatar() {
        if(avatar == null) return "1234.jpg";
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
