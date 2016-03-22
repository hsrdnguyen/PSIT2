package ch.avocado.share.service;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;

/**
 * Created by coffeemakr on 21.03.16.
 */
public interface IGroupDataHandler {
    Group getGroup(String Id);
    boolean addGroup(Group group);
    boolean updateGroup(Group group);
    boolean deleteGroup(Group group);
    Group getGroupByName(String name);
    User[] getGroupMembers(Group group);
    Group[] getGroupsOfUser(User user);
}
