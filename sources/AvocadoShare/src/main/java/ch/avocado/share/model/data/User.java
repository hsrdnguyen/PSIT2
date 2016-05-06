package ch.avocado.share.model.data;

import java.util.Date;
import java.util.List;

/**
 * User model.
 */
public class User extends AccessIdentity {

    private UserPassword password;
    private String prename;
    private String surname;
    private String avatar;
    private EmailAddress mail;

    public User(String id, List<Category> categories, Date creationDate, Rating rating, String description, UserPassword password, String prename, String surname, String avatar, EmailAddress mail) {
        super(id, categories, creationDate, rating, null, description);
        setAvatar(avatar);
        setMail(mail);
        setPassword(password);
        setPrename(prename);
        setSurname(surname);
    }

    public User(UserPassword password, String prename, String surname, String avatar, EmailAddress mail) {
        this(null, null, new Date(), new Rating(), "", password, prename, surname, avatar, mail);
    }

    /**
     * @return The userpassword object
     */
    public UserPassword getPassword() {
        return this.password;
    }

    /**
     * @param password The password object (not null)
     */
    private void setPassword(UserPassword password) {
        if (password == null) throw new IllegalArgumentException("password is null");
        this.password = password;
    }

    /**
     * @param password The plaintext password (not null)
     */
    public void setPassword(String password) {
        if (password == null) throw new IllegalArgumentException("password is null");
        this.password = UserPassword.fromPassword(password);
    }

    /**
     * @return The first name of the user
     */
    public String getPrename() {
        return prename;
    }

    /**
     * @param prename The first name of the user (not null)
     */
    public void setPrename(String prename) {
        if (prename == null) throw new IllegalArgumentException("prename is null");
        this.prename = prename;
    }

    /**
     * @return The last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname The last name (not null)
     */
    public void setSurname(String surname) {
        if (surname == null) throw new IllegalArgumentException("surname is null");
        this.surname = surname;
    }

    /**
     * @return The path? to the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar The path? to the avatar (not null)
     */
    public void setAvatar(String avatar) {
        if (avatar == null) throw new IllegalArgumentException("avatar is null");
        this.avatar = avatar;
    }

    /**
     * @return The email adress
     */
    public EmailAddress getMail() {
        return mail;
    }

    /**
     * @param mail The email adress (not null)
     */
    public void setMail(EmailAddress mail) {
        if (mail == null) throw new IllegalArgumentException("mail is null");
        this.mail = mail;
    }

    /**
     * @return The full name
     */
    public String getFullName() {
        return getPrename() + " " + getSurname();
    }

    /**
     * @param password The new password as plaintext (not null)
     * @param code     The reset code (not null)
     * @return {@code true} if the code is correct and the password changed.
     */
    public boolean resetPassword(String password, String code) {
        MailVerification verification = getPassword().getResetVerification();
        if(verification != null && !verification.isExpired() && verification.getCode().equals(code)) {
            getPassword().setPassword(password);
            getPassword().setResetVerification(null);
            return true;
        }
        return false;
    }


    @Override
    public String getReadableName() {
        return getFullName();
    }
}
