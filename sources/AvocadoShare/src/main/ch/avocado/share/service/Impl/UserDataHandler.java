package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.DataHandlerMockBase;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.activation.DataHandler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandler extends DataHandlerBase implements IUserDataHandler {

    public static final int DELETE_USER_QUERY_ID_INDEX = 1;

    @Override
    public String addUser(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        IDatabaseConnectionHandler db = getConnectionHandler();
        if(db == null) return null;
        try {
            user.setId(addAccessControlObject(user.getDescription()));
            PreparedStatement stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_USER_QUERY);
            stmt.setInt(1, Integer.parseInt(user.getId()));
            stmt.setString(2, user.getPrename());
            stmt.setString(3, user.getSurname());
            stmt.setString(4, user.getAvatar());
            stmt.setString(5, user.getPassword().getDigest());
            db.insertDataSet(stmt);
            addMail(user);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataHandlerException(e);
        }
        return user.getId();
    }

    @Override
    public boolean deleteUser(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (user.getId() == null) return false;
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.DELETE_USER_QUERY);
            preparedStatement.setInt(DELETE_USER_QUERY_ID_INDEX, Integer.parseInt(user.getId()));
            return getConnectionHandler().deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataHandlerException(e);
        }
    }

    private User getUserFromResultSet(ResultSet resultSet) throws DataHandlerException {
        String id, description, prename, surname, avatar;
        Date creationDate;
        UserPassword password;
        EmailAddress email;
        try {
            if (!resultSet.next()) {
                return null;
            }
            id = "" + resultSet.getInt(SQLQueryConstants.USER_RESULT_ID_INDEX);
            description = resultSet.getString(SQLQueryConstants.USER_RESULT_DESCRIPTION_INDEX);
            boolean emailVerified = resultSet.getBoolean(SQLQueryConstants.USER_RESULT_VERIFIED_INDEX);
            String emailAddress = resultSet.getString(SQLQueryConstants.USER_RESULT_ADDRESS_INDEX);
            password = new UserPassword(resultSet.getString(SQLQueryConstants.USER_RESULT_PASSWORD_INDEX));
            prename = resultSet.getString(SQLQueryConstants.USER_RESULT_PRENAME_INDEX);
            surname = resultSet.getString(SQLQueryConstants.USER_RESULT_SURNAME_INDEX);
            avatar = resultSet.getString(SQLQueryConstants.USER_RESULT_AVATAR_INDEX);
            creationDate = resultSet.getDate(SQLQueryConstants.USER_RESULT_CREATION_DATE_INDEX);
            email = new EmailAddress(emailVerified, emailAddress, null);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return new User(id, null, creationDate, 0.0f, "", description, password, prename, surname, avatar, email);
    }

    private User getUserFromPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataHandlerException {
        if(preparedStatement == null) throw new IllegalArgumentException("preparedStatement is null");
        ResultSet resultSet = preparedStatement.executeQuery();
        User user = getUserFromResultSet(resultSet);
        if(user != null && !user.getMail().isVerified()) {
            EmailAddressVerification emailAddressVerification = getEmailAddressVerification(user.getId(), user.getMail().getAddress());
            if(emailAddressVerification != null) {
                user.getMail().setVerification(emailAddressVerification);
            }
        }
        return user;
    }

    @Override
    public User getUser(String userId) throws DataHandlerException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_USER_QUERY);
            preparedStatement.setInt(1, Integer.parseInt(userId));
            return getUserFromPreparedStatement(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataHandlerException(e);
        }
    }

    @Override
    public User getUserByEmailAddress(String emailAddress) throws DataHandlerException {
        if (emailAddress == null) throw new IllegalArgumentException("emailAddress is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        if(connectionHandler == null) return null;
        try {
            PreparedStatement preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.SELECT_USER_BY_MAIL_QUERY);
            preparedStatement.setString(1, emailAddress);
            return getUserFromPreparedStatement(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }


    private EmailAddressVerification getEmailAddressVerification(String userId, String address) throws DataHandlerException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        Date expiry;
        String code;
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_EMAIL_VERIFICATION);
            preparedStatement.setInt(SQLQueryConstants.SELECT_EMAIL_VERIFICATION_USER_ID_INDEX, Integer.parseInt(userId));
            preparedStatement.setString(SQLQueryConstants.SELECT_EMAIL_VERIFICATION_ADDRESS_INDEX, address);
            ResultSet resultSet = getConnectionHandler().executeQuery(preparedStatement);
            if(!resultSet.next()) {
                return null;
            }
            code = resultSet.getString(SQLQueryConstants.EMAIL_VERIFICATION_RESULT_CODE_INDEX);
            expiry = resultSet.getDate(SQLQueryConstants.EMAIL_VERIFICATION_RESULT_EXPIRY_INDEX);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return new EmailAddressVerification(expiry, code);
    }

    @Override
    public boolean addMail(User user) throws DataHandlerException {
        PreparedStatement stmt;
        IDatabaseConnectionHandler connectionHandler;
        connectionHandler = getConnectionHandler();
        try {
            stmt = connectionHandler.getPreparedStatement(SQLQueryConstants.INSERT_MAIL_QUERY);
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getMail().getAddress());
            connectionHandler.insertDataSet(stmt);

            stmt = connectionHandler.getPreparedStatement(SQLQueryConstants.INSERT_MAIL_VERIFICATION_QUERY);
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getMail().getAddress());
            stmt.setDate(3, new java.sql.Date(user.getMail().getVerification().getExpiry().getTime()));
            stmt.setString(4, user.getMail().getVerification().getCode());
            connectionHandler.insertDataSet(stmt);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return true;
    }

    @Override
    public boolean updateUser(User user) throws DataHandlerException {
        // TODO: update email address
        if (user == null) throw new IllegalArgumentException("user is null");
        if(!updateDescription(user.getId(), user.getDescription())) {
            return false;
        }
        PreparedStatement stmt = null;
        try {
            stmt = getConnectionHandler().getPreparedStatement(SQLQueryConstants.UPDATE_USER_QUERY);
            stmt.setString(1, user.getPrename());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getAvatar());
            stmt.setString(4, user.getPassword().getDigest());
            stmt.setInt(5, Integer.parseInt(user.getId()));
            return getConnectionHandler().updateDataSet(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean verifyUser(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        try {
            PreparedStatement stmt = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SET_MAIL_TO_VERIFIED);
            stmt.setInt(1, Integer.parseInt(user.getId()));
            return getConnectionHandler().updateDataSet(stmt);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean addPasswordResetVerification(PasswordResetVerification verification, String userId) throws DataHandlerException {
        if (verification == null) throw new IllegalArgumentException("verification is null");
        if (userId == null) throw new IllegalArgumentException("userId is null");
        try {
            PreparedStatement stmt;
            stmt = getConnectionHandler().getPreparedStatement(SQLQueryConstants.INSERT_PASSWORD_VERIFICATION_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, new java.sql.Date(verification.getExpiry().getTime()));
            stmt.setString(3, verification.getCode());
            return null != getConnectionHandler().insertDataSet(stmt);
        } catch (Exception e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public ArrayList<PasswordResetVerification> getPasswordVerifications(String userId) throws DataHandlerException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        ArrayList<PasswordResetVerification> result = new ArrayList<>();
        try {
            PreparedStatement stmt;
            stmt = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_PASSWORD_VERIFICATION_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = getConnectionHandler().executeQuery(stmt);
            while (rs.next()) {
                result.add(new PasswordResetVerification(rs.getDate(2), rs.getString(3)));
            }
        } catch (Exception e) {
            throw new DataHandlerException(e);
        }
        return result;
    }

    @Override
    public List<User> getUsers(Collection<String> ids) throws DataHandlerException {
        List<User> users = new ArrayList<>(ids.size());
        for(String id: ids) {
            User user = getUser(id);
            if(user != null) {
                users.add(user);
            }
        }
        return users;
    }
}
