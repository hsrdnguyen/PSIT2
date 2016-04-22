package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.common.constants.sql.UserConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandler extends DataHandlerBase implements IUserDataHandler {

    public static final int DELETE_USER_QUERY_ID_INDEX = 1;

    @Override
    public String addUser(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            user.setId(addAccessControlObject(user));
            long userId = Long.parseLong(user.getId());
            PreparedStatement stmt = connectionHandler.getPreparedStatement(UserConstants.INSERT_USER_QUERY);
            stmt.setLong(1, Long.parseLong(user.getId()));
            stmt.setString(2, user.getPrename());
            stmt.setString(3, user.getSurname());
            stmt.setString(4, user.getAvatar());
            stmt.setString(5, user.getPassword().getDigest());
            connectionHandler.insertDataSet(stmt);
            if(user.getPassword().getResetVerification() != null) {
                addPasswordResetVerification(userId, user.getPassword().getResetVerification());
            }
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
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(UserConstants.DELETE_USER_QUERY);
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
        String resetCode;
        Date resetExpiry;
        boolean emailVerified;
        String emailAddress;
        try {
            if (!resultSet.next()) {
                return null;
            }
            id = "" + resultSet.getLong(UserConstants.USER_RESULT_ID_INDEX);
            description = resultSet.getString(UserConstants.USER_RESULT_DESCRIPTION_INDEX);
            emailVerified = resultSet.getBoolean(UserConstants.USER_RESULT_VERIFIED_INDEX);
            emailAddress = resultSet.getString(UserConstants.USER_RESULT_ADDRESS_INDEX);
            password = new UserPassword(resultSet.getString(UserConstants.USER_RESULT_PASSWORD_INDEX));
            prename = resultSet.getString(UserConstants.USER_RESULT_PRENAME_INDEX);
            surname = resultSet.getString(UserConstants.USER_RESULT_SURNAME_INDEX);
            avatar = resultSet.getString(UserConstants.USER_RESULT_AVATAR_INDEX);
            creationDate = resultSet.getTimestamp(UserConstants.USER_RESULT_CREATION_DATE_INDEX);
            resetCode = resultSet.getString(UserConstants.USER_RESULT_RESET_CODE_INDEX);
            resetExpiry = resultSet.getTimestamp(UserConstants.USER_RESULT_RESET_EXPIRY_INDEX);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        email = new EmailAddress(emailVerified, emailAddress, null);
        if (resetCode != null && resetExpiry != null) {
            PasswordResetVerification verification = new PasswordResetVerification(resetExpiry, resetCode);
            password.setResetVerification(verification);
        }
        return new User(id, null, creationDate, 0.0f, description, password, prename, surname, avatar, email);
    }

    private User getUserFromPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataHandlerException {
        if (preparedStatement == null) throw new IllegalArgumentException("preparedStatement is null");
        ResultSet resultSet = getConnectionHandler().executeQuery(preparedStatement);
        User user = getUserFromResultSet(resultSet);
        if (user != null && !user.getMail().isVerified()) {
            EmailAddressVerification emailAddressVerification = getEmailAddressVerification(user.getId(), user.getMail().getAddress());
            if (emailAddressVerification != null) {
                user.getMail().setVerification(emailAddressVerification);
                user.getMail().setDirty(false);
            }
        }
        return user;
    }

    @Override
    public User getUser(String userId) throws DataHandlerException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(UserConstants.SELECT_USER_QUERY);
            preparedStatement.setLong(1, Long.parseLong(userId));
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
        if (connectionHandler == null) return null;
        User user;
        try {
            PreparedStatement preparedStatement = connectionHandler.getPreparedStatement(UserConstants.SELECT_USER_BY_MAIL_QUERY);
            preparedStatement.setString(1, emailAddress);
            user = getUserFromPreparedStatement(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return user;
    }


    private EmailAddressVerification getEmailAddressVerification(String userId, String address) throws DataHandlerException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        Date expiry;
        String code;
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(UserConstants.SELECT_EMAIL_VERIFICATION);
            preparedStatement.setInt(UserConstants.SELECT_EMAIL_VERIFICATION_USER_ID_INDEX, Integer.parseInt(userId));
            preparedStatement.setString(UserConstants.SELECT_EMAIL_VERIFICATION_ADDRESS_INDEX, address);
            ResultSet resultSet = getConnectionHandler().executeQuery(preparedStatement);
            if (!resultSet.next()) {
                return null;
            }
            code = resultSet.getString(UserConstants.EMAIL_VERIFICATION_RESULT_CODE_INDEX);
            expiry = resultSet.getDate(UserConstants.EMAIL_VERIFICATION_RESULT_EXPIRY_INDEX);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return new EmailAddressVerification(expiry, code);
    }

    private void updateEmailAddress(EmailAddress emailAddress) throws SQLException, DataHandlerException {
        if(emailAddress == null) {
            throw new IllegalArgumentException("emailAddress is null");
        }
        if(emailAddress.isDirty()) {
            IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
            PreparedStatement statement = connectionHandler.getPreparedStatement(UserConstants.SET_EMAIL_VERIFICATION);
            statement.setBoolean(UserConstants.SET_EMAIL_VERIFICATION_INDEX_VALID, emailAddress.isValid());
            statement.setString(UserConstants.SET_EMAIL_VERIFICATION_INDEX_ADDRESS, emailAddress.getAddress());
            if(1 != statement.executeUpdate()) {
                throw new DataHandlerException("Update address failed");
            }
            emailAddress.setDirty(false);
        }else {
            System.out.println("not dirty");
        }
    }

    @Override
    public boolean addMail(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (user.getId() == null) throw new IllegalArgumentException("user.id is null");
        if (user.getMail() == null) throw new IllegalArgumentException("user.mail is null");
        if (user.getMail().getAddress() == null) throw new IllegalArgumentException("user.mail.address is null");

        PreparedStatement stmt;
        IDatabaseConnectionHandler connectionHandler;
        connectionHandler = getConnectionHandler();
        long userId = Long.parseLong(user.getId());
        try {
            stmt = connectionHandler.getPreparedStatement(UserConstants.INSERT_MAIL_QUERY);
            stmt.setLong(1, userId);
            stmt.setString(2, user.getMail().getAddress());
            connectionHandler.insertDataSet(stmt);

            stmt = connectionHandler.getPreparedStatement(UserConstants.INSERT_MAIL_VERIFICATION_QUERY);
            stmt.setLong(1, userId);
            EmailAddress mail = user.getMail();
            stmt.setString(2, mail.getAddress());
            EmailAddressVerification verification = mail.getVerification();
            stmt.setDate(3, new java.sql.Date(verification.getExpiry().getTime()));
            stmt.setString(4, user.getMail().getVerification().getCode());
            connectionHandler.insertDataSet(stmt);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return true;
    }

    private boolean updatePasswordResetVerification(long userId, PasswordResetVerification verification) throws DataHandlerException {
        try {
            if (verification == null) {
                deleteResetVerification(userId);
            } else {
                IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
                PreparedStatement statement = connectionHandler.getPreparedStatement(UserConstants.UPDATE_PASSWORD_RESET);
                statement.setTimestamp(UserConstants.UPDATE_PASSWORD_RESET_EXPIRY_INDEX, new Timestamp(verification.getExpiry().getTime()));
                statement.setString(UserConstants.UPDATE_PASSWORD_RESET_CODE_INDEX, verification.getCode());
                statement.setLong(UserConstants.UPDATE_PASSWORD_RESET_USER_INDEX, userId);
                if (statement.executeUpdate() == 0) {
                    addPasswordResetVerification(userId, verification);
                }
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return true;
    }

    @Override
    public boolean updateUser(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        if (!updateObject(user)) {
            return false;
        }
        long userId = Long.parseLong(user.getId());
        updatePasswordResetVerification(userId, user.getPassword().getResetVerification());

        PreparedStatement stmt = null;
        try {
            stmt = connectionHandler.getPreparedStatement(UserConstants.UPDATE_USER_QUERY);
            stmt.setString(1, user.getPrename());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getAvatar());
            stmt.setString(4, user.getPassword().getDigest());
            stmt.setLong(5, userId);
            if(!connectionHandler.updateDataSet(stmt)) {
                return false;
            }
            updateEmailAddress(user.getMail());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataHandlerException(e);
        }
        return true;
    }

    private boolean addPasswordResetVerification(long userId, PasswordResetVerification verification) throws DataHandlerException, SQLException {
        if (verification == null) throw new IllegalArgumentException("verification is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement stmt;

        stmt = connectionHandler.getPreparedStatement(UserConstants.INSERT_PASSWORD_VERIFICATION_QUERY);
        stmt.setLong(1, userId);
        stmt.setTimestamp(2, new Timestamp(verification.getExpiry().getTime()));
        stmt.setString(3, verification.getCode());
        return null != connectionHandler.insertDataSet(stmt);
    }

    private void deleteResetVerification(long userId) throws DataHandlerException, SQLException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement statement = connectionHandler.getPreparedStatement(UserConstants.DELETE_RESET_VERIFICATION);
        statement.setLong(1, userId);
        connectionHandler.deleteDataSet(statement);
    }

    @Override
    public List<User> getUsers(Collection<String> ids) throws DataHandlerException {
        List<User> users = new ArrayList<>(ids.size());
        for (String id : ids) {
            User user = getUser(id);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> search(Set<String> searchTerms) throws DataHandlerException {
        User foundUser;
        try {
            IDatabaseConnectionHandler connectionHandler = getConnectionHandler();

            String query = UserConstants.SEARCH_QUERY_START + UserConstants.SEARCH_QUERY_LIKE;

            for (int i = searchTerms.size() - 1; i > 0; i--) {
                query += UserConstants.SEARCH_QUERY_LINK + UserConstants.SEARCH_QUERY_LIKE;
            }

            PreparedStatement ps = connectionHandler.getPreparedStatement(query);
            int postition = 1;
            for (String tmp : searchTerms) {
                for(int i = 0; i < UserConstants.NUMBER_OF_TERMS_PER_LIKE; i++) {
                    ps.setString(postition, "%" + tmp.toLowerCase() + "%");
                    ++postition;
                }
            }
            System.out.println(query);
            ResultSet rs = connectionHandler.executeQuery(ps);
            return getUsersFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<User> getUsersFromResultSet(ResultSet rs) throws DataHandlerException {
        List<User> users = new LinkedList<>();
        User user;
        do {
            user = getUserFromResultSet(rs);
            if(user != null) {
                users.add(user);
            }
        }while(user != null);
        return users;
    }
}
