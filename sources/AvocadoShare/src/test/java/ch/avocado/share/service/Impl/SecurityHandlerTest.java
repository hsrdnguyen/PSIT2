package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.*;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.MailingServiceMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test security data handler
 */
public class SecurityHandlerTest {

    private SecurityHandler securityHandler;
    private Group groupOne;
    private Group groupTwo;
    private Group groupThree;
    private Module module;
    private File fileInModule;
    private User user;
    private User owningUser;
    private User moduleOwner;

    private User getUser(String prename, String surname, String email) {
        return new User(UserPassword.fromPassword("12345"), prename, surname, "1234.jpg", new EmailAddress(false, email, MailVerification.fromExpiryInHours(24)));
    }

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        MailingServiceMock.use();

        securityHandler = new SecurityHandler();

        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
        IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);

        // Add users
        user = getUser("Prename", "Surname", "user2@zhaw.ch");
        owningUser = getUser("Prename", "Surname", "user1@zhaw.ch");
        moduleOwner = getUser("Module Owner", "Surname", "user3@zhaw.ch");

        assertNotNull(userDataHandler.addUser(user));
        assertNotNull(user.getId());
        assertNotNull(userDataHandler.getUser(user.getId()));

        assertNotNull(userDataHandler.addUser(moduleOwner));
        assertNotNull(moduleOwner.getId());
        assertNotNull(userDataHandler.getUser(moduleOwner.getId()));

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

        // Add module
        module = new Module(moduleOwner.getId(), "Module description", "Unique Module One");

        assertNotNull(moduleDataHandler.addModule(module));

        fileInModule = DummyFactory.newFile(1, owningUser, module);
        assertNotNull(fileDataHandler.addFile(fileInModule));
    }

    @After
    public void tearDown() throws Exception {
        try {
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);

            userDataHandler.deleteUser(user);
            userDataHandler.deleteUser(owningUser);
            userDataHandler.deleteUser(moduleOwner);
            groupDataHandler.deleteGroup(groupOne);
            groupDataHandler.deleteGroup(groupTwo);
            groupDataHandler.deleteGroup(groupThree);

            moduleDataHandler.deleteModule(module);

            fileDataHandler.deleteFile(fileInModule);

        } finally {
            ServiceLocatorModifier.restore();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGetAccessLevelWithUserNull() throws Exception {
        securityHandler.getAccessLevel(null, groupOne);
    }

    @Test(expected = NullPointerException.class)
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

    @Test(expected = NullPointerException.class)
    public void testGetAnonymousAccessLevelWithNullForString() throws DataHandlerException {
        securityHandler.getAnonymousAccessLevel((String) null);
    }

    @Test(expected = NullPointerException.class)
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
    public void testGetAccessLevelWithAccessFromModule() throws Exception {
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, fileInModule));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, module));

        // If the user has READ rights on the module he has READ rights on the file.
        assertTrue(securityHandler.setAccessLevel(user, module, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, fileInModule));
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(user, module));

        // If the user has WRITE rights on the module he has WRITE rights on the file.
        assertTrue(securityHandler.setAccessLevel(user, module, AccessLevelEnum.WRITE));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, fileInModule));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, module));

        // If the user has MANAGE rights on the module he has MANAGE rights on the file.
        assertTrue(securityHandler.setAccessLevel(user, module, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, fileInModule));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, module));

        // Check if the rights can be revoked
        assertTrue(securityHandler.setAccessLevel(user, module, AccessLevelEnum.NONE));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, fileInModule));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(user, module));
    }

    @Test
    public void testModuleOwnerHasManageRightsForFiles() throws Exception {
        // Check if the user is really  the owner
        assertEquals(AccessLevelEnum.OWNER, securityHandler.getAccessLevel(moduleOwner, module));
        assertEquals(AccessLevelEnum.OWNER, securityHandler.getAccessLevel(moduleOwner.getId(), module.getId()));
        // Should have manage rights
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(moduleOwner, fileInModule));
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
    public void testGetObjectOnWhichIdentityHasAccessLevelForOwningUser() throws Exception {
        List<String> ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(owningUser, AccessLevelEnum.READ);

        // The owning user has access on all its groups.
        assertTrue(ids.contains(groupOne.getId()));
        assertTrue(ids.contains(groupTwo.getId()));
        assertTrue(ids.contains(groupThree.getId()));
        assertTrue(ids.contains(fileInModule.getId()));
        assertEquals(4, ids.size());
    }

    @Test
    public void testGetObjectsOnWhichIdentityHasAccessLevelForModuleOwner() throws Exception {
        // The module owner should have access to its module
        List<String> ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(moduleOwner, AccessLevelEnum.READ);
        assertTrue(ids.contains(module.getId()));
        assertEquals(1, ids.size());
    }

    @Test
    public void testGetObjectsOnWhichIdentityHasAccessLevel() throws Exception {
        // the user has no access at all
        List<String> ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);
        assertTrue(ids.isEmpty());

        // now we add rights for the user so the lists should appear
        assertTrue(securityHandler.setAccessLevel(user, groupOne, AccessLevelEnum.READ));
        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.READ));
        assertTrue(securityHandler.setAccessLevel(user, groupThree, AccessLevelEnum.READ));
        assertTrue(securityHandler.setAccessLevel(user, module, AccessLevelEnum.READ));

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);
        assertTrue(ids.contains(groupOne.getId()));
        assertTrue(ids.contains(groupTwo.getId()));
        assertTrue(ids.contains(groupThree.getId()));
        assertTrue(ids.contains(module.getId()));
        assertTrue(ids.contains(fileInModule.getId()));
        assertEquals(5, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.WRITE);
        assertEquals(0, ids.size());
        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.MANAGE);
        assertEquals(0, ids.size());
        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.OWNER);
        assertEquals(0, ids.size());

        assertTrue(securityHandler.setAccessLevel(user, module, AccessLevelEnum.NONE));
        assertTrue(securityHandler.setAccessLevel(user, fileInModule, AccessLevelEnum.READ));

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);

        assertTrue(ids.contains(groupOne.getId()));
        assertTrue(ids.contains(groupTwo.getId()));
        assertTrue(ids.contains(groupThree.getId()));
        assertFalse(ids.contains(module.getId()));
        assertTrue(ids.contains(fileInModule.getId()));
        assertEquals(4, ids.size());


        assertTrue(securityHandler.setAccessLevel(user, groupTwo, AccessLevelEnum.WRITE));
        assertTrue(securityHandler.setAccessLevel(user, groupThree, AccessLevelEnum.MANAGE));
        assertEquals(AccessLevelEnum.MANAGE, securityHandler.getAccessLevel(user, groupThree));
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(user, groupTwo));

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ);
        assertEquals(4, ids.size());
        assertTrue(ids.contains(fileInModule.getId()));
        assertTrue(ids.contains(groupOne.getId()));
        assertTrue(ids.contains(groupTwo.getId()));
        assertTrue(ids.contains(groupThree.getId()));

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.WRITE);
        assertTrue(ids.contains(groupTwo.getId()));
        assertTrue(ids.contains(groupThree.getId()));
        assertEquals(2, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.MANAGE);
        assertTrue(ids.contains(groupThree.getId()));
        assertEquals(1, ids.size());

        ids = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.OWNER);
        assertEquals(0, ids.size());
    }
}