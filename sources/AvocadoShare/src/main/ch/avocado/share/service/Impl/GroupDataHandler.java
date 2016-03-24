package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IGroupDataHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    @Override
    public Group getGroup(String id) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.SQL_SELECT_GROUP_BY_ID);
            preparedStatement.setString(0, id);
            getDatabaseHandler().executeQuery(preparedStatement);
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public boolean addGroup(Group group) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        return false;
    }

    @Override
    public boolean updateGroup(Group group) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        return false;
    }

    @Override
    public boolean deleteGroup(Group group) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        return false;
    }

    @Override
    public Group getGroupByName(String name) {
        return null;
    }

    @Override
    public User[] getGroupMembers(Group group) {
        return new User[0];
    }

    @Override
    public Group[] getGroupsOfUser(User user) {
        return new Group[0];
    }
}
