package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * DataHandler for all AccessControlObject subclasses.
 */
abstract class DataHandlerBase {

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

    private boolean addOwnership(long ownerId, long objectId) throws DataHandlerException, SQLException {
        PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.INSERT_OWNERSHIP);
        preparedStatement.setLong(SQLQueryConstants.INSERT_OWNERSHIP_INDEX_OWNER, ownerId);
        preparedStatement.setLong(SQLQueryConstants.INSERT_OWNERSHIP_INDEX_OBJECT, objectId);
        getConnectionHandler().insertDataSet(preparedStatement); // TODO: check result?
        return true;
    }

    protected boolean updateObject(AccessControlObjectBase object) throws DataHandlerException {
        if (object == null) throw new IllegalArgumentException("object is null");
        long objectId = Long.parseLong(object.getId());
        if (!updateDescription(objectId, object.getDescription())) return false;
        Long ownerId = null;
        if(object.getOwnerId() != null) {
            ownerId = Long.parseLong(object.getOwnerId());
        }
        return setOwnership(objectId, ownerId);
    }

    private boolean updateDescription(long objectId, String description) throws DataHandlerException {
        if (description == null) throw new IllegalArgumentException("description is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.UPDATE_ACCESS_CONTROL_DESCRIPTION);
            preparedStatement.setLong(SQLQueryConstants.UPDATE_ACCESS_CONTROL_DESCRIPTION_ID_INDEX, objectId);
            preparedStatement.setString(SQLQueryConstants.UPDATE_ACCESS_CONTROL_DESCRIPTION_DESCRIPTION_INDEX, description);
            return getConnectionHandler().updateDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * @param objectId The identifier of the object
     * @param ownerId  Id of the owner or null if there is no owner
     * @return
     * @throws DataHandlerException
     */
    private boolean setOwnership(long objectId, Long ownerId) throws DataHandlerException {
        try {
            if (ownerId == null) {
                deleteOwnership(objectId);
                // We don't return the result of deleteOwnership because
                // it's not a failure if there was no existing ownership
                return true;
            } else {
                Long currentOwner = getOwnerIdInternal(objectId);
                if (currentOwner == null) {
                    return addOwnership(objectId, ownerId);
                } else {
                    return updateOwnership(objectId, ownerId);
                }
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private boolean updateOwnership(long objectId, Long ownerId) throws DataHandlerException, SQLException {
        PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.UPDATE_OWNERSHIP);
        statement.setLong(SQLQueryConstants.UPDATE_OWNERSHIP_INDEX_OBJECT, objectId);
        statement.setLong(SQLQueryConstants.UPDATE_OWNERSHIP_INDEX_OWNER, ownerId);
        return getConnectionHandler().updateDataSet(statement);
    }

    private boolean deleteOwnership(long objectId) throws SQLException, DataHandlerException {
        PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.DELETE_OWNERSHIP);
        statement.setLong(SQLQueryConstants.DELETE_OWNERSHIP_INDEX_OBJECT, objectId);
        return getConnectionHandler().deleteDataSet(statement);
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
            preparedStatement.setLong(1, Long.parseLong(objectId));
            return getConnectionHandler().deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        } catch (NumberFormatException e) {
            throw new DataHandlerException("Invalid ID");
        }
    }

    /**
     * Create a new access control object
     *
     * @param object the object to add
     * @return The identifier of the object (not null)
     * @throws DataHandlerException If something went wrong
     */
    protected String addAccessControlObject(AccessControlObjectBase object) throws DataHandlerException {
        if (object == null) throw new IllegalArgumentException("object is null");
        if (object.getDescription() == null) throw new IllegalArgumentException("object.description is null");
        String id;
        Long ownerId = null;
        if(object.getOwnerId() != null) {
            ownerId = Long.parseLong(object.getOwnerId());
        }
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY);
            preparedStatement.setString(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX, object.getDescription());
            id = getConnectionHandler().insertDataSet(preparedStatement);
            if (id != null && ownerId != null) {
                addOwnership(ownerId, Long.parseLong(id));
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return id;
    }

    private Long getOwnerIdInternal(long objectId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.SELECT_OWNER_OF_OBJECT);
            preparedStatement.setLong(1, objectId);
            resultSet = connectionHandler.executeQuery(preparedStatement);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return null;
    }

    /**
     * @param objectId The identifier of the object
     * @return The id of the owner or null if there is no owner.
     * @throws DataHandlerException
     * @deprecated It's better to use a joined statement to query all values at once.
     */
    protected String getOwnerId(String objectId) throws DataHandlerException {
        Long ownerId = getOwnerIdInternal(Long.parseLong(objectId));
        if (ownerId == null) return null;
        return Long.toString(ownerId);
    }
}