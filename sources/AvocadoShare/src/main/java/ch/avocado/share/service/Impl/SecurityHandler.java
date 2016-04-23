package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessIdentity;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            preparedStatement.setLong(1, ownerId);
            preparedStatement.setLong(2, objectId);
            preparedStatement.setLong(3, ownerId);
            preparedStatement.setLong(4, objectId);
            preparedStatement.setLong(5, ownerId);
            preparedStatement.setLong(6, objectId);
            preparedStatement.setLong(7, objectId);
            preparedStatement.setLong(8, objectId);
            preparedStatement.setLong(9, ownerId);

        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return preparedStatement;
    }

    @Override
    public AccessLevelEnum getAccessLevel(String identityId, String targetId) throws DataHandlerException {
        if (identityId == null || identityId.isEmpty())
            throw new IllegalArgumentException("identityId is null or empty");
        if (targetId == null || targetId.isEmpty()) throw new IllegalArgumentException("targetId is null or empty");

        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        boolean read = false, write = false, manage = false, owner = false;
        int identityIdInt = Integer.parseInt(identityId);
        int targetIdInt = Integer.parseInt(targetId);

        if (identityIdInt == targetIdInt) {
            return AccessLevelEnum.OWNER;
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
        if (identity == null) throw new IllegalArgumentException("identity is null");
        if (target == null) throw new IllegalArgumentException("target is null");
        if (target.getOwnerId() != null && identity.getId() != null) {
            if (target.getOwnerId().equals(identity.getId())) {
                return AccessLevelEnum.OWNER;
            }
        }
        return getAccessLevel(identity.getId(), target.getId());
    }

    private PreparedStatement getAddAccessLevelStatement(long ownerId, long objectId, int level) throws DataHandlerException {
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.ADD_ACCESS_LEVEL_QUERY);
            statement.setLong(SQLQueryConstants.ADD_ACCESS_LEVEL_QUERY_INDEX_OBJECT, objectId);
            statement.setLong(SQLQueryConstants.ADD_ACCESS_LEVEL_QUERY_INDEX_OWNER, ownerId);
            statement.setInt(SQLQueryConstants.ADD_ACCESS_LEVEL_QUERY_INDEX_LEVEL, level);
            return statement;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private PreparedStatement getSetAccessLevelStatement(long ownerId, long objectId, int level) throws DataHandlerException {
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SET_ACCESS_LEVEL_QUERY);
            statement.setLong(1, level);
            statement.setLong(2, objectId);
            statement.setLong(3, ownerId);
            return statement;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private boolean insertOrUpdateAccessLevel(long ownerId, long objectId, int level) throws DataHandlerException {
        PreparedStatement preparedStatement = getSetAccessLevelStatement(ownerId, objectId, level);
        try {
            if (!getConnectionHandler().updateDataSet(preparedStatement)) {
                // There was no rights entry yet
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

        long objectId = Long.parseLong(targetId);
        long ownerId = Long.parseLong(identityId);

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

    private boolean deleteAccess(long ownerId, long objectId) throws DataHandlerException {
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.DELETE_ACCESS_LEVEL_QUERY);
            preparedStatement.setLong(SQLQueryConstants.DELETE_ACCESS_LEVEL_QUERY_INDEX_OWNER_ID, ownerId);
            preparedStatement.setLong(SQLQueryConstants.DELETE_ACCESS_LEVEL_QUERY_INDEX_OBJECT_ID, objectId);
            return getConnectionHandler().deleteDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(String targetId) throws DataHandlerException {
        if (targetId == null) throw new IllegalArgumentException("targetId is null");
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_ANONYMOUS_ACCESS_LEVEL);
            preparedStatement.setInt(1, Integer.parseInt(targetId));
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
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) throws DataHandlerException {
        if (target == null) throw new IllegalArgumentException("target is null");
        return getAnonymousAccessLevel(target.getId());
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
            boolean owner = resultSet.getBoolean(5);
            AccessLevelEnum levelEnum = getAccessLevelForRights(readable, writable, manageable, owner);
            if (levelEnum.containsLevel(filterLevel)) {
                objectsIdsWithAccess.put(groupId, levelEnum);
            }
        }
        return objectsIdsWithAccess;
    }

    @Override
    public Map<String, AccessLevelEnum> getGroupsWithAccessIncluding(AccessLevelEnum accessLevel, AccessControlObjectBase target) throws DataHandlerException {
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_GROUPS_WITH_ACCESS_ON_OBJECT);
            long groupId = Long.parseLong(target.getId());
            preparedStatement.setLong(1, groupId);
            preparedStatement.setLong(2, groupId);

            ResultSet resultSet = preparedStatement.executeQuery();
            return getObjectIdWithAccessFromResultSet(resultSet, accessLevel);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public Map<String, AccessLevelEnum> getUsersWithAccessIncluding(AccessLevelEnum accessLevel, AccessControlObjectBase target) throws DataHandlerException {
        if (target == null) throw new IllegalArgumentException("target is null");
        if (accessLevel == null) throw new IllegalArgumentException("accessLevel is null");
        long objectId = Long.parseLong(target.getId());
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_USER_WITH_ACCESS_ON_OBJECT);
            preparedStatement.setLong(1, objectId);
            preparedStatement.setLong(2, objectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getObjectIdWithAccessFromResultSet(resultSet, accessLevel);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private List<String> getIdsOfObjectWhichAreOwned(AccessIdentity owner) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        List<String> ids = new LinkedList<>();
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(SQLQueryConstants.SELECT_OWNED_IDS);
            statement.setLong(SQLQueryConstants.SELECT_OWNED_IDS_OWNER_INDEX, Long.parseLong(owner.getId()));
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                ids.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return ids;
    }

    @Override
    public List<String> getIdsOfObjectsOnWhichIdentityHasAccess(AccessIdentity identity, AccessLevelEnum accessLevelEnum) throws DataHandlerException {
        if(accessLevelEnum == AccessLevelEnum.OWNER) {
            return getIdsOfObjectWhichAreOwned(identity);
        }
        int level = getAccessLevelInt(accessLevelEnum);
        List<String> ids = new LinkedList<>();
        int ownerId = Integer.parseInt(identity.getId());
        try {
            PreparedStatement preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS);
            preparedStatement.setInt(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS_LEVEL_INDEX, level);
            preparedStatement.setInt(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX, ownerId);
            preparedStatement.setInt(SQLQueryConstants.SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX_2, ownerId);
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
