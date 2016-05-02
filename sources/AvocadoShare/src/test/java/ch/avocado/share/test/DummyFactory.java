package ch.avocado.share.test;

import ch.avocado.share.model.data.*;

import java.util.Date;

public class DummyFactory {
    public static User newUser(int identifier) {
        final Date expiry = new Date(System.currentTimeMillis() + 24 * 60 * 1000);
        MailVerification verification = new MailVerification(expiry);
        EmailAddress email = new EmailAddress(false, "user"+ identifier + "@nowhere.nothing", verification);
        return new User(UserPassword.fromPassword(""), "Prename " + identifier, "Surname " + identifier, "avatar " + identifier, email);
    }

    public static File newFile(int i, User owner, Module module) {
        return new File(owner.getId(), "description " + i, "title " + i, new Date(), module.getId(), new DiskFile("123456789123456789", "image/png", ".png"));
    }

    public static Module newModule(int i, User owner) {
        return new Module(owner.getId(), "Module description " + i, "Module Name " + i);
    }
}
