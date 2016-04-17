package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.MailingServiceMock;
import ch.avocado.share.service.exceptions.DataHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test security data handler
 */
public class SecurityHandlerTest {

    private SecurityHandler securityHandler;
    private Group groupOne;
    private Group groupTwo;
    private Group groupThree;

    private User user;
    private User owningUser;

    private User getUser(String prename, String surname, String email) {
        return new User(UserPassword.fromPassword("12345"), prename, surname, "1234.jpg", new EmailAddress(false, email, new EmailAddressVerification(EmailAddressVerification.getDateFromExpiryInHours(24))));
    }

    @Before
    public void setUp() throws Exception {

        MailingServiceMock.use();

        securityHandler = new SecurityHandler();

        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);

        // Add users
        user = getUser("Prename", "Surname", "user2@zhaw.ch");
        owningUser = getUser("Prename", "Surname", "user1@zhaw.ch");

        assertNotNull(userDataHandler.addUser(user));
        assertNotNull(user.getId());
        assertNotNull(userDataHandler.getUser(user.getId()));

        assertNotNull(userDataHandler.addUser(owningUser));
        assertNotNull(owningUser.getId());
        assertNotNull(userDataHandler.getUser(owningUser.getId()));

        // Add groups

        groupOne = new Group(owningUser.getId(), "Group description", "Unique Group One");
        groupTwo = new Group(owningUser.getId(), "Group description", "Unique Group Two");
        groupThree = new Group(owningUser.getId(), "Group Description", "Unique Group Three");

        groupDataHandler.addGroup(groupOne);
        assertNotNull(groupOne.getId());
        assertNotNull(groupDataHandler.getGroup(groupOne.getId()));

        groupDataHandler.addGroup(groupTwo);
        assertNotNull(groupTwo.getId());
        assertNotNull(groupDataHandler.getGroup(groupTwo.getId()));

        groupDataHandler.addGroup(groupThree);
        assertNotNull(groupThree.getId());
        assertNotNull(groupDataHandler.getGroup(groupThree.getId()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccessLevelWithUserNull() throws Exception {
        securityHandler.getAccessLevel(null, groupOne);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccessLevelWithObjectNull() throws Exception {
        securityHandler.getAccessLevel(user, null);
    }

    @Test
    public void testGetAccessLevel() throws Exception {
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, groupOne));
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupOne));
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupOne));
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupOne));
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, groupOne));
    }

    @Test
    public void testGetInheritedAccessLevel() throws Exception {
        // Add user to group one
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupOne));
        // Change rights of groupOne on groupTwo
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, groupTwo));
    }

    @Test
    public void testGetInheritedAccessLevelButUserHasMoreAccess() throws Exception {
        // Add user to groupOne
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupOne));

        // Set user to high rights
        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.MANAGE));

        // Change rights of groupOne on groupTwo
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));


        // Set user to high rights
        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.WRITE));

        // Change rights of groupOne on groupTwo
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));


        // Set user to high rights
        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.READ));

        // Change rights of groupOne on groupTwo
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(groupOne, groupTwo));
        assertTrue(securityHandler.setAccessLevel(groupOne, groupTwo, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupTwo));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupOne, groupTwo));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAnonymousAccessLevelWithNullForString() throws DataHandlerException {
        securityHandler.getAnonymousAccessLevel((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAnonymousAccessLevelWithNullForIdentity() throws DataHandlerException {
        securityHandler.getAnonymousAccessLevel((AccessIdentity) null);
    }

    @Test
    public void testGetAnonymousAccessLevel() throws Exception {
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAnonymousAccessLevel(groupOne));
        assertTrue(securityHandler.setAnonymousAccessLevel(groupOne, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAnonymousAccessLevel(groupOne));
        assertTrue(securityHandler.setAnonymousAccessLevel(groupOne, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAnonymousAccessLevel(groupOne));
        assertTrue(securityHandler.setAnonymousAccessLevel(groupOne, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAnonymousAccessLevel(groupOne));
        assertTrue(securityHandler.setAnonymousAccessLevel(groupOne, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAnonymousAccessLevel(groupOne));
    }

    @Test
    public void testGetAccessLevelWithInheritedAnonymousAccess() throws Exception {
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAnonymousAccessLevel(groupOne));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, groupOne));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(groupTwo, groupOne));
        assertTrue(securityHandler.setAnonymousAccessLevel(groupOne, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupOne));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(groupTwo, groupOne));

        assertTrue(securityHandler.setAccessLevel(groupTwo, groupOne, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupOne));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupTwo, groupOne));

        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupOne));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupTwo, groupOne));
    }


    @Test
    public void testGetGroupsWithAccess() throws Exception {
        assertTrue(securityHandler.setAccessLevel(groupTwo, groupOne, AccessLevelEnum.READ));
        assertTrue(securityHandler.setAccessLevel(groupThree, groupOne, AccessLevelEnum.READ));
        Map<String, AccessLevelEnum> groups = securityHandler.getGroupsWithAccessIncluding(AccessLevelEnum.READ, groupOne);
        assertEquals(2, groups.size());
        assertEquals(AccessLevelEnum.READ, groups.get(groupTwo.getId()));
        assertEquals(AccessLevelEnum.READ, groups.get(groupThree.getId()));
        groups = securityHandler.getGroupsWithAccessIncluding(AccessLevelEnum.NONE, groupOne);
        assertEquals(groups.size(), 2);
        groups = securityHandler.getGroupsWithAccessIncluding(AccessLevelEnum.WRITE, groupOne);
        assertEquals(groups.size(), 0);
        groups = securityHandler.getGroupsWithAccessIncluding(AccessLevelEnum.MANAGE, groupOne);
        assertEquals(groups.size(), 0);
        assertTrue(securityHandler.setAccessLevel(groupThree, groupOne, AccessLevelEnum.MANAGE));
    }

    @Test
    public void testGetUsersWithAccessIncluding() throws Exception {
        Map<String, AccessLevelEnum> users;
        // At first there should be only the owner listed.
        users = securityHandler.getUsersWithAccessIncluding(AccessLevelEnum.OWNER, groupOne);
        assertEquals(1, users.size());
        assertEquals(AccessLevelEnum.OWNER, users.get(owningUser.getId()));

        users = securityHandler.getUsersWithAccessIncluding(AccessLevelEnum.READ, groupOne);
        assertEquals(1, users.size());
        assertEquals(AccessLevelEnum.OWNER, users.get(owningUser.getId()));

        // now we add rights for another user and check if he's listed as well
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.READ));
        users = securityHandler.getUsersWithAccessIncluding(AccessLevelEnum.READ, groupOne);
        assertEquals(2, users.size());
        assertEquals(AccessLevelEnum.OWNER, users.get(owningUser.getId()));
        assertEquals(AccessLevelEnum.READ, users.get(user.getId()));
    }

    @Test
    public void testGetObjectsOnWhichIdentityHasAccessLevel() throws Exception {
        List<String> ids;
        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(owningUser,  AccessLevelEnum.READ);
        // The owning user has access on all its groups.
        assertTrue(ids.contains(groupOne.getId()));
        assertTrue(ids.contains(groupTwo.getId()));
        assertTrue(ids.contains(groupThree.getId()));
        assertEquals(3, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);
        assertTrue(ids.isEmpty());

        // now we add rights for the user so the lists should appear
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.READ));
        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.READ));
        assertTrue(securityHandler.setAccessLevel(user, groupThree, AccessLevelEnum.READ));
        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);
        assertEquals(3, ids.size());
        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.WRITE);
        assertEquals(0, ids.size());

        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.WRITE));
        assertTrue(securityHandler.setAccessLevel(user, groupThree, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupThree));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);
        assertEquals(3, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.WRITE);
        assertEquals(2, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.MANAGE);
        assertEquals(1, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.OWNER);
        assertEquals(0, ids.size());
    }

    @After
    public void tearDown() throws Exception {
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        userDataHandler.deleteUser(user);
        userDataHandler.deleteUser(owningUser);
        groupDataHandler.deleteGroup(groupOne);
        groupDataHandler.deleteGroup(groupTwo);
        groupDataHandler.deleteGroup(groupThree);
    }
}