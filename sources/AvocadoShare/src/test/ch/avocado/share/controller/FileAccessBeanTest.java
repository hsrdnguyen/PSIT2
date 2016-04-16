package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.Impl.MailingService;
import ch.avocado.share.service.Impl.SecurityHandler;
import ch.avocado.share.service.Mock.MailingServiceMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.DataHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import sun.awt.X11.XModifierKeymap;

import java.security.Provider;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class UserArgumentMatcher implements ArgumentMatcher<User> {

    private User user;

    public UserArgumentMatcher(User user) {
        this.user = user;
    }

    @Override
    public boolean matches(Object o) {
        if(!(o instanceof User)) {
            return false;
        }
        User otherUser = (User) o;
        return otherUser.getId().equals(user.getId());
    }
}



class FileArgumentMatcher implements ArgumentMatcher<File> {

    private File file;

    public FileArgumentMatcher(File file) {
        this.file = file;
    }

    @Override
    public boolean matches(Object o) {
        if(!(o instanceof File)) {
            return false;
        }
        File otherFile = (File) o;
        return otherFile.getId().equals(file.getId());
    }
}



/**
 * Created by coffeemakr on 16.04.16.
 */
public class FileAccessBeanTest {

    private User userWithoutReadRights;
    private User userWithReadRights;
    private User owner;
    private File file;
    private Module module;
    private FileAccessBean bean;
    private ISecurityHandler securityHandler;
    private String email0 = "unexisting_user0@zhaw.ch";
    private String email1 = "unexisting_user1@zhaw.ch";
    private String email2 = "unexisting_user2@zhaw.ch";

    private User userFromEmail(String email) {
        return new User(UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "1234.jpg", new EmailAddress(false, email, new EmailAddressVerification(new Date())));
    }

    @Before
    public void setUp() throws Exception {
        IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        securityHandler = ServiceLocator.getService(ISecurityHandler.class);

        bean = new FileAccessBean();
        userWithReadRights = userFromEmail(email0);
        userWithoutReadRights = userFromEmail(email1);
        owner = userFromEmail(email2);

        deleteTestUsers();

        assertNotNull(userDataHandler.addUser(userWithReadRights));
        assertNotNull(userDataHandler.addUser(userWithoutReadRights));
        assertNotNull(userDataHandler.addUser(owner));

        module = new Module(owner.getId(), "description",  "UNEXISTING MODULE!!!!");
        moduleDataHandler.addModule(module);
        file = new File(owner.getId(), "description", "title", "path", new Date(), ".jpg", module.getId());
        fileDataHandler.addFile(file);
        assertNotNull(fileDataHandler.getFile(file.getId()).getOwnerId());

        assertTrue(securityHandler.setAccessLevel(userWithReadRights, file, AccessLevelEnum.READ));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, file));
    }

    private void deleteTestUsers() throws DataHandlerException, ServiceNotFoundException {
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        User user;
        user = userDataHandler.getUserByEmailAddress(email0);
        if(user != null) {
            userDataHandler.deleteUser(user);
        }
        user = userDataHandler.getUserByEmailAddress(email1);
        if(user != null) {
            userDataHandler.deleteUser(user);
        }
        user = userDataHandler.getUserByEmailAddress(email2);
        if(user != null) {
            userDataHandler.deleteUser(user);
        }

    }


    @After
    public void tearDown() throws Exception {
        deleteTestUsers();
        IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
        IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        fileDataHandler.deleteFile(file);
        moduleDataHandler.deleteModule(module);
    }

    @Test
    public void testRequestAccessForUserWithoutAccess() throws Exception {
        MailingServiceMock.use();
        IMailingService mailingService = mock(IMailingService.class);
        ServiceLocatorModifier.setService(IMailingService.class, mailingService);

        UserArgumentMatcher isOwner = new UserArgumentMatcher(owner);
        UserArgumentMatcher isRequester = new UserArgumentMatcher(userWithoutReadRights);
        FileArgumentMatcher isFile = new FileArgumentMatcher(file);

        bean.setFileId(file.getId());
        bean.setRequesterUserMail(userWithoutReadRights.getMail().getAddress());
        assertTrue(bean.requestAccess());

        verify(mailingService, times(1)).sendRequestAccessEmail(argThat(isRequester), argThat(isOwner), argThat(isFile));
    }

    @Test
    public void testRequestAccessForUserWithAccess() throws Exception {
        MailingServiceMock.use();
        IMailingService mailingService = mock(IMailingService.class);
        ServiceLocatorModifier.setService(IMailingService.class, mailingService);

        bean.setFileId(file.getId());
        bean.setRequesterUserMail(userWithoutReadRights.getMail().getAddress());
        assertFalse(bean.requestAccess());

        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
    }

    @Test
    public void testRequestAccessForInvalidUser() throws Exception {
        MailingServiceMock.use();
        IMailingService mailingService = mock(IMailingService.class);
        ServiceLocatorModifier.setService(IMailingService.class, mailingService);

        bean.setFileId(file.getId());
        // We use a inexisting id
        bean.setRequesterUserMail("999999999999999");
        assertFalse(bean.requestAccess());

        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
    }

    @Test
    public void testRequestAccessForInvalidFile() throws Exception {
        MailingServiceMock.use();
        IMailingService mailingService = mock(IMailingService.class);
        ServiceLocatorModifier.setService(IMailingService.class, mailingService);

        // We use a inexisting id
        String notExistingFileId = "999999999999999";
        assertNull(ServiceLocator.getService(IFileDataHandler.class).getFile(notExistingFileId));
        bean.setFileId(notExistingFileId);
        bean.setRequesterUserMail(userWithoutReadRights.getId());
        assertFalse(bean.requestAccess());

        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
    }


    @Test
    public void testGrantAccess() throws Exception {

        bean.setFileId(file.getId());
        bean.setOwnerUserId(owner.getId());
        bean.setRequesterUserId(userWithoutReadRights.getId());
        assertTrue(bean.grantAccess());
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(userWithoutReadRights, file));
    }

    @Test
    public void testGrantAccessWithInvalidOwner() throws Exception {
        bean.setFileId(file.getId());
        bean.setOwnerUserId(userWithReadRights.getId());
        bean.setRequesterUserId(userWithoutReadRights.getId());
        assertFalse(bean.grantAccess());
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, file));
    }

    @Test
    public void testGrantAccessWithInvalidFile() throws Exception {
        bean.setFileId(owner.getId());
        bean.setOwnerUserId(owner.getId());
        bean.setRequesterUserId(userWithoutReadRights.getId());
        assertFalse(bean.grantAccess());
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, file));
        assertEquals(AccessLevelEnum.NONE, securityHandler.getAccessLevel(userWithoutReadRights, owner));
    }

    @Test
    public void testGrantAccessForUserWithRights() throws Exception {
        bean.setFileId(file.getId());
        bean.setOwnerUserId(owner.getId());
        bean.setRequesterUserId(userWithReadRights.getId());
        assertTrue(bean.grantAccess());
        assertEquals(AccessLevelEnum.READ, securityHandler.getAccessLevel(userWithReadRights, file));

        securityHandler.setAccessLevel(userWithReadRights, file, AccessLevelEnum.WRITE);
        assertTrue(bean.grantAccess());
        assertEquals(AccessLevelEnum.WRITE, securityHandler.getAccessLevel(userWithReadRights, file));
    }


}