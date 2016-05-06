package ch.avocado.share.servlet;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IAvatarStorageHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Impl.AvatarFileStorageHandler;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.Mock.UserDataHandlerMock;
import ch.avocado.share.service.exceptions.FileStorageException;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AvatarServlet}
 */
public class AvatarServletTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AvatarServlet servlet;
    private AvatarFileStorageHandler avatarStorageHandler;
    private User user;

    @Before
    public void setUp() throws Exception {
        // mock avatar handler
        avatarStorageHandler = mock(AvatarFileStorageHandler.class);
        ServiceLocatorModifier.setService(IAvatarStorageHandler.class, avatarStorageHandler);

        // add test user
        UserDataHandlerMock.use();
        user = DummyFactory.newUser(1);
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        String avatarId = "123456";
        user.setAvatar(avatarId);
        assertEquals(avatarId, user.getAvatar());
        userDataHandler.addUser(user);
        assertNotNull(user.getId());

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servlet = new AvatarServlet();

    }

    @After
    public void tearDown() throws Exception {
        ServiceLocatorModifier.restore();
    }

    @Test
    public void testDoGetWithoutParameter() throws Exception {
        request.setMethod("GET");
        servlet.doGet(request, response);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testDoGetWithValidParameter() throws Exception {
        request.setMethod("GET");
        request.setParameter(AvatarServlet.PARAMETER_USER_ID, user.getId());
        String contentString = "12382190893401 miupe1 23ei2u1p3 1289 32198p";
        byte[] testData = contentString.getBytes();
        when(avatarStorageHandler.readImages(user.getAvatar())).thenReturn(new ByteArrayInputStream(testData));
        when(avatarStorageHandler.getImageType(user.getAvatar())).thenReturn("image/png");
        servlet.doGet(request, response);
        assertEquals(200, response.getStatus());
        assertEquals(contentString, response.getContentAsString());
        assertEquals("image/png", response.getContentType());
        assertEquals(user.getAvatar(), response.getHeader("Etag"));
    }

    @Test
    public void testDoGetFileStorageHandlerException() throws Exception {
        request.setMethod("GET");
        request.setParameter(AvatarServlet.PARAMETER_USER_ID, user.getId());
        when(avatarStorageHandler.readImages(user.getAvatar())).thenThrow(new FileStorageException("File not found"));
        when(avatarStorageHandler.getImageType(user.getAvatar())).thenReturn("image/png");
        servlet.doGet(request, response);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDoGetWithUnexistingUser() throws Exception {
        request.setMethod("GET");
        request.setParameter(AvatarServlet.PARAMETER_USER_ID, "1234");
        servlet.doGet(request, response);
        assertEquals(404, response.getStatus());
    }
}