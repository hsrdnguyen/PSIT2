package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IGroupDataHandler;

import java.util.ArrayList;
import java.util.Date;

/**
 * Mock handler for group data.
 */
public class GroupDataHandlerMock extends DataHandlerMockBase<Group> implements IGroupDataHandler {

    public static final int NUMBER_OF_GROUPS = 100;
    public static final String EXISTING_GROUP_NAME = "groupName0";
    public static final String EXISTING_GROUP0 = "group0";
    public static final String EXISTING_GROUP1 = "group1";
    public static final String EXISTING_GROUP2 = "group2";
    public static final String EXISTING_GROUP3 = "group3";

    public static final String NOT_EXISTING_GROUP_NAME = "Not existing Name";

    public GroupDataHandlerMock() {
        super();
        for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
            objects.add(new Group("group" + i, new ArrayList<Category>(), new Date(1000), 0, "owner" + i, "description" + i, "groupName" + i, new ArrayList<String>()));
        }
    }

    @Override
    public Group getGroup(String id) {
        return get(id);
    }

    @Override
    public String addGroup(Group group) {
        return add(group);
    }

    @Override
    public boolean updateGroup(Group group) {
        return update(group);
    }

    @Override
    public boolean deleteGroup(Group group) {
        return delete(group);
    }

    @Override
    public Group getGroupByName(String name) {
        for (Group group : objects) {
            if(group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    public static void use() throws Exception{
        ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
    }

    public Group[] getAllGroups() {
        return getAll(Group.class);
    }
}
