package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by coffeemakr on 21.03.16.
 */
public class GroupDataHandler extends DataHandlerBase implements IGroupDataHandler {

    private PreparedStatement getGetStatement(String id) throws DataHandlerException {
        if (id == null) throw new IllegalArgumentException("id is null");
        PreparedStatement statement;

        try {
            statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Group.SELECT_BY_ID_QUERY);
            statement.setString(SQLQueryConstants.Group.GET_BY_ID_ID_INDEX, id);
        } catch (SQLException e) {
            return null;
        }
        return statement;
    }

    /**
     * @param statement The statement to fetch the group
     * @return The result set (not null)
     * @throws DataHandlerException
     */
    private ResultSet executeGetStatement(PreparedStatement statement) throws DataHandlerException {
        if (statement == null) throw new IllegalArgumentException("statement is null");
        try {
            return getConnectionHandler().executeQuery(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private Group getGroupFromResultSet(ResultSet resultSet) throws DataHandlerException {
        if(resultSet == null) throw new IllegalArgumentException("resultSet is null");
        String id, name, description;
        Date creationDate;
        // TODO: @muellcy1 fetch categories and rating .. and ownerId?
        try {
            id = resultSet.getString(SQLQueryConstants.Group.RESULT_ID_INDEX);
            name = resultSet.getString(SQLQueryConstants.Group.RESULT_NAME_INDEX);
            description = resultSet.getString(SQLQueryConstants.Group.RESULT_DESCRIPTION_INDEX);
            creationDate = resultSet.getDate(SQLQueryConstants.Group.RESULT_CREATION_DATE);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return new Group(id, new ArrayList<Category>(), creationDate, 0.0f, "???", name, description);
    }

    @Override
    public Group getGroup(String id) throws DataHandlerException {
        if(id == null) throw new IllegalArgumentException("id is null");
        PreparedStatement statement = getGetStatement(id);
        return getGroupFromResultSet(executeGetStatement(statement));
    }

    private PreparedStatement getInsertStatement(String name, String description) throws DataHandlerException {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (description == null) throw new IllegalArgumentException("description is null");
        PreparedStatement statement;
        try {
            statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Group.INSERT_QUERY);
            statement.setString(SQLQueryConstants.Group.INSERT_QUERY_NAME_INDEX, name);
            statement.setString(SQLQueryConstants.Group.INSERT_QUERY_DESCRIPTION_INDEX, description);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return statement;
    }

    private String executeInsertStatement(PreparedStatement statement) throws DataHandlerException {
        if (statement == null) throw new IllegalArgumentException("statement is null");
        String identifier;
        try {
            identifier = getConnectionHandler().insertDataSet(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return identifier;
    }

    @Override
    public String addGroup(Group group) throws DataHandlerException {
        if (group == null) throw new IllegalArgumentException("group is null");
        if (group.getId() != null) throw new IllegalArgumentException("group.getId() is not null");
        PreparedStatement statement = getInsertStatement(group.getName(), group.getDescription());
        return executeInsertStatement(statement);
    }

    @Override
    public boolean updateGroup(Group group) {
        if(group == null) throw new IllegalArgumentException("group is null");

        return false;
    }

    @Override
    public boolean deleteGroup(Group group) throws DataHandlerException {
        if(group == null) throw new IllegalArgumentException("group is null");
        return deleteAccessControlObject(group.getId());
    }

    private PreparedStatement getGetByNameStatement(String name) throws DataHandlerException {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Group.SELECT_BY_NAME_QUERY);
            preparedStatement.setString(SQLQueryConstants.Group.GET_BY_NAME_NAME_INDEX, name);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return preparedStatement;
    }

    @Override
    public Group getGroupByName(String name) throws DataHandlerException {
        if(name == null) throw new IllegalArgumentException("name is null");
        return getGroupFromResultSet(executeGetStatement(getGetByNameStatement(name)));
    }
}
