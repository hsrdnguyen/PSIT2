package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static ch.avocado.share.common.constants.sql.AccessControlObjectConstants.*;


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

    private void addOwnership(long ownerId, long objectId) throws DataHandlerException, SQLException {
        PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(INSERT_OWNERSHIP);
        preparedStatement.setLong(INSERT_OWNERSHIP_INDEX_OWNER, ownerId);
        preparedStatement.setLong(INSERT_OWNERSHIP_INDEX_OBJECT, objectId);
        getConnectionHandler().insertDataSet(preparedStatement);
        // We don't check the result because if will fail if the object or owner doesn't exist
    }

    protected void updateObject(AccessControlObjectBase object) throws DataHandlerException, ObjectNotFoundException {
        if (object == null) throw new IllegalArgumentException("object is null");
        if (object.getId() == null) throw new IllegalArgumentException("object.id is null");
        long objectId;
        try {
            objectId = Long.parseLong(object.getId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("object.id is not a number");
        }
        if (!updateDescription(objectId, object.getDescription())) {
            throw new ObjectNotFoundException(object.getClass(), objectId);
        }
        Long ownerId = null;
        if(object.getOwnerId() != null) {
            ownerId = Long.parseLong(object.getOwnerId());
        }
        setOwnership(objectId, ownerId);
    }

    private boolean updateDescription(long objectId, String description) throws DataHandlerException {
        if (description == null) throw new IllegalArgumentException("description is null");
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(UPDATE_ACCESS_CONTROL_DESCRIPTION);
            preparedStatement.setLong(UPDATE_ACCESS_CONTROL_DESCRIPTION_ID_INDEX, objectId);
            preparedStatement.setString(UPDATE_ACCESS_CONTROL_DESCRIPTION_DESCRIPTION_INDEX, description);
            return getConnectionHandler().updateDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * @param objectId The identifier of the object
     * @param ownerId  Id of the owner or null if there is no owner
     * @throws DataHandlerException
     */
    private void setOwnership(long objectId, Long ownerId) throws DataHandlerException, ObjectNotFoundException {
        try {
            if (ownerId == null) {
                deleteOwnership(objectId);
            } else {
                Long currentOwner = getOwnerIdInternal(objectId);
                if (currentOwner == null) {
                    addOwnership(objectId, ownerId);
                } else {
                    updateOwnership(objectId, ownerId);
                }
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private void updateOwnership(long objectId, Long ownerId) throws DataHandlerException, SQLException, ObjectNotFoundException {
        PreparedStatement statement = getConnectionHandler().getPreparedStatement(UPDATE_OWNERSHIP);
        statement.setLong(UPDATE_OWNERSHIP_INDEX_OBJECT, objectId);
        statement.setLong(UPDATE_OWNERSHIP_INDEX_OWNER, ownerId);
        if(!getConnectionHandler().updateDataSet(statement)) {
            throw new ObjectNotFoundException(AccessControlObjectBase.class, objectId);
        }
    }

    private void deleteOwnership(long objectId) throws SQLException, DataHandlerException {
        PreparedStatement statement = getConnectionHandler().getPreparedStatement(DELETE_OWNERSHIP);
        statement.setLong(DELETE_OWNERSHIP_INDEX_OBJECT, objectId);
        getConnectionHandler().deleteDataSet(statement);
    }

    /**
     * Delete the access control object. This will also delete the subtype. (ON DELETE CASCADE).
     *
     * @param object The object to delete.
     * @return {@code true} if the object was found and deleted. Otherwise {@code false} is returned.
     * @throws DataHandlerException If something went wrong
     * @throws ObjectNotFoundException If the object doesn't exist
     */
    protected void deleteAccessControlObject(AccessControlObjectBase object) throws DataHandlerException, ObjectNotFoundException {
        if(object == null) throw new IllegalArgumentException("object is null");
        if(object.getId() == null) throw new IllegalArgumentException("object.id is null");
        long id;
        try {
            id = Long.parseLong(object.getId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("object.id is not a number");
        }
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(DELETE_ACCESS_CONTROL_QUERY);
            preparedStatement.setLong(1, id);
            if(!getConnectionHandler().deleteDataSet(preparedStatement)) {
                throw new ObjectNotFoundException(object.getClass(), id);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
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
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(INSERT_ACCESS_CONTROL_QUERY);
            preparedStatement.setString(INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX, object.getDescription());
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
            preparedStatement = connectionHandler.getPreparedStatement(SELECT_OWNER_OF_OBJECT);
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

    protected String getIdList(Collection<String> ids) {
        String idListForQuery = "( ";
        boolean addComma = false;
        for (String id : ids) {
            long parsedId;
            try {
                parsedId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ids contain invalid value");
            }
            if (!addComma) {
                addComma = true;
            } else {
                idListForQuery += " , ";
            }
            idListForQuery += parsedId;
        }
        idListForQuery += " )";
        return idListForQuery;
    }
}