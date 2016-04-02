package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IUserDataHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Mock handler for user data
 */
public class UserDataHandlerMock extends DataHandlerMockBase<User> implements IUserDataHandler {

    public static final String EXISTING_USER0 = "user0";
    public static final String EXISTING_USER1 = "user1";
    public static final String EXISTING_USER2 = "user2";
    public static final String EXISTING_USER3 = "user3";

    /**
     * Save some precious time while testing :)
     */
    public static final UserPassword DEFAULT_PASSWORD = UserPassword.fromPassword("1234");

    public UserDataHandlerMock() {
        super();
        reset();
    }

    public void reset() {
        objects.clear();
        for (int i = 0; i < 100; i++) {
            String id = "user" + i;
            objects.put(id, new User(id, new ArrayList<Category>(), new Date(1000), 0, "owner" + i, "description" + i, DEFAULT_PASSWORD, "prename" + i, "surname" + i, "avator" + i, new EmailAddress(true, "email" + i + "@zhaw.ch", null)));
        }
    }

    @Override
    public String addUser(User user) {
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
        for (User user : objects.values()) {
            if (user.getMail().getAddress().equals(emailAddress)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean addMail(User user) throws SQLException {
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        return update(user);
    }

    @Override
    public boolean verifyUser(User user) {
        return false;
    }

    @Override
    public boolean insertPasswordReset(PasswordResetVerification verification, String userId) {
        return false;
    }

    @Override
    public ArrayList<PasswordResetVerification> getPasswordVerifications(String userId) {
        return null;
    }

    public static void use() throws Exception {
        if (!ServiceLocator.getService(IUserDataHandler.class).getClass().equals(UserDataHandlerMock.class)) {
            ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
        }
    }

    public User[] getAllUsers() {
        return getAll(User.class);
    }
}
