package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Mock handler for group data.
 */
public class GroupDataHandlerMock extends DataHandlerMockBase<Group> implements IGroupDataHandler {

    private static final int NUMBER_OF_GROUPS = 100;
    public static final String EXISTING_GROUP_NAME = "groupName0";
    public static final String EXISTING_GROUP0 = "group0";
    public static final String EXISTING_GROUP1 = "group1";
    public static final String EXISTING_GROUP2 = "group2";
    public static final String EXISTING_GROUP3 = "group3";

    public static final String NOT_EXISTING_GROUP_NAME = "Not existing Name";

    public GroupDataHandlerMock() {
        super();
        reset();
    }

    public int getNumberOfGroups() {
        return objects.size();
    }

    /**
     * Reset all mock groups to initial state.
     */
    private void reset() {
        objects.clear();
        for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
            String id = "group" + i;
            objects.put(id, new Group(id, new ArrayList<Category>(), new Date(1000), 0, "owner" + i, "Eine Beschreibung der Gruppe " + i + ".\n Vielleicht wiederspiegelt diese Gruppe eine Klasse, dann könnte das hier stehen.", "groupName" + i));
        }
    }

    @Override
    public Group getGroup(String id) {
        return get(id);
    }

    @Override
    public List<Group> getGroups(Collection<String> ids) throws DataHandlerException {
        ArrayList<Group> groups = new ArrayList<>(ids.size());
        for(String id: ids) {
            Group group = getGroup(id);
            if(group != null) {
                groups.add(group);
            }
        }
        return groups;
    }

    @Override
    public String addGroup(Group group) {
        if(getGroupByName(group.getName()) != null) {
            return null;
        }
        return add(group);
    }

    @Override
    public void updateGroup(Group group) {
        update(group);
    }

    @Override
    public void deleteGroup(Group group) {
        delete(group);
    }

    @Override
    public Group getGroupByName(String name) {
        for (Group group : objects.values()) {
            if(group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    public static void use() throws Exception{
        if(!ServiceLocator.getService(IGroupDataHandler.class).getClass().equals(GroupDataHandlerMock.class)) {
            ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
        }
    }

    public Group[] getAllGroups() {
        return getAll(Group.class);
    }
}
