package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IGroupDataHandler;


public class GroupDataHandlerMock implements IGroupDataHandler {
    @Override
    public Group getGroup(String Id) {
        return null;
    }

    @Override
    public boolean addGroup(Group group) {
        return false;
    }

    @Override
    public boolean updateGroup(Group group) {
        return false;
    }

    @Override
    public boolean deleteGroup(Group group) {
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
