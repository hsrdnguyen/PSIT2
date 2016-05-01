package ch.avocado.share.servlet;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.*;

public class LogoutServletTest {

    private LogoutServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
	private User user;

    @Before
    public void setUp() throws Exception {
		DatabaseConnectionHandlerMock.use();
        servlet = new LogoutServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
		user = DummyFactory.newUser(1);
		ServiceLocator.getService(IUserDataHandler.class).addUser(user);
		new UserSession(request).authenticate(user);

    }

	@After
	public void tearDown() throws Exception {
		ServiceLocator.getService(IUserDataHandler.class).deleteUser(user);
		ServiceLocatorModifier.restore();
	}

    private void assertIsRedirect(HttpServletResponse response) {
		String redirectUrl = response.getHeader("location");
		assertTrue(response.getStatus() == HttpServletResponse.SC_FOUND);
		assertNotNull(redirectUrl);
    }

	private void assertIsLoggedOut(MockHttpServletRequest request) {
		UserSession session = new UserSession(request);
		assertFalse("Not logged out", session.isAuthenticated());
	}

	private void assertIsLoggedIn(MockHttpServletRequest request) {
		UserSession session = new UserSession(request);
		assertTrue("Not logged in", session.isAuthenticated());
	}

	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		assertIsLoggedIn(request);
		servlet.doPost(request, response);
		assertIsRedirect(response);
		assertIsLoggedOut(request);
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		assertIsLoggedIn(request);
		servlet.doGet(request, response);
		assertIsRedirect(response);
		assertIsLoggedOut(request);
	}
}
