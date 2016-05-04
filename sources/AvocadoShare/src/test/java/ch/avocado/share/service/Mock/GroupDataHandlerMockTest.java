package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.service.IGroupDataHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for group mock handler
 */
public class GroupDataHandlerMockTest {


    private GroupDataHandlerMock handler;

    @Before
    public void setUp() {
        handler = new GroupDataHandlerMock();
    }

    @Test
    public void testisIGroupDataHandler() {
        @SuppressWarnings("unused")
        IGroupDataHandler groupDataHandler = handler;
    }

    @Test
    public void testGetGroup() throws Exception {
        Group group;
        String groupId;
        groupId = GroupDataHandlerMock.EXISTING_GROUP0;
        group = handler.getGroup(groupId);
        assertNotNull(group);
        assertEquals(group.getId(), groupId);

        groupId = GroupDataHandlerMock.EXISTING_GROUP1;
        group = handler.getGroup(groupId);
        assertNotNull(group);
        assertEquals(group.getId(), groupId);

        groupId = GroupDataHandlerMock.EXISTING_GROUP2;
        group = handler.getGroup(groupId);
        assertNotNull(group);
        assertEquals(group.getId(), groupId);

        groupId = GroupDataHandlerMock.EXISTING_GROUP3;
        group = handler.getGroup(groupId);
        assertNotNull(group);
        assertEquals(group.getId(), groupId);

    }

    @Test
    public void testAddGroup() throws Exception {
        String ownerId = "user0";
        String description = "new_description";
        String name = "new_group_name";
        Group group = new Group(null, new ArrayList<Category>(), new Date(System.currentTimeMillis()), 0.0f, ownerId, description,  name);
        String newId = handler.addGroup(group);
        assertNotNull(newId);
        Group addedGroup = handler.getGroup(newId);
        assertEquals(addedGroup.getId(), newId);
        assertEquals(addedGroup.getName(), name);
        assertEquals(addedGroup.getDescription(), description);
        //assertEquals(addedGroup.getOwnerId(), ownerId);

        // test add group with existing name
        assertNotNull(handler.getGroupByName(GroupDataHandlerMock.EXISTING_GROUP_NAME));
        name = GroupDataHandlerMock.EXISTING_GROUP_NAME;
        group = new Group(null, new ArrayList<Category>(), new Date(System.currentTimeMillis()), 0.0f, ownerId, description,  name);
        newId = handler.addGroup(group);
        assertNull(newId);
    }

    @Test
    public void testUpdateGroup() throws Exception {
        Group group;
        String ownerId = "user0";
        String description = "new_description";
        String name = "new_group_name";
        group = new Group(GroupDataHandlerMock.EXISTING_GROUP0, new ArrayList<Category>(), new Date(System.currentTimeMillis()), 0.0f, ownerId, description,  name);
        handler.updateGroup(group);
        group = handler.getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        assertEquals(group.getId(), GroupDataHandlerMock.EXISTING_GROUP0);
        assertEquals(group.getName(), name);
        assertEquals(group.getDescription(), description);
        //assertEquals(group.getOwnerId(), ownerId);
    }

    @Test
    public void testDeleteGroup() throws Exception {
        Group group = handler.getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        assertNotNull(group);
        handler.deleteGroup(group);
        assertNull(handler.getGroup(GroupDataHandlerMock.EXISTING_GROUP0));
    }

    @Test
    public void testGetGroupByName() throws Exception {
        Group group;
        String ownerId = "user0";
        String description = "new_description";
        String name = "new_group_name";
        group = new Group(null, new ArrayList<Category>(), new Date(System.currentTimeMillis()), 0.0f, ownerId, description,  name);
        int numberOfGroupsBeforeAdd = handler.getNumberOfGroups();
        String id = handler.addGroup(group);
        assertEquals(numberOfGroupsBeforeAdd + 1, handler.getNumberOfGroups());
        assertNotNull(id);
        assertNotNull(handler.getGroup(id));
        assertNotNull(handler.getGroupByName(name));
        assertEquals(handler.getGroupByName(name), handler.getGroup(id));
        assertNotNull(handler.getGroupByName(GroupDataHandlerMock.EXISTING_GROUP_NAME));
        assertNull(handler.getGroupByName(GroupDataHandlerMock.NOT_EXISTING_GROUP_NAME));
    }

    @Test
    public void testGetAllGroups() throws Exception {
        Group[] groups = handler.getAllGroups();
        assertEquals(groups.length, handler.getNumberOfGroups());
        for(Group group: groups) {
            assertNotNull(group);
        }
    }

    @After
    public void restoreServices() throws Exception {
        ServiceLocatorModifier.restore();
    }
}