package ch.avocado.share.model.data;

import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public class User extends AccessControlObjectBase{

    private UserPassword password;
    private String prename;
    private String surname;
    private String avatar;
    private EmailAddress mail;

    public User(String id, List<Category> categories, Date creationDate, Rating rating, String ownerId, String description, String password, String prename, String surname, String avatar, EmailAddress mail) {
        super(id, categories, creationDate, rating, ownerId, description);
        setAvatar(avatar);
        setMail(mail);
        setPassword(password);
        setPrename(prename);
        setSurname(surname);
    }

    public String getPasswordDigest() {
    	return this.password.getDigest();
    }
    
    public void setPassword(String password) {
    	if (password == null) throw new IllegalArgumentException("password is null");
    	this.password = UserPassword.fromPassword(password);
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        if (prename == null) throw new IllegalArgumentException("prename is null");
        this.prename = prename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        if (surname == null) throw new IllegalArgumentException("surname is null");
        this.surname = surname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if (avatar == null) throw new IllegalArgumentException("avatar is null");
        this.avatar = avatar;
    }

    public EmailAddress getMail() {
        return mail;
    }

    public void setMail(EmailAddress mail) {
        if (mail == null) throw new IllegalArgumentException("mail is null");
        this.mail = mail;
    }
}