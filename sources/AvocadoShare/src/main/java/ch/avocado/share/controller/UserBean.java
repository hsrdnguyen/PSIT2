package ch.avocado.share.controller;

import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IAvatarStorageHandler;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.*;
import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserBean extends ResourceBean<User> {

    private static final String ERROR_EMPTY_PRENAME = "Vorname darf nicht leer sein.";
    private static final String ERROR_EMPTY_SURNAME = "Nachname darf nicht leer sein.";
    private static final String ERROR_PASSWORD_TOO_SHORT = "Das Passwort muss mindestens 8 Zeichen lang sein.";
    private static final String ERROR_EMPTY_EMAIL = "E-Mail-Adresse darf nicht leer sein.";
    private static final String ERROR_INVALID_EMAIL = "Die E-Mail-Adresse ist ungültig oder leider nicht erlaubt.";
    private static final String ERROR_EMPTY_PASSWORD = "Das Password darf nicht leer sein.";
    private static final String ERROR_EMPTY_PASSWORD_CONFIRMATION = "Die Passwort-Bestätigung darf nicht leer sein.";
    private static final String ERROR_PASSWORD_CONFIRMATION_INCORRECT = "Die Passwörter stimmen nicht überein.";
    private static final String PRENAME_TOO_LONG = "Der Vorname ist zu lang.";
    private static final String SURNAME_TOO_LONG = "Der Nachname ist zu lang.";
    private static final String ERROR_EMAIL_NOT_ZHAW = "Zurzeit sind nur E-Mail-Adressen der Zürcher Hochschule für Angewandte Wissenschaften (zhaw.ch) erlaubt.";

    private String prename;
    private String surname;
    private String mail;
    private String password;
    private String passwordConfirmation;
    private FileItem avatar = null;

    private static final int MAX_LENGTH_PRENAME = 50;
    private static final int MAX_LENGTH_SURNAME = 50;

    @Override
    protected boolean hasMembers() {
        return true;
    }

    private void checkPrename(User user) {
        if(getPrename() == null || getPrename().isEmpty()) {
            user.addFieldError("prename", ERROR_EMPTY_PRENAME);
        } else if(getPrename().length() > MAX_LENGTH_PRENAME) {
            user.addFieldError("prename", PRENAME_TOO_LONG);
        }
    }

    private void checkSurname(User user) {
        if(getSurname() == null || getSurname().isEmpty()) {
            user.addFieldError("surname", ERROR_EMPTY_SURNAME);
        } else if(getSurname().length() > MAX_LENGTH_SURNAME) {
            user.addFieldError("surname", SURNAME_TOO_LONG);
        }
    }

    private void checkEmailIsZhaw(User user) {
        if(!getMail().endsWith("@zhaw.ch") && !getMail().endsWith("@students.zhaw.ch")) {
            user.addFieldError("mail", ERROR_EMAIL_NOT_ZHAW);
        }
    }

    private void checkEmailAddress(User user) {
        if (getMail() == null) {
            user.addFieldError("mail", ERROR_EMPTY_EMAIL);
        } else if (!getMail().toLowerCase().matches("[a-z0-9]+[_a-z0-9\\.-]*[a-z0-9]+@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
            user.addFieldError("mail", ERROR_INVALID_EMAIL);
        } else {
            checkEmailIsZhaw(user);
        }
    }


    private void checkEmailAddressIsUnique(User user) throws DataHandlerException, ServiceNotFoundException {
        if(getMail() == null) {
            return;
        }
        try {
            getService(IUserDataHandler.class).getUserByEmailAddress(getMail());
            // email already taken
            user.addFieldError("mail", "E-Mail-Adresse existiert bereits");
        } catch (ObjectNotFoundException ignored) {
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

    private void addEmailToUser(User user, String emailaddress) {
        long theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
        Date nextWeek = new Date(theFuture);
        final Date expiry = nextWeek;
        MailVerification verification = new MailVerification(expiry);
        EmailAddress mail = new EmailAddress(false, emailaddress, verification);
        user.setMail(mail);
    }

    private void storeNewEmailAddress(User user, String emailAddress) throws DataHandlerException, ServiceNotFoundException {
        addEmailToUser(user, emailAddress);
        getService(IUserDataHandler.class).addMail(user);
        getService(IMailingService.class).sendVerificationEmail(user);
    }

    @Override
    public User create() throws DataHandlerException, ServiceNotFoundException, FileStorageException {
        User user = new User(UserPassword.fromPassword(""), "", "", "", new EmailAddress(false, "", null));

        checkPrename(user);
        checkSurname(user);
        checkPasswords(user);
        checkEmailAddress(user);
        checkEmailAddressIsUnique(user);
        if(user.hasErrors()) {
            return user;
        }
        if(getAvatar() != null) {
            storeAvatar(user, getAvatar());
        }
        user.setPrename(getPrename());
        user.setSurname(getSurname());
        user.setPassword(password);
        addEmailToUser(user, mail);
        user.setId(getService(IUserDataHandler.class).addUser(user));
        getService(IMailingService.class).sendVerificationEmail(user);
        return user;
    }

    @Override
    public User get() throws DataHandlerException, ServiceNotFoundException, ObjectNotFoundException {
        return getService(IUserDataHandler.class).getUser(getId());
    }

    @Override
    public List<User> index() {
        return new ArrayList<>();
    }


    private void storeAvatar(User user, FileItem avatar) throws FileStorageException, ServiceNotFoundException {
        IAvatarStorageHandler avatarStorageHandler = getService(IAvatarStorageHandler.class);
        InputStream inputStream = null;
        try {
            inputStream = avatar.getInputStream();
        } catch (IOException e) {
            throw new FileStorageException("Avatar konnte nicht gelesen werden");
        }
        String reference = avatarStorageHandler.storeAvatar(inputStream);
        user.setAvatar(reference);
    }

    @Override
    public void update(User user) throws ServiceException {
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
            // TODO: check implentation
            checkEmailAddress(user);
            if (!user.hasErrors() && !user.getMail().getAddress().equals(mail)) {
                storeNewEmailAddress(user, mail);

            }
        }

        if(avatar != null) {
            if(getAvatar() != null) {
                storeAvatar(user, getAvatar());
                userChanged = true;
            }
        }

        if(!user.hasErrors() && userChanged) {
            getService(IUserDataHandler.class).updateUser(user);
        }
    }

    @Override
    public void destroy(User user) throws ServiceException {
        getService(IUserDataHandler.class).deleteUser(user);
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

    public FileItem getAvatar() {
        return avatar;
    }

    public void setAvatar(FileItem avatar) {
        this.avatar = avatar;
    }
}
