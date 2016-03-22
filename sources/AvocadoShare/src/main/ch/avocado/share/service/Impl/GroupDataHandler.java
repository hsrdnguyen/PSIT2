package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IGroupDataHandler;

import java.sql.PreparedStatement;

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
    public Group getGroup(String Id) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
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
}
