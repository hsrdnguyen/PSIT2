package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SecurityHandler extends DataHandlerBase implements ISecurityHandler {

    private int getAccessLevelInt(AccessLevelEnum level) throws DataHandlerException {
        boolean readable = level.containsLevel(AccessLevelEnum.READ);
        boolean writable = level.containsLevel(AccessLevelEnum.WRITE);
        boolean manageable = level.containsLevel(AccessLevelEnum.MANAGE);

        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_ACCESS_LEVEL_QUERY);
            preparedStatement.setBoolean(1, readable);
            preparedStatement.setBoolean(2, writable);
            preparedStatement.setBoolean(3, manageable);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new DataHandlerException("Could not find level: " + level);
            }
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }

    }

    private AccessLevelEnum getAccessLevelForResultSet(ResultSet rs) throws SQLException {
        boolean read, write, manage, owner;
        read = rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_READ_INDEX);
        write = rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_WRITE_INDEX);
        manage = rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_MANAGE_INDEX);
        owner = rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_OWNER_INDEX);
        return getAccessLevelForRights(read, write, manage, owner);
    }

    private AccessLevelEnum getAccessLevelForRights(boolean read, boolean write, boolean manage, boolean owner) {
        if (owner) {
            return AccessLevelEnum.OWNER;
        } else if (manage) {
            return AccessLevelEnum.MANAGE;
        } else if (write) {
            return AccessLevelEnum.WRITE;
        } else if (read) {
            return AccessLevelEnum.READ;
        }
        return AccessLevelEnum.NONE;
    }


    private PreparedStatement getSelectAccessLevelIncludingInheritedStatement(int ownerId, int objectId) throws DataHandlerException {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_ACCESS_LEVEL_INCLUDING_INHERITED);
            preparedStatement.setInt(1, ownerId);
            preparedStatement.setInt(2, objectId);
            preparedStatement.setInt(3, ownerId);
            preparedStatement.setInt(4, objectId);
            preparedStatement.setInt(5, objectId);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return preparedStatement;
    }

    @Override
    public AccessLevelEnum getAccessLevel(String identityId, String targetId) throws DataHandlerException {
        if (identityId == null || identityId.isEmpty()) throw new IllegalArgumentException("identityId is null or empty");
        if (targetId == null || targetId.isEmpty()) throw new IllegalArgumentException("targetId is null or empty");

        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        boolean read = false, write = false, manage = false, owner = false;
        int identityIdInt = Integer.parseInt(identityId);
        int targetIdInt = Integer.parseInt(targetId);

        if (identityIdInt == targetIdInt) {
            System.out.println("Same!");
            return AccessLevelEnum.MANAGE;
        } else {
            System.out.println("Different: " + targetIdInt + " - " + identityIdInt);
        }
        PreparedStatement preparedStatement = getSelectAccessLevelIncludingInheritedStatement(identityIdInt, targetIdInt);
        try {
            ResultSet rs = connectionHandler.executeQuery(preparedStatement);
            while (rs.next()) {
                read |= rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_READ_INDEX);
                write |= rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_WRITE_INDEX);
                manage |= rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_MANAGE_INDEX);
                owner |= rs.getBoolean(SQLQueryConstants.SELECT_ACCESS_LEVEL_OWNER_INDEX);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return getAccessLevelForRights(read, write, manage, owner);
    }

    @Override
    public AccessLevelEnum getAccessLevel(AccessIdentity identity, AccessControlObjectBase target) throws DataHandlerException {
        if(identity == null) throw new IllegalArgumentException("identity is null");
        if(target == null) throw new IllegalArgumentException("target is null");
        return getAccessLevel(identity.getId(), target.getId());
    }

    private PreparedStatement getAddAccessLevelStatement(int ownerId, int objectId, int level) throws DataHandlerException {
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.ADD_ACCESS_LEVEL_QUERY);
            statement.setInt(1, objectId);
            statement.setInt(2, ownerId);
            statement.setInt(3, level);
            return statement;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private PreparedStatement getSetAccessLevelStatement(int ownerId, int objectId, int level) throws DataHandlerException {
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SET_ACCESS_LEVEL_QUERY);
            statement.setInt(1, level);
            statement.setInt(2, objectId);
            statement.setInt(3, ownerId);
            return statement;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private boolean insertOrUpdateAccessLevel(int ownerId, int objectId, int level) throws DataHandlerException {
        PreparedStatement preparedStatement = getSetAccessLevelStatement(ownerId, objectId, level);
        try {
            if (!getConnectionHandler().updateDataSet(preparedStatement)) {
                preparedStatement = getAddAccessLevelStatement(ownerId, objectId, level);
                return null != getConnectionHandler().insertDataSet(preparedStatement);
            }
            return true;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean setAccessLevel(String identityId, String targetId, AccessLevelEnum accessLevel) throws DataHandlerException {
        if (identityId == null || identityId.isEmpty())
            throw new IllegalArgumentException("identityId is null or empty");
        if (targetId == null || targetId.isEmpty()) throw new IllegalArgumentException("targetId is null or empty");
        if (accessLevel == null) throw new IllegalArgumentException("accessLevel is null");
        int objectId = Integer.parseInt(identityId);
        int ownerId = Integer.parseInt(targetId);
        if (accessLevel == AccessLevelEnum.NONE) {
            return deleteAccess(ownerId, objectId);
        } else if (accessLevel == AccessLevelEnum.OWNER) {
            throw new IllegalArgumentException("can't set OWNER rights.");
        } else {
            int level = getAccessLevelInt(accessLevel);
            return insertOrUpdateAccessLevel(ownerId, objectId, level);
        }
    }

    @Override
    public boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel) throws DataHandlerException {
        if (identity == null) throw new IllegalArgumentException("identity is null");
        if (target == null) throw new IllegalArgumentException("target is null");
        if (accessLevel == null) throw new IllegalArgumentException("accessLevel is null");
        return setAccessLevel(identity.getId(), target.getId(), accessLevel);
    }

    private boolean deleteAccess(int ownerId, int objectId) throws DataHandlerException {
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.DELETE_ACCESS_LEVEL_QUERY);
            preparedStatement.setInt(2, ownerId);
            preparedStatement.setInt(1, objectId);
            return getConnectionHandler().deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) throws DataHandlerException {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_ANONYMOUS_ACCESS_LEVEL);
            preparedStatement.setInt(1, Integer.parseInt(target.getId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return AccessLevelEnum.NONE;
            } else {
                return getAccessLevelForResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean setAnonymousAccessLevel(AccessControlObjectBase object, AccessLevelEnum accessLevelEnum) throws DataHandlerException {
        int level = getAccessLevelInt(accessLevelEnum);
        int objectId = Integer.parseInt(object.getId());
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.UPDATE_ANONYMOUS_ACCESS_LEVEL);
            preparedStatement.setInt(SQLQueryConstants.UPDATE_ANONYMOUS_ACCESS_LEVEL_LEVEL_INDEX, level);
            preparedStatement.setInt(SQLQueryConstants.UPDATE_ANONYMOUS_ACCESS_LEVEL_OBJECT_ID_INDEX, objectId);
            if (!getConnectionHandler().updateDataSet(preparedStatement)) {
                preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.ADD_ANONYMOUS_ACCESS_LEVEL);
                preparedStatement.setInt(SQLQueryConstants.ADD_ANONYMOUS_ACCESS_LEVEL_LEVEL_INDEX, level);
                preparedStatement.setInt(SQLQueryConstants.ADD_ANONYMOUS_ACCESS_LEVEL_OBJECT_ID_INDEX, objectId);
                return null != getConnectionHandler().insertDataSet(preparedStatement);
            }
            return true;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }


    private Map<String, AccessLevelEnum> getObjectIdWithAccessFromResultSet(ResultSet resultSet, AccessLevelEnum filterLevel) throws SQLException {
        Map<String, AccessLevelEnum> objectsIdsWithAccess = new HashMap<>();
        while (resultSet.next()) {
            String groupId = "" + resultSet.getInt(1);
            boolean readable = resultSet.getBoolean(2);
            boolean writable = resultSet.getBoolean(3);
            boolean manageable = resultSet.getBoolean(4);
            AccessLevelEnum levelEnum = getAccessLevelForRights(readable, writable, manageable, false);
            if (filterLevel.containsLevel(levelEnum)) {
                objectsIdsWithAccess.put(groupId, levelEnum);
            }
        }
        return objectsIdsWithAccess;
    }

    @Override
    public Map<String, AccessLevelEnum> getGroupsWithAccessIncluding(AccessLevelEnum accessLevel, AccessControlObjectBase target) throws DataHandlerException {
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_GROUPS_WITH_ACCESS_ON_OBJECT);
            preparedStatement.setInt(1, Integer.parseInt(target.getId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            return getObjectIdWithAccessFromResultSet(resultSet, accessLevel);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public Map<String, AccessLevelEnum> getUsersWithAccessIncluding(AccessLevelEnum accessLevel, AccessControlObjectBase target) throws DataHandlerException {
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_USER_WITH_ACCESS_ON_OBJECT);
            preparedStatement.setInt(1, Integer.parseInt(target.getId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            return getObjectIdWithAccessFromResultSet(resultSet, accessLevel);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public List<String> getIdsOfObjectsOnWhichIdentityHasAccess(AccessIdentity identity, AccessLevelEnum accessLevelEnum) throws DataHandlerException {
        int level = getAccessLevelInt(accessLevelEnum);
        List<String> ids = new LinkedList<>();
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS);
            preparedStatement.setInt(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS_LEVEL_INDEX, level);
            preparedStatement.setInt(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX, Integer.parseInt(identity.getId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ids.add(Integer.toString(resultSet.getInt(1)));
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return ids;
    }
}
