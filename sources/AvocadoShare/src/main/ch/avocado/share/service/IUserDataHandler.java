package ch.avocado.share.service;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;

import java.sql.SQLException;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IUserDataHandler {

    /**
     * adds the given user to the Database
     * @param user user to be added
     * @return Id of the added user. Null if error occured
     */
    String addUser(User user);

    /**
     * deletes the given user from the database
     * @param user user to be deleted
     * @return true if was successfully deleted
     */
    boolean deleteUser(User user);

    /**
     * Returns the user from the database
     * @param userId id of the requested user
     * @return user from database
     */
    User getUser(String userId);

    /**
     * Returns the user from the database selected by its
     * email address.
     * @param emailAddress email address of the user
     * @return user if there is a user with this email address
     *              or otherwise null.
     */
    User getUserByEmailAddress(String emailAddress);

    /**
     * Adds a new mail address to the database
     * @param user user with the new email
     * @return true if adding succeeded
     */
    boolean addMail(User user) throws SQLException;

    /**
     * Returns the user from the database selected by its
     * email address.
     * @param emailAddress email address of the user
     * @param getVerification sets if verification should be loaded from database
     * @return user if there is a user with this email address
     *              or otherwise null.
     */
    User getUserByEmailAddress(String emailAddress, boolean getVerification);

    /**
     * updates a user on the database
     * @param user user with updated data
     * @return true if it was successfully updated
     */
    boolean updateUser(User user);

    /**
     * verifies the user in the database
     * @param user user to be verified
     * @return true if verification was successful
     */
    boolean verifyUser(User user);

}
