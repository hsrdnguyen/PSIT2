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
        if(user == null) throw new IllegalArgumentException("user is null");
        return false;
    }

    @Override
    public User getUser(String userId) {
        if(userId == null) throw new IllegalArgumentException("userId is null");
        PreparedStatement stmt = null;
        try {
            Date creationDate = getAccessControlObjectDate(userId);
            EmailAddress mail = getMail(userId);

            ResultSet rs;

            stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_USER_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));

            rs = stmt.executeQuery();

            if (rs.next())
            {
                return new User(rs.getString(SQLQueryConstants.USER_RESULT_ID_INDEX),
                        null,
                        creationDate,
                        0.0f,
                        "",
                        rs.getString(SQLQueryConstants.USER_RESULT_DESCRIPTION_INDEX),
                        new UserPassword(rs.getString(SQLQueryConstants.USER_RESULT_PASSWORD_INDEX)),
                        rs.getString(SQLQueryConstants.USER_RESULT_PRENAME_INDEX),
                        rs.getString(SQLQueryConstants.USER_RESULT_SURNAME_INDEX),
                        rs.getString(SQLQueryConstants.USER_RESULT_AVATAR_INDEX),
                        mail);
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private Date getAccessControlObjectDate(String userId) throws SQLException {
        if(userId == null) throw new IllegalArgumentException("userId is null");
        PreparedStatement stmt;
        stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_ACCESS_CONTROL_QUERY);
        stmt.setInt(1, Integer.parseInt(userId));

        ResultSet rs = stmt.executeQuery();
        Date creationDate = new Date();

        if(rs.next())
        {
            creationDate = rs.getDate(2);
        }
        return creationDate;
    }

    private EmailAddress getMail(String userId) throws SQLException {
        if(userId == null) throw new IllegalArgumentException("userId is null");
        PreparedStatement stmt;
        ResultSet rs;
        stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_MAIL_QUERY);
        stmt.setInt(1, Integer.parseInt(userId));

        rs = stmt.executeQuery();
        EmailAddress mail = null;

        if(rs.next())
        {
            mail = new EmailAddress(rs.getBoolean(3), rs.getString(2), null);
        }

        return mail;
    }

    @Override
    public User getUserByEmailAddress(String emailAddress) {
        if(emailAddress == null) throw new IllegalArgumentException("emailAddress is null");
        try {
            PreparedStatement ps = db.getPreparedStatement(SQLQueryConstants.SELECT_USER_BY_MAIL_QUERY);
            ps.setString(1, emailAddress);

            ResultSet rs = db.executeQuery(ps);

            if (rs.next())
            {
                EmailAddress mail = new EmailAddress(rs.getBoolean(8), rs.getString(7), null);

                return new User(rs.getString(1),
                        null,
                        new Date(),
                        0.0f,
                        "",
                        rs.getString(SQLQueryConstants.USER_RESULT_DESCRIPTION_INDEX),
                        new UserPassword(rs.getString(SQLQueryConstants.USER_RESULT_PASSWORD_INDEX)),
                        rs.getString(SQLQueryConstants.USER_RESULT_PRENAME_INDEX),
                        rs.getString(SQLQueryConstants.USER_RESULT_SURNAME_INDEX),
                        rs.getString(SQLQueryConstants.USER_RESULT_AVATAR_INDEX),
                        mail);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
    public User getUserByEmailAddress(String emailAddress, boolean getVerification) {
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        PreparedStatement stmt = null;
        try {
            stmt = db.getPreparedStatement(SQLQueryConstants.UPDATE_USER_QUERY);
            stmt.setString(1,user.getPrename());
            stmt.setString(2,user.getSurname());
            stmt.setString(3,user.getAvatar());
            stmt.setString(4,user.getDescription());
            stmt.setString(5,user.getPassword().getDigest());
            stmt.setInt(6,Integer.parseInt(user.getId()));
            return db.updateDataSet(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean verifyUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
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
    public boolean insertPasswordReset(PasswordResetVerification verification, String userId) {
        if(verification == null) throw new IllegalArgumentException("verification is null");
        if(userId == null) throw new IllegalArgumentException("userId is null");
        try {
            PreparedStatement stmt;
            stmt = db.getPreparedStatement(SQLQueryConstants.INSERT_PASSWORD_VERIFICATION_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, new java.sql.Date(verification.getExpiry().getTime()));
            stmt.setString(3, verification.getCode());
            db.insertDataSet(stmt);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<PasswordResetVerification> getPasswordVerifications(String userId) {
        if(userId == null) throw new IllegalArgumentException("userId is null");
        ArrayList<PasswordResetVerification> result = new ArrayList<>();

        try {
            PreparedStatement stmt;
            stmt = db.getPreparedStatement(SQLQueryConstants.SELECT_PASSWORD_VERIFICATION_QUERY);
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = db.executeQuery(stmt);

            while (rs.next())
            {
                result.add(new PasswordResetVerification(rs.getDate(2),rs.getString(3)));
            }


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
