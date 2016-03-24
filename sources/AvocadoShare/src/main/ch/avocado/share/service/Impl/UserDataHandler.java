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
    public String addUser(User user) {
        if (user ==  null) throw new IllegalArgumentException("user is null");

        try {
            user.setId(db.insertDataSet(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY));

            db.insertDataSet(String.format(SQLQueryConstants.INSERT_USER_QUERY,
                    user.getId(),
                    user.getPrename(),
                    user.getSurname(),
                    user.getAvatar(),
                    user.getDescription(),
                    user.getPassword()));
            db.insertDataSet(String.format(SQLQueryConstants.INSERT_MAIL_QUERY,
                    user.getId(),
                    user.getMail().getAddress()));
            db.insertDataSet(String.format(SQLQueryConstants.INSERT_MAIL_VERIFICATION_QUERY,
                    user.getId(),
                    user.getMail().getAddress(),
                    user.getMail().getVerification().getExpiry(),
                    user.getMail().getVerification().getCode()));
            return user.getId();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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

    @Override
    public boolean addUserToGroup(User user, Group group) {
        return false;
    }
}
