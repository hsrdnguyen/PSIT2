package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.sql.RatingConstants;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IRatingDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ch.avocado.share.common.constants.sql.RatingConstants.*;

/**
 * Created by kunzlio1 on 17.04.2016.
 */
public class RatingDataHandler implements IRatingDataHandler {

    /**
     * Gets the Rating object for an AccessControlObject.
     *
     * @param ratedObjectId The id of the AccessControlObject, for which the Ranking should be returned.
     * @return The Ranking for the AccessControlObject.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public Rating getRatingForObject(long ratedObjectId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_SELECT_BY_OBJECT_ID);
            preparedStatement.setLong(1, ratedObjectId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return getRatingFromResultSet(resultSet);
            } else {
                return new Rating(ratedObjectId);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * Gets the rating as integer, which the a User gave to an AccessControlObject.
     *
     * @param ratingUserId  The id of the User, which had rated.
     * @param ratedObjectId The id of the AccessControlObject, which was rated
     * @return The rating as integer, which the a User gave to the AccessControlObject.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public int getRatingForUserAndObject(long ratingUserId, long ratedObjectId) throws DataHandlerException, ObjectNotFoundException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_SELECT_BY_USER_ID_AND_OBJECT_ID);
            preparedStatement.setLong(1, ratingUserId);
            preparedStatement.setLong(2, ratedObjectId);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) throw new ObjectNotFoundException(Rating.class, ratingUserId + ", " + ratedObjectId);
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public void putRating(long ratedAccessObjectId, long userId, int rating) throws DataHandlerException {
        if(!updateRating(ratedAccessObjectId, userId, rating)) {
            insertRating(ratedAccessObjectId, userId, rating);
        }
    }

    /**
     * Insert a new Ranking int to the database.
     *
     * @param ratedAccessObjectId The id of the rated AccessControlObject.
     * @param ratingUserId        The id of the rating User.
     * @param rating              The rating, which the User gave to the AccessControlObject.
     * @throws DataHandlerException     This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     * @throws IllegalArgumentException If the rating is not between {@value RatingConstants#MIN_RATING_VALUE}
     *                                  and {@value RatingConstants#MAX_RATING_VALUE}
     */
    public void addRating(long ratedAccessObjectId, long ratingUserId, int rating) throws DataHandlerException {
        if (rating < MIN_RATING_VALUE || rating > MAX_RATING_VALUE) {
            throw new IllegalArgumentException("Rating not between " + MIN_RATING_VALUE
                    + " and " + MAX_RATING_VALUE);
        }
        insertRating(ratedAccessObjectId, ratingUserId, rating);
    }

    /**
     * Deletes a Ranking from the database.
     *
     * @param ratedAccessObjectId The id of the rated AccessControlObject.
     * @param ratingUserId        The id of the rating User.
     * @return True if the Ranking was deleted from the database. False if not.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public boolean deleteRating(long ratedAccessObjectId, long ratingUserId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_DELETE_RATING);
            preparedStatement.setLong(1, ratedAccessObjectId);
            preparedStatement.setLong(2, ratingUserId);
            return connectionHandler.deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * Updated a Ranking in the database.
     *
     * @param ratedAccessObjectId The id of the rated AccessControlObject.
     * @param ratingUserId        The id of the rating User.
     * @param rating              The changed rating, which the User gave to the AccessControlObject.
     * @return True if the Ranking was updated correctly in the database. False if not.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public boolean updateRating(long ratedAccessObjectId, long ratingUserId, int rating) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_UPDATE_RATING);
            preparedStatement.setInt(1, rating);
            preparedStatement.setLong(2, ratedAccessObjectId);
            preparedStatement.setLong(3, ratingUserId);
            return connectionHandler.updateDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private void insertRating(long ratedAccessObjectId, long ratingUserId, int rating) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_ADD_RATING);
            preparedStatement.setLong(1, ratedAccessObjectId);
            preparedStatement.setLong(2, ratingUserId);
            preparedStatement.setInt(3, rating);
            if(null == connectionHandler.insertDataSet(preparedStatement)) {
                throw new DataHandlerException("Rating could not be added");
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private Rating getRatingFromResultSet(ResultSet resultSet) throws SQLException {
        Rating rating;
        rating = new Rating(resultSet.getLong(1));
        do {
            rating.addRating(resultSet.getInt(3), resultSet.getLong(2));
        } while (resultSet.next());
        return rating;
    }

    private IDatabaseConnectionHandler getDatabaseHandler() throws DataHandlerException {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
    }
}
