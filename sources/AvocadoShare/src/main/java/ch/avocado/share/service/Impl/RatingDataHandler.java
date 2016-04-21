package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.common.constants.sql.RatingConstants;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IRatingDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kunzlio1 on 17.04.2016.
 */
public class RatingDataHandler implements IRatingDataHandler{

    /**
     * Gets the Rating object for an AccessControlObject.
     * @param ratedObjectId The id of the AccessControlObject, for which the Ranking should be returned.
     * @return The Ranking for the AccessControlObject.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public Rating getRatingForObject(int ratedObjectId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) throw new DataHandlerException("DatabaseConnectionHandler is not available");
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_SELECT_BY_OBJECT_ID);
            preparedStatement.setLong(1, ratedObjectId);
            resultSet = preparedStatement.executeQuery();
            return getRatingFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * Gets the rating as integer, which the a User gave to an AccessControlObject.
     * @param ratingUserId The id of the User, which had rated.
     * @param ratedObjectId The id of the AccessControlObject, which was rated
     * @return The rating as integer, which the a User gave to the AccessControlObject.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public int getRatingForUserAndObject(long ratingUserId, long ratedObjectId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) throw new DataHandlerException("DatabaseConnectionHandler is not available");
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_SELECT_BY_USER_ID_AND_OBJECT_ID);
            preparedStatement.setLong(1, ratingUserId);
            preparedStatement.setLong(2, ratedObjectId);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) throw new DataHandlerException("There's no rating from that user for that object");
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * Insert a new Ranking int to the database.
     * @param ratedAccessObjectId The id of the rated AccessControlObject.
     * @param ratingUserId The id of the rating User.
     * @param rating The rating, which the User gave to the AccessControlObject.
     * @return The primary key, from the inserted dataset.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public long addRating(long ratedAccessObjectId, long ratingUserId, int rating) throws DataHandlerException {
        if(rating < RatingConstants.MIN_RATING_VALUE || rating > RatingConstants.MAX_RATING_VALUE) {
            throw new DataHandlerException("Rating not between " + RatingConstants.MIN_RATING_VALUE
                    + " and " + RatingConstants.MAX_RATING_VALUE);
        }

        return insertRating(ratedAccessObjectId, ratingUserId, rating);
    }

    /**
     * Deletes a Ranking from the database.
     * @param ratedAccessObjectId The id of the rated AccessControlObject.
     * @param ratingUserId The id of the rating User.
     * @return True if the Ranking was deleted from the database. False if not.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public boolean deleteRating(long ratedAccessObjectId, long ratingUserId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) throw new DataHandlerException("DatabaseConnectionHandler is not available");
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_DELETE_RATING);
            preparedStatement.setLong(1, ratedAccessObjectId);
            preparedStatement.setLong(2, ratingUserId);
            return connectionHandler.deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * Updated a Ranking in the database.
     * @param ratedAccessObjectId The id of the rated AccessControlObject.
     * @param ratingUserId The id of the rating User.
     * @param rating The changed rating, which the User gave to the AccessControlObject.
     * @return True if the Ranking was updated correctly in the database. False if not.
     * @throws DataHandlerException This Exception is thrown, if there is an error while accessing/reading or writing in the db.
     */
    public boolean updateRating(long ratedAccessObjectId, long ratingUserId, int rating) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) throw new DataHandlerException("DatabaseConnectionHandler is not available");
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_UPDATE_RATING);
            preparedStatement.setInt(1, rating);
            preparedStatement.setLong(2, ratedAccessObjectId);
            preparedStatement.setLong(3, ratingUserId);
            return connectionHandler.updateDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private long insertRating(long ratedAccessObjectId, long ratingUserId, int rating) throws DataHandlerException{
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) throw new DataHandlerException("DatabaseConnectionHandler is not available");
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_ADD_RATING);
            preparedStatement.setLong(1, ratedAccessObjectId);
            preparedStatement.setLong(2, ratingUserId);
            preparedStatement.setInt(3, rating);
            return Long.parseLong(connectionHandler.insertDataSet(preparedStatement));
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private Rating getRatingFromResultSet(ResultSet resultSet) throws DataHandlerException {
        Rating rating = null;
        try {
            if (resultSet.next()){
                rating = new Rating(resultSet.getLong(1));
                do {
                    rating.addRating(resultSet.getInt(3), resultSet.getLong(2));
                }while (resultSet.next());
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        if (rating != null) return rating;
        throw new DataHandlerException("There's no rating for that object");
    }

    private IDatabaseConnectionHandler getDatabaseHandler() {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }
}
