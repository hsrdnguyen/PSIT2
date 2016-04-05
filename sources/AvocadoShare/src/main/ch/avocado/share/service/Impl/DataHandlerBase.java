package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * DataHandler for all AccessControlObject subclasses.
 */
public abstract class DataHandlerBase {

    private IDatabaseConnectionHandler connectionHandler = null;

    /**
     * @return An instance of IDatabaseConnectionHandler (not null).
     * @throws DataHandlerException If the service could not be found.
     */
    protected IDatabaseConnectionHandler getConnectionHandler() throws DataHandlerException {
        if (connectionHandler == null) {
            try {
                connectionHandler = ServiceLocator.getService(IDatabaseConnectionHandler.class);
            } catch (ServiceNotFoundException e) {
                throw new DataHandlerException(e.getMessage());
            }
        }
        return connectionHandler;
    }

    protected boolean updateDescription(String objectId, String description) throws DataHandlerException {
        if(objectId == null) throw new IllegalArgumentException("objectId is null");
        if(description == null) throw new IllegalArgumentException("description is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.UPDATE_ACCESS_CONTROL_DESCRIPTION);
            preparedStatement.setInt(SQLQueryConstants.UPDATE_ACCESS_CONTROL_DESCRIPTION_ID_INDEX, Integer.parseInt(objectId));
            preparedStatement.setString(SQLQueryConstants.UPDATE_ACCESS_CONTROL_DESCRIPTION_DESCRIPTION_INDEX, description);
            return getConnectionHandler().updateDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * Delete the access control object. This will also delete the subtype. (ON DELETE CASCADE).
     *
     * @param objectId The identifier of the object.
     * @return {@code true} if the object was found and deleted. Otherwise {@code false} is returned.
     * @throws DataHandlerException If something went wrong
     */
    protected boolean deleteAccessControlObject(String objectId) throws DataHandlerException {
        if (objectId == null) throw new IllegalArgumentException("objectId is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.DELETE_ACCESS_CONTROL_QUERY);
            preparedStatement.setLong(1, Integer.parseInt(objectId));
            return getConnectionHandler().deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        } catch (NumberFormatException e) {
            throw new DataHandlerException("Invalid ID");
        }
    }

    /**
     * Create a new access control object
     * @param description The description of the object.
     * @return The identifier of the object (not null)
     * @throws DataHandlerException If something went wrong
     */
    protected String addAccessControlObject(String description) throws DataHandlerException {
        if (description == null) throw new IllegalArgumentException("description is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY);
            preparedStatement.setString(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX, description);
            return getConnectionHandler().insertDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }
}