package ch.avocado.share.model.data;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.GroupDataHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.Mock.UserDataHandlerMock;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for Members
 */
public class MembersTest {

    private ArrayList<User> users;
    private IUserDataHandler userDataHandler;
    private IGroupDataHandler groupDataHandler;
    private ArrayList<Group> groups;
    private Map<String, AccessLevelEnum> groupsIdsWithAccess;
    private Map<String, AccessLevelEnum> usersIdsWithAccess;
    private Map<Group, AccessLevelEnum> groupsWithAccess;
    private Map<User, AccessLevelEnum> usersWithAccess;
    private AccessControlObjectBase target;

    @Before
    public void setUp() throws Exception {
        GroupDataHandlerMock.use();
        UserDataHandlerMock.use();
        userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        users = new ArrayList<>();
        groups = new ArrayList<>();
        groupsIdsWithAccess = new HashMap<>();
        usersIdsWithAccess = new HashMap<>();
        groupsWithAccess = new HashMap<>();
        usersWithAccess = new HashMap<>();
        User user = null;
        for (int i = 0; i < 10; i++) {
            user = DummyFactory.newUser(i);
            userDataHandler.addUser(user);
            assertNotNull(user.getId());
            users.add(user);
            usersIdsWithAccess.put(user.getId(), AccessLevelEnum.READ);
            usersWithAccess.put(user, AccessLevelEnum.READ);
        }
        Group group;
        for (int i = 0; i < 10; i++) {
            group = DummyFactory.newGroup(i, user);
            assertNotNull(groupDataHandler.addGroup(group));
            assertNotNull(group.getId());
            groups.add(group);
            groupsIdsWithAccess.put(group.getId(), AccessLevelEnum.READ);
            groupsWithAccess.put(group, AccessLevelEnum.READ);
        }

        target = groups.get(0);


    }

    @After
    public void tearDown() throws Exception {
        ServiceLocatorModifier.restore();
    }

    private static void assertObjectListEquals(Iterable<? extends AccessControlObjectBase> expected, Iterable<? extends AccessControlObjectBase> actual) {
        Set<String> expectedIds = new HashSet<>();
        Set<String> actualIds = new HashSet<>();
        for (AccessControlObjectBase user : expected) {
            expectedIds.add(user.getId());
        }
        for (AccessControlObjectBase user : actual) {
            actualIds.add(user.getId());
        }
        assertEquals(expectedIds, actualIds);
    }

    private static void assertObjectsWithAccessEquals(Map<? extends AccessControlObjectBase, AccessLevelEnum> expected,
                                                      Map<? extends AccessControlObjectBase, AccessLevelEnum> actual) {
        HashMap<String, AccessLevelEnum> expectedMap = new HashMap<>();
        HashMap<String, AccessLevelEnum> actualMap = new HashMap<>();

        for(Map.Entry<? extends AccessControlObjectBase, AccessLevelEnum> entry: expected.entrySet() ) {
            expectedMap.put(entry.getKey().getId(), entry.getValue());
        }

        for(Map.Entry<? extends AccessControlObjectBase, AccessLevelEnum> entry: actual.entrySet() ) {
            actualMap.put(entry.getKey().getId(), entry.getValue());
        }

        for(Map.Entry<String, AccessLevelEnum> expectedEntry: expectedMap.entrySet()) {
            String id = expectedEntry.getKey();
            AccessLevelEnum access = expectedEntry.getValue();
            assertTrue("id not in actual entries: " + id, actualMap.containsKey(id));
            assertEquals(access, actualMap.get(id));
            actualMap.remove(id);
        }
        assertTrue(actualMap.isEmpty());
    }

    @Test
    public void testFromIdsWithRights() throws Exception {
        Members members = Members.fromIdsWithRights(usersIdsWithAccess, groupsIdsWithAccess, target);
        assertEquals(target.getId(), members.getTarget().getId());
        assertEquals(target.getReadableName(), members.getTarget().getReadableName());
        assertObjectListEquals(users, members.getUsers());
        assertObjectListEquals(groups, members.getGroups());

    }

    @Test(expected = NullPointerException.class)
    public void testFromIdsWithNullAsUserIds() throws ServiceNotFoundException, DataHandlerException {
        Members.fromIdsWithRights(null, groupsIdsWithAccess, target);
    }

    @Test(expected = NullPointerException.class)
    public void testFromIdsWithNullAsGroupIds() throws ServiceNotFoundException, DataHandlerException {
        Members.fromIdsWithRights(usersIdsWithAccess, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void testFromIdsWithNullAsTarget() throws ServiceNotFoundException, DataHandlerException {
        Members.fromIdsWithRights(usersIdsWithAccess, groupsIdsWithAccess, null);
    }


    @Test
    public void testGetUsers() {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);
        assertObjectListEquals(users, members.getUsers());
    }

    @Test
    public void testGetGroups() {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);
        assertObjectListEquals(groups, members.getGroups());

    }


    @Test
    public void testGetTarget() throws Exception {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);
        assertEquals(target.getId(), members.getTarget().getId());
    }

    @Test
    public void testGetIdentitiesWithAccess() throws Exception {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);

        HashMap<AccessControlObjectBase, AccessLevelEnum> identitiesWithAccess = new HashMap<>();
        identitiesWithAccess.putAll(usersWithAccess);
        identitiesWithAccess.putAll(groupsWithAccess);
        assertObjectsWithAccessEquals(identitiesWithAccess, members.getIdentitiesWithAccess());
    }

    @Test
    public void testGetIdentities() throws Exception {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);

        ArrayList<AccessControlObjectBase> objects = new ArrayList<>(groups.size() + users.size());
        objects.addAll(users);
        objects.addAll(groups);
        assertObjectListEquals(objects, members.getIdentities());
    }

    @Test
    public void testGetUsersWithAccess() throws Exception {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);
        assertObjectsWithAccessEquals(usersWithAccess, members.getUsersWithAccess());
    }

    @Test
    public void testGetGroupsWithAccess() throws Exception {
        Members members = new Members(usersWithAccess, groupsWithAccess, target);
        assertObjectsWithAccessEquals(groupsWithAccess, members.getGroupsWithAccess());

    }
}