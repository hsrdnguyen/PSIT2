package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kunzlio1 on 17.04.2016.
 */
public class RatingDataHandler {

    private String insertRating(String ratedAccessObjectId, String userId, int rating) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        String id;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_ADD_RATING);
            preparedStatement.setLong(1, Long.parseLong(ratedAccessObjectId));
            preparedStatement.setLong(2, Long.parseLong(userId));
            preparedStatement.setInt(3, rating);
            id = connectionHandler.insertDataSet(preparedStatement);
        } catch (SQLException e) {
            return null;
        }
        return id;
    }

    private boolean deleteRating(String ratedAccessObjectId, String userId) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_DELETE_RATING);
            preparedStatement.setLong(1, Long.parseLong(ratedAccessObjectId));
            preparedStatement.setLong(2, Long.parseLong(userId));
            return connectionHandler.deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean updateRating(String ratedAccessObjectId, String userId, int rating) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        PreparedStatement preparedStatement;
        String id;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.Rating.SQL_UPDATE_RATING);
            preparedStatement.setInt(1, rating);
            preparedStatement.setLong(2, Long.parseLong(ratedAccessObjectId));
            preparedStatement.setLong(3, Long.parseLong(userId));
            return connectionHandler.updateDataSet(preparedStatement);
        } catch (SQLException e) {
            return false;
        }
    }

    private IDatabaseConnectionHandler getDatabaseHandler() {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }
}
