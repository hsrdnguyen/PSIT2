package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.service.IUserDataHandler;

import java.util.ArrayList;
import java.util.Date;

public class UserDataHandlerMock extends DataHandlerMockBase<User> implements IUserDataHandler {

    public static final String EXISTING_USER0 = "user0";
    public static final String EXISTING_USER1 = "user1";
    public static final String EXISTING_USER2 = "user2";
    public static final String EXISTING_USER3 = "user3";

    public UserDataHandlerMock() {
        super();
        for (int i = 0; i < 10; i++) {
            objects.add(new User("user" + i, new ArrayList<Category>(), new Date(1000), 0, "owner" + i, "description" + i, UserPassword.fromPassword("1234"), "prename" + i, "surname" + i, "avator" + i, new EmailAddress(true, "email" + i + "@zhaw.ch", null)));
        }
    }

    @Override
    public boolean addUser(User user) {
        return add(user);
    }

    @Override
    public boolean deleteUser(User user) {
        return delete(user);
    }

    @Override
    public User getUser(String userId) {
        return get(userId);
    }

    @Override
    public User getUserByEmailAddress(String emailAddress) {
        for (User user : objects) {
            if(user.getMail().getAddress().equals(emailAddress)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByEmailAddress(String emailAddress, boolean getVerification) {
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        return update(user);
    }

    @Override
    public boolean verifyUser(User user) {
        return false;
    }

    public static void use() throws Exception{
        ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
    }
}
