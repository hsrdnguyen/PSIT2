package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Group;
<<<<<<< HEAD
import ch.avocado.share.model.data.User;
=======
>>>>>>> 61fbff3800537ff29580806ccfe2e58a46155f3d
import ch.avocado.share.service.IGroupDataHandler;

/**
 * Created by coffeemakr on 21.03.16.
 */
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
