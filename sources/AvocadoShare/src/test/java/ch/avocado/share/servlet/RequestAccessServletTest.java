package ch.avocado.share.servlet;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.*;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.MailingServiceException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.test.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.ConnectionPoolDataSource;

import static ch.avocado.share.test.Asserts.assertIsRedirected;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link RequestAccessServlet}
 */
public class RequestAccessServletTest {

    private static final String PARAMETER_TYPE = "type";
    private static final String PARAMETER_ID = "id";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User owner;
    private Module module;
    private File file;
    private User requester;
    private Group group;

    private ISecurityHandler securityDataHandler;
    private IModuleDataHandler moduleDataHandler;
    private IUserDataHandler userDataHandler;
    private IFileDataHandler fileDataHandler;
    private IMailingService mailingService;
    private IGroupDataHandler groupDataHandler;
    private RequestAccessServlet servlet;

    private UserArgumentMatcher isOwner;
    private UserArgumentMatcher isRequester;
    private FileArgumentMatcher isFile;
    private GroupArgumentMatcher isGroup;
    private ModuleArgumentMatcher isModule;

    @After
    public void tearDown() throws DataHandlerException, ObjectNotFoundException {
        try {
            groupDataHandler.deleteGroup(group);
            moduleDataHandler.deleteModule(module);
            deleteUser(owner);
            deleteUser(requester);
            if (file != null && file.getId() != null) {
                fileDataHandler.deleteFile(file);
            }
        } finally {
            ServiceLocatorModifier.restore();
        }
    }

    private void deleteUser(User user) throws DataHandlerException, ObjectNotFoundException {
        if (user.getId() == null) {
            String address = user.getMail().getAddress();
            User userToDelete = null;
            try {
                System.out.println("Searching for user: " + address);
                userToDelete = userDataHandler.getUserByEmailAddress(address);
                System.out.println("Deleting user: " + userToDelete);
            } catch (ObjectNotFoundException ignored) {
            }
            if (userToDelete != null) {
                userDataHandler.deleteUser(userToDelete);
            }
        } else {
            userDataHandler.deleteUser(user);
        }
    }

    private void addUser(User user) throws DataHandlerException, ObjectNotFoundException {
        deleteUser(user);
        userDataHandler.addUser(user);
    }

    private void deleteModule(Module module) throws DataHandlerException, ObjectNotFoundException {
        if(module.getId() == null) {
            //module = moduleDataHandler.getModuleByName(module.getName());
        } else {
            moduleDataHandler.deleteModule(module);
        }

    }

    private void addModule(Module module) throws DataHandlerException, ObjectNotFoundException {
        deleteModule(module);
        moduleDataHandler.addModule(module);
    }

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        mailingService = mock(IMailingService.class);
        ServiceLocatorModifier.setService(IMailingService.class, mailingService);


        securityDataHandler = ServiceLocator.getService(ISecurityHandler.class);
        fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
        groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);

        request = new MockHttpServletRequest();

        response = new MockHttpServletResponse();
        owner = DummyFactory.newUser(1);
        requester = DummyFactory.newUser(2);
        addUser(owner);
        addUser(requester);

        group = DummyFactory.newGroup(1, owner);
        groupDataHandler.addGroup(group);

        module = DummyFactory.newModule(1, owner);
        addModule(module);

        file = DummyFactory.newFile(1, owner, module);
        fileDataHandler.addFile(file);

        servlet = new RequestAccessServlet();


        isOwner = new UserArgumentMatcher(owner);
        isRequester = new UserArgumentMatcher(requester);
        isFile = new FileArgumentMatcher(file);
        isGroup = new GroupArgumentMatcher(group);
        isModule = new ModuleArgumentMatcher(module);
    }

    @Test
    public void testPostForUserWithoutAccess() throws Exception {

        request.setMethod("POST");
        request.setParameter(PARAMETER_ID, file.getId());
        request.setParameter(PARAMETER_TYPE, "file");
        new UserSession(request).authenticate(requester);

        servlet.service(request, response);

        assertEquals("Request access failed", 200, response.getStatus());

        verify(mailingService, times(1)).sendRequestAccessEmail(argThat(isRequester), argThat(isOwner), argThat(isFile));
    }


    @Test
    public void testPostForUserWithoutAccessOnGroup() throws Exception {
        request.setMethod("POST");
        request.setParameter(PARAMETER_ID, group.getId());
        request.setParameter(PARAMETER_TYPE, "group");
        new UserSession(request).authenticate(requester);

        servlet.service(request, response);

        assertEquals("Request access failed", 200, response.getStatus());

        verify(mailingService, times(1)).sendRequestAccessEmail(argThat(isRequester), argThat(isOwner), argThat(isGroup));
    }

    @Test
    public void testPostForUserWithoutAccessOnModule() throws Exception {
        request.setMethod("POST");
        request.setParameter(PARAMETER_ID, module.getId());
        request.setParameter(PARAMETER_TYPE, "module");
        new UserSession(request).authenticate(requester);

        servlet.service(request, response);

        assertEquals("Request access failed", 200, response.getStatus());
        verify(mailingService, times(1)).sendRequestAccessEmail(argThat(isRequester), argThat(isOwner), argThat(isModule));
    }

    @Test
    public void testPostFailsWithMissingId() throws Exception {

        request.setMethod("POST");
        request.setParameter(PARAMETER_TYPE, "file");
        new UserSession(request).authenticate(requester);

        servlet.service(request, response);

        assertEquals("Request access failed", HttpStatusCode.BAD_REQUEST.getCode(), response.getStatus());

        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
    }


    @Test
    public void testPostFailsWhenMailingServiceThrowsException() throws Exception {
        doThrow(new MailingServiceException("jiod@fd.com", new RuntimeException())).when(mailingService).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));

        UserArgumentMatcher isOwner = new UserArgumentMatcher(owner);
        UserArgumentMatcher isRequester = new UserArgumentMatcher(requester);
        FileArgumentMatcher isFile = new FileArgumentMatcher(file);

        request.setMethod("POST");
        request.setParameter(PARAMETER_ID, file.getId());
        request.setParameter(PARAMETER_TYPE, "file");
        new UserSession(request).authenticate(requester);

        servlet.service(request, response);

        assertNotEquals("Request succeeded but mailing service failed ", 200, response.getStatus());
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.getStatus());
        verify(mailingService, times(1)).sendRequestAccessEmail(argThat(isRequester), argThat(isOwner), argThat(isFile));
    }


    @Test
    public void testPostForUserWithAccess() throws Exception {
        request.setParameter(PARAMETER_ID, file.getId());
        new UserSession(request).authenticate(requester);

        securityDataHandler.setAccessLevel(requester, file, AccessLevelEnum.READ);

        request.setMethod("POST");
        request.setParameter(PARAMETER_TYPE, "file");
        request.setParameter(PARAMETER_ID, file.getId());
        new UserSession(request).authenticate(requester);

        servlet.service(request, response);

        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
        assertIsRedirected(response);
    }

    @Test
    public void testPostForUnauthenticatedUser() throws Exception {

        request.setMethod("POST");
        request.setParameter(PARAMETER_ID, file.getId());
        request.setParameter(PARAMETER_TYPE, "file");
        servlet.service(request, response);

        assertNotEquals("Request access succeeded but should fail", 200, response.getStatus());
        assertEquals(HttpStatusCode.UNAUTHORIZED.getCode(), response.getStatus());
        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
    }

    @Test
    public void testPostForInvalidFile() throws Exception {

        // We use a inexisting id
        String notExistingFileId = "999999999999999";
        try {
            ServiceLocator.getService(IFileDataHandler.class).getFile(notExistingFileId);
            fail("unexisting file exists");
        } catch (ObjectNotFoundException ignored) {
        }
        request.setMethod("POST");
        request.setParameter(PARAMETER_TYPE, "file");
        request.setParameter(PARAMETER_ID, notExistingFileId);
        new UserSession(request).authenticate(requester);
        servlet.service(request, response);

        assertNotEquals("Request access succeeded but should fail", 200, response.getStatus());
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.getStatus());

        verify(mailingService, never()).sendRequestAccessEmail(any(User.class), any(User.class), any(File.class));
    }

    @Test
    public void testGetWithoutAccess() throws Exception {
        request.setMethod("GET");

        new UserSession(request).authenticate(requester);
        request.setParameter(PARAMETER_TYPE, "file");
        request.setParameter(PARAMETER_ID, file.getId());

        servlet.service(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetUnauthenticated() throws Exception {
        request.setMethod("GET");

        request.setParameter(PARAMETER_TYPE, "file");
        request.setParameter(PARAMETER_ID, file.getId());

        servlet.service(request, response);

        assertEquals(HttpStatusCode.UNAUTHORIZED.getCode(), response.getStatus());
    }


    @Test
    public void testGetWithAlreadyAccessOnFile() throws Exception {
        request.setMethod("GET");
        securityDataHandler.setAccessLevel(requester, file, AccessLevelEnum.WRITE);
        new UserSession(request).authenticate(requester);
        request.setParameter(PARAMETER_TYPE, "file");
        request.setParameter(PARAMETER_ID, file.getId());

        servlet.service(request, response);
        assertIsRedirected(response);
        assertTrue(response.getHeader("location").endsWith("/file?id=" + file.getId()));
    }
}