package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.*;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;


/**
 * Created by coffeemakr on 16.04.16.
 */
public class FileObjectAccessBeanTest {

    private User userWithoutReadRights;
    private User userWithReadRights;
    private User owner;
    private File file;
    private Module module;
    private ObjectAccessBean bean;
    private ISecurityHandler securityHandler;
    private String email0 = "unexisting_user0@zhaw.ch";
    private String email1 = "unexisting_user1@zhaw.ch";
    private String email2 = "unexisting_user2@zhaw.ch";

    private User userFromEmail(String email) {
        final Date expiry = new Date();
        return new User(UserPassword.fromPassword(""), "Prename", "Surname", "1234.jpg", new EmailAddress(false, email, new MailVerification(expiry)));
    }

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        IRatingDataHandler ratingDataHandler = ServiceLocator.getService(IRatingDataHandler.class);
        securityHandler = ServiceLocator.getService(ISecurityHandler.class);

        bean = new ObjectAccessBean();
        userWithReadRights = userFromEmail(email0);
        userWithoutReadRights = userFromEmail(email1);
        owner = userFromEmail(email2);

        deleteTestUsers();

        assertNotNull(userDataHandler.addUser(userWithReadRights));
        assertNotNull(userDataHandler.addUser(userWithoutReadRights));
        assertNotNull(userDataHandler.addUser(owner));

        module = new Module(owner.getId(), "description", "UNEXISTING MODULE!!!!");
        moduleDataHandler.addModule(module);
        file = DummyFactory.newFile(1, owner, module);
        fileDataHandler.addFile(file);
        ratingDataHandler.addRating(Long.parseLong(file.getId()), Long.parseLong(owner.getId()), 2);
        assertNotNull(fileDataHandler.getFile(file.getId()).getOwnerId());

        assertTrue(securityHandler.setAccessLevel(userWithReadRights, file, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, file));
    }

    private void deleteTestUsers() throws DataHandlerException, ServiceNotFoundException {
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        User user;
        try {
            user = userDataHandler.getUserByEmailAddress(email0);
            userDataHandler.deleteUser(user);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        }
        try {
            user = userDataHandler.getUserByEmailAddress(email1);
            userDataHandler.deleteUser(user);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        }

        try {
            user = userDataHandler.getUserByEmailAddress(email2);
            userDataHandler.deleteUser(user);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        }

    }


    @After
    public void tearDown() throws Exception {
        deleteTestUsers();

        try {
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            fileDataHandler.deleteFile(file);
        }catch (ServiceException e) {
            e.printStackTrace();
        }

        try {
            IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
            moduleDataHandler.deleteModule(module);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @After
    public void restoreServices() throws Exception {
        ServiceLocatorModifier.restore();
    }


    @Test
    public void testGrantAccess() throws Exception {

        bean.setFileId(file.getId());
        bean.setObjectOwner(owner);
        bean.setRequesterUserId(userWithoutReadRights.getId());
        assertTrue(bean.grantAccess());
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(userWithoutReadRights, file));
    }

    @Test
    public void testGrantAccessWithInvalidOwner() throws Exception {
        bean.setFileId(file.getId());
        bean.setObjectOwner(userWithReadRights);
        bean.setRequesterUserId(userWithoutReadRights.getId());
        assertFalse(bean.grantAccess());
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, file));
    }

    @Test
    public void testGrantAccessWithInvalidFile() throws Exception {
        assertNotNull(owner.getId());
        bean.setFileId(owner.getId());
        bean.setObjectOwner(owner);
        bean.setRequesterUserId(userWithoutReadRights.getId());
        assertFalse(bean.grantAccess());
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, file));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, owner));
    }

    @Test
    public void testGrantAccessForUserWithRights() throws Exception {
        bean.setFileId(file.getId());
        bean.setObjectOwner(owner);
        bean.setRequesterUserId(userWithReadRights.getId());
        assertTrue(bean.grantAccess());
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(userWithReadRights, file));

        securityHandler.setAccessLevel(userWithReadRights, file, AccessLevelEnum.WRITE);
        assertTrue(bean.grantAccess());
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(userWithReadRights, file));
    }

}