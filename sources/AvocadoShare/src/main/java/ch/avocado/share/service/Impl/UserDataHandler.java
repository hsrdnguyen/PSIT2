package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static ch.avocado.share.common.constants.sql.UserConstants.*;

/**
 *
 */
public class UserDataHandler extends DataHandlerBase implements IUserDataHandler {


    @Override
    public String addUser(User user) throws DataHandlerException {
        if (user == null) throw new IllegalArgumentException("user is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            user.setId(addAccessControlObject(user));
            long userId = Long.parseLong(user.getId());
            PreparedStatement stmt = connectionHandler.getPreparedStatement(INSERT_USER_QUERY);
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
        if (user.getId() == null) throw new IllegalArgumentException("user.id is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(DELETE_USER_QUERY);
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
            id = "" + resultSet.getLong(USER_RESULT_ID_INDEX);
            description = resultSet.getString(USER_RESULT_DESCRIPTION_INDEX);
            emailVerified = resultSet.getBoolean(USER_RESULT_VERIFIED_INDEX);
            emailAddress = resultSet.getString(USER_RESULT_ADDRESS_INDEX);
            password = new UserPassword(resultSet.getString(USER_RESULT_PASSWORD_INDEX));
            prename = resultSet.getString(USER_RESULT_PRENAME_INDEX);
            surname = resultSet.getString(USER_RESULT_SURNAME_INDEX);
            avatar = resultSet.getString(USER_RESULT_AVATAR_INDEX);
            creationDate = resultSet.getTimestamp(USER_RESULT_CREATION_DATE_INDEX);
            resetCode = resultSet.getString(USER_RESULT_RESET_CODE_INDEX);
            resetExpiry = resultSet.getTimestamp(USER_RESULT_RESET_EXPIRY_INDEX);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        email = new EmailAddress(emailVerified, emailAddress, null);
        if (resetCode != null && resetExpiry != null) {
            MailVerification verification = new MailVerification(resetExpiry, resetCode);
            password.setResetVerification(verification);
        }
        return new User(id, null, creationDate, 0.0f, description, password, prename, surname, avatar, email);
    }

    private User getUserFromPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataHandlerException {
        if (preparedStatement == null) throw new IllegalArgumentException("preparedStatement is null");
        ResultSet resultSet = getConnectionHandler().executeQuery(preparedStatement);
        User user = getUserFromResultSet(resultSet);
        if (user != null && !user.getMail().isVerified()) {
            MailVerification emailAddressVerification = getEmailAddressVerification(user.getId(), user.getMail().getAddress());
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
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SELECT_USER_QUERY);
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
            PreparedStatement preparedStatement = connectionHandler.getPreparedStatement(SELECT_USER_BY_MAIL_QUERY);
            preparedStatement.setString(1, emailAddress);
            user = getUserFromPreparedStatement(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return user;
    }


    private MailVerification getEmailAddressVerification(String userId, String address) throws DataHandlerException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        Date expiry;
        String code;
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SELECT_EMAIL_VERIFICATION);
            preparedStatement.setInt(SELECT_EMAIL_VERIFICATION_USER_ID_INDEX, Integer.parseInt(userId));
            preparedStatement.setString(SELECT_EMAIL_VERIFICATION_ADDRESS_INDEX, address);
            ResultSet resultSet = getConnectionHandler().executeQuery(preparedStatement);
            if (!resultSet.next()) {
                return null;
            }
            code = resultSet.getString(EMAIL_VERIFICATION_RESULT_CODE_INDEX);
            expiry = resultSet.getTimestamp(EMAIL_VERIFICATION_RESULT_EXPIRY_INDEX);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        final Date expiry1 = expiry;
        final String code1 = code;
        return new MailVerification(expiry1, code1);
    }

    private void updateEmailAddress(EmailAddress emailAddress) throws SQLException, DataHandlerException {
        if(emailAddress == null) {
            throw new IllegalArgumentException("emailAddress is null");
        }
        if(emailAddress.isDirty()) {
            IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
            PreparedStatement statement = connectionHandler.getPreparedStatement(SET_EMAIL_VERIFICATION);
            statement.setBoolean(SET_EMAIL_VERIFICATION_INDEX_VALID, emailAddress.isValid());
            statement.setString(SET_EMAIL_VERIFICATION_INDEX_ADDRESS, emailAddress.getAddress());
            if(1 != statement.executeUpdate()) {
                throw new DataHandlerException("Update address failed");
            }
            emailAddress.setDirty(false);
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
        String address = user.getMail().getAddress();
        try {
            stmt = connectionHandler.getPreparedStatement(INSERT_MAIL_QUERY);
            stmt.setLong(INSERT_MAIL_INDEX_USER, userId);
            stmt.setString(INSERT_MAIL_INDEX_ADDRESS, address);
            stmt.setBoolean(INSERT_MAIL_INDEX_VERIFIED, user.getMail().isVerified());
            connectionHandler.insertDataSet(stmt);
            if(!user.getMail().isVerified()) {
                MailVerification verification = user.getMail().getVerification();
                if(verification != null) {
                    addEmailAddressVerification(userId, address, verification);
                }
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return true;
    }

    private void addEmailAddressVerification(long userId, String address, MailVerification verification) throws SQLException, DataHandlerException {
        if(address == null) throw new IllegalArgumentException("address is null");
        if(verification == null) throw new IllegalArgumentException("verification is null");
        IDatabaseConnectionHandler handler = getConnectionHandler();
        PreparedStatement stmt = handler.getPreparedStatement(INSERT_MAIL_VERIFICATION);
        stmt.setLong(INSERT_MAIL_VERIFICATION_INDEX_USER, userId);
        stmt.setString(INSERT_MAIL_VERIFICATION_INDEX_ADDRESS, address);
        stmt.setTimestamp(INSERT_MAIL_VERIFICATION_INDEX_EXPIRY, new Timestamp(verification.getExpiry().getTime()));
        stmt.setString(INSERT_MAIL_VERIFICATION_INDEX_CODE, verification.getCode());
        handler.insertDataSet(stmt);
    }

    private boolean updatePasswordResetVerification(long userId, MailVerification verification) throws DataHandlerException {
        try {
            if (verification == null) {
                deleteResetVerification(userId);
            } else {
                IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
                PreparedStatement statement = connectionHandler.getPreparedStatement(UPDATE_PASSWORD_RESET);
                statement.setTimestamp(UPDATE_PASSWORD_RESET_EXPIRY_INDEX, new Timestamp(verification.getExpiry().getTime()));
                statement.setString(UPDATE_PASSWORD_RESET_CODE_INDEX, verification.getCode());
                statement.setLong(UPDATE_PASSWORD_RESET_USER_INDEX, userId);
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
            stmt = connectionHandler.getPreparedStatement(UPDATE_USER_QUERY);
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

    private boolean addPasswordResetVerification(long userId, MailVerification verification) throws DataHandlerException, SQLException {
        if (verification == null) throw new IllegalArgumentException("verification is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement stmt;

        stmt = connectionHandler.getPreparedStatement(INSERT_PASSWORD_VERIFICATION_QUERY);
        stmt.setLong(1, userId);
        stmt.setTimestamp(2, new Timestamp(verification.getExpiry().getTime()));
        stmt.setString(3, verification.getCode());
        return null != connectionHandler.insertDataSet(stmt);
    }

    private void deleteResetVerification(long userId) throws DataHandlerException, SQLException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement statement = connectionHandler.getPreparedStatement(DELETE_RESET_VERIFICATION);
        statement.setLong(1, userId);
        connectionHandler.deleteDataSet(statement);
    }

    @Override
    public List<User> getUsers(Collection<String> ids) throws DataHandlerException {
        String query = SELECT_USERS_BY_ID_LIST + getIdList(ids);
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        ResultSet result;
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(query);
            result = connectionHandler.executeQuery(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return getUsersFromResultSet(result);
    }

    @Override
    public List<User> search(Set<String> searchTerms) throws DataHandlerException {
        if (searchTerms == null) throw new IllegalArgumentException("searchTerms is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        searchTerms.remove("");
        if (searchTerms.isEmpty()) {
            return new ArrayList<>();
        }
        String query = SEARCH_QUERY_START + SEARCH_QUERY_LIKE;

        for (int i = searchTerms.size() - 1; i > 0; i--) {
            query += SEARCH_QUERY_LINK + SEARCH_QUERY_LIKE;
        }

        ResultSet rs;
        try {
            PreparedStatement ps = connectionHandler.getPreparedStatement(query);
            int position = 1;
            for (String tmp : searchTerms) {
                for (int i = 0; i < NUMBER_OF_TERMS_PER_LIKE; i++) {
                    ps.setString(position, "%" + tmp.toLowerCase() + "%");
                    ++position;
                }
            }
            rs = connectionHandler.executeQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataHandlerException(e);
        }
        return getUsersFromResultSet(rs);
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
