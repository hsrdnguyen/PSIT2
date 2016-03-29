package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IGroupDataHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by coffeemakr on 21.03.16.
 */
public class GroupDataHandler implements IGroupDataHandler {

    private IDatabaseConnectionHandler getDatabaseHandler() {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }

    private PreparedStatement getGetStatement(String id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        PreparedStatement statement;
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) return null;
        try {
            statement = connectionHandler.getPreparedStatement(SQLQueryConstants.GET_GROUP_BY_ID);
            statement.setString(SQLQueryConstants.GET_GROUP_BY_ID_ID_INDEX, id);
        } catch (SQLException e) {
            return null;
        }
        return statement;
    }

    private ResultSet executeGetStatement(PreparedStatement statement) {
        if (statement == null) throw new IllegalArgumentException("statement is null");
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) return null;
        try {
            return connectionHandler.executeQuery(statement);
        } catch (SQLException e) {
            return null;
        }
    }

    private Group getGroupFromResultSet(ResultSet resultSet) {
        String id, name, description;
        Date creationDate;
        // TODO: @muellcy1 fetch categories and rating .. and ownerId?
        try {
            id = resultSet.getString(SQLQueryConstants.GROUP_RESULT_ID_INDEX);
            name = resultSet.getString(SQLQueryConstants.GROUP_RESULT_NAME_INDEX);
            description = resultSet.getString(SQLQueryConstants.GROUP_RESULT_DESCRIPTION_INDEX);
            creationDate = resultSet.getDate(SQLQueryConstants.GROUP_RESULT_CREATION_DATE);
        } catch (SQLException e) {
            return null;
        }
        return new Group(id, new ArrayList<Category>(), creationDate, 0.0f, "???", name, description);
    }

    @Override
    public Group getGroup(String id) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement statement = getGetStatement(id);
        ResultSet resultSet = executeGetStatement(statement);
        return getGroupFromResultSet(resultSet);
    }

    private PreparedStatement getInsertStatement(String name, String description) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (description == null) throw new IllegalArgumentException("description is null");
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) return null;
        PreparedStatement statement;
        try {
            statement = connectionHandler.getPreparedStatement(SQLQueryConstants.INSERT_GROUP_QUERY);
            statement.setString(SQLQueryConstants.INSERT_GROUP_QUERY_NAME_INDEX, name);
            statement.setString(SQLQueryConstants.INSERT_GROUP_QUERY_DESCRIPTION_INDEX, description);
        } catch (SQLException e) {
            return null;
        }
        return statement;
    }

    private String executeInsertStatement(PreparedStatement statement) {
        if (statement == null) throw new IllegalArgumentException("statement is null");
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) return null;
        String identifier;
        try {
            identifier = connectionHandler.insertDataSet(statement);
        } catch (SQLException e) {
            return null;
        }
        return identifier;
    }

    @Override
    public String addGroup(Group group) {
        if (group == null) throw new IllegalArgumentException("group is null");
        if (group.getId() != null) throw new IllegalArgumentException("group.getId() is not null");
        PreparedStatement statement = getInsertStatement(group.getName(), group.getDescription());
        return executeInsertStatement(statement);
    }

    @Override
    public boolean updateGroup(Group group) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) return false;
        return false;
    }

    @Override
    public boolean deleteGroup(Group group) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if (connectionHandler == null) return false;
        return false;
    }

    @Override
    public Group getGroupByName(String name) {
        return null;
    }
}
