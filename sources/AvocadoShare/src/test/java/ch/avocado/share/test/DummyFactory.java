package ch.avocado.share.test;

import ch.avocado.share.model.data.*;

import java.util.Date;

public class DummyFactory {
    public static User newUser(int identifier) {
        EmailAddressVerification verification = new EmailAddressVerification(new Date(System.currentTimeMillis() + 24*60*1000));
        EmailAddress email = new EmailAddress(false, "user"+ identifier + "@nowhere.nothing", verification);
        return new User(UserPassword.EMPTY_PASSWORD, "Prename " + identifier, "Surname " + identifier, "avatar " + identifier, email);
    }

    public static File newFile(int i, User owner, Module module) {
        return new File(owner.getId(), "description " + i, "title " + i, "123456789123456789", new Date(), ".png", module.getId(), "image/png");
    }

    public static Module newModule(int i, User owner) {
        return new Module(owner.getId(), "Module description " + i, "Module Name " + i);
    }
}