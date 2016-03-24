package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IUserDataHandler;

/**
 * Created by bergm on 19/03/2016.
 */
public class UserDataHandlerMock implements IUserDataHandler {
    @Override
    public String addUser(User user) {
        return "";
    }

    @Override
    public boolean deleteUser(User user) {
        return false;
    }

    @Override
    public User getUser(String userId) {
        return null;
    }

    @Override
    public User getUserByEmailAddress(String emailAddress) {
        return null;
    }

    @Override
    public User getUserByEmailAddress(String emailAddress, boolean getVerification) {
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        return false;
    }

    @Override
    public boolean verifyUser(User user) {
        return false;
    }

    @Override
    public boolean addUserToGroup(User user, Group group) {
        return false;
    }
}
