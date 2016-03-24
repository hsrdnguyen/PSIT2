package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.sql.PreparedStatement;
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

            PreparedStatement stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY);
            user.setId(db.insertDataSet(stmt));

            stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_USER_QUERY);
            stmt.setString(1,user.getId());
            stmt.setString(2,user.getPrename());
            stmt.setString(3,user.getSurname());
            stmt.setString(4,user.getAvatar());
            stmt.setString(5,user.getDescription());
            stmt.setString(6,user.getPassword().getDigest());


            stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_MAIL_QUERY);
            stmt.setString(1,user.getId());
            stmt.setString(2,user.getMail().getAddress());
            stmt.setDate(3,new java.sql.Date(user.getMail().getVerification().getExpiry().getTime()));
            stmt.setString(4,user.getMail().getVerification().getCode());
            db.insertDataSet(stmt);

            stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_MAIL_VERIFICATION_QUERY);
            stmt.setString(1,user.getId());
            stmt.setString(2,user.getMail().getAddress());
            db.insertDataSet(stmt);
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
            PreparedStatement stmt = db.getPreparedStatement(SQLQueryConstants.SET_MAIL_TO_VERIFIED);
            stmt.setString(1,user.getId());
            db.updateDataSet(stmt);
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
