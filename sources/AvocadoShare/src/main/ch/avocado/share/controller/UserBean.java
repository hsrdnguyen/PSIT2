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

    @Override
    protected boolean hasMembers() {
        return true;
    }

    private void checkPrename(User user) {
        if(getPrename() == null || getPrename().isEmpty()) {
            user.addFieldError("prename", ERROR_EMPTY_PRENAME);
        }
    }

    private void checkSurname(User user) {
        if(getSurname() == null || getSurname().isEmpty()) {
            user.addFieldError("surname", ERROR_EMPTY_SURNAME);
        }
    }

    private void checkEmailAddress(User user) {
        if (getMail() == null) {
            user.addFieldError("mail", ERROR_EMPTY_EMAIL);
        } else if (!getMail().toLowerCase().matches("[a-z0-9]+[_a-z0-9\\.-]*[a-z0-9]+@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
            user.addFieldError("mail", ERROR_INVALID_EMAIL);
        }
    }


    private void checkEmailAdressIsUnique(User user) throws HttpBeanException, DataHandlerException {
        if(getMail() == null) {
            return;
        }
        if(getService(IUserDataHandler.class).getUserByEmailAddress(getMail()) != null) {
            // email already taken
            user.addFieldError("mail", "E-Mail-Adresse existiert bereits");
        }
    }

    private void checkPasswords(User user) {
        boolean invalid = false;
        if(password == null || password.isEmpty()) {
            user.addFieldError("password", ERROR_EMPTY_PASSWORD);
            invalid = true;
        }
        if(passwordConfirmation == null ||  passwordConfirmation.isEmpty()) {
            user.addFieldError("passwordConfirmation", ERROR_EMPTY_PASSWORD_CONFIRMATION);
            invalid = true;
        }
        if(!invalid) {
            if(!passwordConfirmation.equals(password)) {
                user.addFieldError("password", ERROR_PASSWORD_CONFIRMATION_INCORRECT);
                invalid = true;
            }
            if(password.length() < 9 ) {
                user.addFieldError("password",  ERROR_PASSWORD_TOO_SHORT);
            }
        }
    }

    @Override
    protected void ensureIsAuthenticatedToCreate() throws HttpBeanException {
        // I do nothing because unauthenticated users can create new users :)
    }

    private void addEmailAddress(User user, String emailAddress) throws HttpBeanException, DataHandlerException {
        long theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
        Date nextWeek = new Date(theFuture);
        EmailAddressVerification verification = new EmailAddressVerification(nextWeek);
        EmailAddress mail = new EmailAddress(false, emailAddress, verification);
        user.setMail(mail);
        getService(IUserDataHandler.class).addMail(user);
        getService(IMailingService.class).sendVerificationEmail(user);
    }

    @Override
    public User create() throws HttpBeanException, DataHandlerException {
        User user = new User(UserPassword.EMPTY_PASSWORD, "", "", "", new EmailAddress(false, "", null));

        checkPrename(user);
        checkSurname(user);
        checkPasswords(user);
        checkEmailAddress(user);
        checkEmailAdressIsUnique(user);
        if(user.hasErrors()) {
            return user;
        }
        user.setPrename(getPrename());
        user.setSurname(getSurname());
        user.setPassword(password);
        addEmailAddress(user, mail);
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
    public void update(User user) throws HttpBeanException, DataHandlerException {
        boolean userChanged = false;
        if(prename != null) {
            checkPrename(user);
            user.setPrename(prename);
            userChanged = true;
        }
        if(surname != null) {
            checkSurname(user);
            if(user.isValid()) {
                user.setSurname(surname);
                userChanged = true;
            }
        }
        if(password != null) {
            checkPasswords(user);
            if(user.isValid()) {
                user.setPassword(password);
                userChanged = true;
            }
        }
        if(mail != null) {
            checkEmailAddress(user);
            if (!user.hasErrors() && !user.getMail().getAddress().equals(mail)) {
                addEmailAddress(user, mail);
            }
        }

        if(!user.hasErrors() && userChanged) {
            getService(IUserDataHandler.class).updateUser(user);
        }
    }

    @Override
    public void destroy(User user) throws HttpBeanException, DataHandlerException {
        getService(IUserDataHandler.class).deleteUser(user);
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
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getAvatar() {
        // TODO: fix
        if(avatar == null) return "1234.jpg";
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
