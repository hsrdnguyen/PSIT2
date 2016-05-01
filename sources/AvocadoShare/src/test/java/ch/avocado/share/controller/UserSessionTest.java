package ch.avocado.share.controller;

import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Impl.UserDataHandler;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for UserSession
 */
public class UserSessionTest {

    private HttpServletRequest request;
    private User user;
    private UserDataHandler userDataHandler;

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        user = DummyFactory.newUser(1);
        userDataHandler = spy(new UserDataHandler());
        ServiceLocatorModifier.setService(IUserDataHandler.class, userDataHandler);
        userDataHandler.addUser(user);
        assertNotNull(user.getId());
        request = new MockHttpServletRequest();
    }

    @After
    public void tearDown() throws Exception {
        userDataHandler.deleteUser(user);
        ServiceLocatorModifier.restore();
    }

    @Test
    public void testAuthenticate() throws Exception {
        UserSession session = new UserSession(request);
        assertFalse(session.isAuthenticated());
        session.authenticate(user);
        assertTrue(session.isAuthenticated());

        session = new UserSession(request);
        assertTrue(session.isAuthenticated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateWithNull() throws Exception {
        UserSession session = new UserSession(request);
        session.authenticate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateWithUserIdNull() throws Exception {
        UserSession session = new UserSession(request);
        User user = DummyFactory.newUser(2);
        assertNull(user.getId());
        session.authenticate(user);
    }


    @Test
    public void testClearAuthentication() throws Exception {
        UserSession session = new UserSession(request);
        session.authenticate(user);
        assertTrue(session.isAuthenticated());

        session.clearAuthentication();
        assertFalse(session.isAuthenticated());

        session = new UserSession(request);
        assertFalse(session.isAuthenticated());

        session.authenticate(user);
        assertTrue(session.isAuthenticated());

        session = new UserSession(request);
        assertTrue(session.isAuthenticated());

        session.clearAuthentication();

        session = new UserSession(request);
        assertFalse(session.isAuthenticated());
    }

    @Test
    public void testGetUser() throws Exception {
        UserSession session = new UserSession(request);
        assertNull(session.getUser());
        session.authenticate(user);
        assertSame(user, session.getUser());
        verify(userDataHandler, never()).getUser(any(String.class));
        verify(userDataHandler, never()).getUserByEmailAddress(any(String.class));


        session = new UserSession(request);
        verify(userDataHandler, never()).getUser(any(String.class));
        verify(userDataHandler, never()).getUserByEmailAddress(any(String.class));
        User sessionUser = session.getUser();
        verify(userDataHandler, times(1)).getUser(user.getId());
        assertNotNull(sessionUser);
        assertEquals(sessionUser.getId(), user.getId());
    }

    @Test
    public void testGetUserId() throws Exception {
        UserSession session = new UserSession(request);
        assertNull(session.getUserId());
        session.authenticate(user);
        assertEquals(user.getId(), session.getUserId());


        session = new UserSession(request);
        assertEquals(user.getId(), session.getUserId());
        verify(userDataHandler, never()).getUser(any(String.class));
        verify(userDataHandler, never()).getUserByEmailAddress(any(String.class));
    }
}