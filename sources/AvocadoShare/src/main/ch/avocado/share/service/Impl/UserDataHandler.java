package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandler implements IUserDataHandler {

    public static final int DELETE_USER_QUERY_ID_INDEX = 1;
    IDatabaseConnectionHandler db;

    public UserDataHandler() throws ServiceNotFoundException {
        db = ServiceLocator.getService(IDatabaseConnectionHandler.class);
    }

    @Override
    public String addUser(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");

        try {

            PreparedStatement stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY);
            stmt.setString(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX, user.getDescription());
            user.setId(db.insertDataSet(stmt));

            stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_USER_QUERY);
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getPrename());
            stmt.setString(3, user.getSurname());
            stmt.setString(4, user.getAvatar());
            stmt.setString(5, user.getPassword().getDigest());
            db.insertDataSet(stmt);

            addMail(user);
            return user.getId();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean deleteUser(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (user.getId() == null) return false;
        try {
            PreparedStatement preparedStatement = db.getPreparedStatement(SQLQueryConstants.DELETE_USER_QUERY);
            preparedStatement.setInt(DELETE_USER_QUERY_ID_INDEX, Integer.parseInt(user.getId()));
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private User getUserFromResultSet(ResultSet resultSet) {
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
            e.printStackTrace();
            return null;
        }
        return new User(id, null, creationDate, 0.0f, "", description, password, prename, surname, avatar, email);
    }

    private User getUserFromPreparedStatement(PreparedStatement preparedStatement) throws SQLException {
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
    public User getUser(String userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        try {
            PreparedStatement preparedStatement = db.getPreparedStatement(SQLQueryConstants.SELECT_USER_QUERY);
            preparedStatement.setInt(1, Integer.parseInt(userId));
            return getUserFromPreparedStatement(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByEmailAddress(String emailAddress) {
        if (emailAddress == null) throw new IllegalArgumentException("emailAddress is null");
        try {
            PreparedStatement preparedStatement = db.getPreparedStatement(SQLQueryConstants.SELECT_USER_BY_MAIL_QUERY);
            preparedStatement.setString(1, emailAddress);
            return getUserFromPreparedStatement(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private EmailAddressVerification getEmailAddressVerification(String userId, String address) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        Date expiry;
        String code;
        try {
            PreparedStatement preparedStatement = db.getPreparedStatement(SQLQueryConstants.SELECT_EMAIL_VERIFICATION);
            preparedStatement.setInt(SQLQueryConstants.SELECT_EMAIL_VERIFICATION_USER_ID_INDEX, Integer.parseInt(userId));
            preparedStatement.setString(SQLQueryConstants.SELECT_EMAIL_VERIFICATION_ADDRESS_INDEX, address);
            ResultSet resultSet = db.executeQuery(preparedStatement);
            if(!resultSet.next()) {
                return null;
            }
            code = resultSet.getString(SQLQueryConstants.EMAIL_VERIFICATION_RESULT_CODE_INDEX);
            expiry = resultSet.getDate(SQLQueryConstants.EMAIL_VERIFICATION_RESULT_EXPIRY_INDEX);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return new EmailAddressVerification(expiry, code);
    }

    private Date getAccessControlObjectDate(String userId) throws SQLException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        PreparedStatement stmt;
        stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_ACCESS_CONTROL_QUERY);
        stmt.setInt(1, Integer.parseInt(userId));

        ResultSet rs = stmt.executeQuery();
        Date creationDate = new Date();

        if (rs.next()) {
            creationDate = rs.getDate(2);
        }
        return creationDate;
    }

    private EmailAddress getMail(String userId) throws SQLException {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        PreparedStatement stmt;
        ResultSet rs;
        stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_MAIL_QUERY);
        stmt.setInt(1, Integer.parseInt(userId));

        rs = stmt.executeQuery();
        EmailAddress mail = null;

        if (rs.next()) {
            mail = new EmailAddress(rs.getBoolean(3), rs.getString(2), null);
        }

        return mail;
    }


    @Override
    public boolean addMail(User user) throws SQLException {
        PreparedStatement stmt;
        stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_MAIL_QUERY);
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getMail().getAddress());
        db.insertDataSet(stmt);

        stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_MAIL_VERIFICATION_QUERY);
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getMail().getAddress());
        stmt.setDate(3, new java.sql.Date(user.getMail().getVerification().getExpiry().getTime()));
        stmt.setString(4, user.getMail().getVerification().getCode());
        db.insertDataSet(stmt);

        return true;
    }

    @Override
    public boolean updateUser(User user) {
        // TODO: update email address
        if (user == null) throw new IllegalArgumentException("user is null");
        PreparedStatement stmt = null;
        try {
            stmt = db.getPreparedStatement(SQLQueryConstants.UPDATE_USER_QUERY);
            stmt.setString(1, user.getPrename());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getAvatar());
            stmt.setString(4, user.getDescription());
            stmt.setString(5, user.getPassword().getDigest());
            stmt.setInt(6, Integer.parseInt(user.getId()));
            return db.updateDataSet(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean verifyUser(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        try {
            PreparedStatement stmt = db.getPreparedStatement(SQLQueryConstants.SET_MAIL_TO_VERIFIED);
            stmt.setString(1, user.getId());
            db.updateDataSet(stmt);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertPasswordReset(PasswordResetVerification verification, String userId) {
        if (verification == null) throw new IllegalArgumentException("verification is null");
        if (userId == null) throw new IllegalArgumentException("userId is null");
        try {
            PreparedStatement stmt;
            stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_PASSWORD_VERIFICATION_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, new java.sql.Date(verification.getExpiry().getTime()));
            stmt.setString(3, verification.getCode());
            db.insertDataSet(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<PasswordResetVerification> getPasswordVerifications(String userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        ArrayList<PasswordResetVerification> result = new ArrayList<>();

        try {
            PreparedStatement stmt;
            stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_PASSWORD_VERIFICATION_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = db.executeQuery(stmt);

            while (rs.next()) {
                result.add(new PasswordResetVerification(rs.getDate(2), rs.getString(3)));
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
