package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.MailingServiceMock;
import ch.avocado.share.service.UserDataHandlerTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test security data handler
 */
public class SecurityHandlerTest {

    private SecurityHandler securityHandler;
    private Group groupOne;
    private Group groupTwo;
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
        user = getUser("Prename", "Surname", "user2@zhaw.ch");
        owningUser = getUser("Prename", "Surname", "user1@zhaw.ch");


        groupOne = new Group(owningUser.getId(), "Group description", "Unique Group One");
        groupTwo = new Group(owningUser.getId(), "Group description", "Unique Group Two");

        groupDataHandler.addGroup(groupOne);
        assertNotNull(groupOne.getId());
        assertNotNull(groupDataHandler.getGroup(groupOne.getId()));

        groupDataHandler.addGroup(groupTwo);
        assertNotNull(groupTwo.getId());
        assertNotNull(groupDataHandler.getGroup(groupTwo.getId()));

        assertNotNull(userDataHandler.addUser(user));
        assertNotNull(user.getId());
        assertNotNull(userDataHandler.getUser(user.getId()));

        assertNotNull(userDataHandler.addUser(owningUser));
        assertNotNull(owningUser.getId());
        assertNotNull(userDataHandler.getUser(owningUser.getId()));

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

        securityHandler.setAccessLevel(groupTwo, groupOne, AccessLevelEnum.WRITE);
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, groupOne));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupTwo, groupOne));

        securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.READ);
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupOne));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(groupTwo, groupOne));
    }


    @Ignore
    @Test
    public void testGetGroupsWithAccess() throws Exception {

    }

    @Ignore
    @Test
    public void testGetUsersWithAccessIncluding() throws Exception {

    }

    @Ignore
    @Test
    public void testGetObjectsOnWhichIdentityHasAccessLevel() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        userDataHandler.deleteUser(user);
        userDataHandler.deleteUser(owningUser);
        groupDataHandler.deleteGroup(groupOne);
        groupDataHandler.deleteGroup(groupTwo);

    }
}