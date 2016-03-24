package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.sql.SQLException;

/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandler implements IUserDataHandler {

    IDatabaseConnectionHandler db;

    public UserDataHandler() throws ServiceNotFoundException {
        db = ServiceLocator.getService(IDatabaseConnectionHandler.class);
    }

    @Override
    public boolean addUser(User user) {
        return false;
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
        try {
            db.updateDataSet(String.format(SQLQueryConstants.SET_MAIL_TO_VERIFIED, user.getId()));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
