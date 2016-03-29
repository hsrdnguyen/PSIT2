package ch.avocado.share.servlet;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import ch.avocado.share.controller.UserSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class LogoutServletTest {

    private LogoutServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        servlet = new LogoutServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    private void assertIsRedirect(HttpServletResponse response) {
		String redirectUrl = response.getHeader("location");
		assertTrue(response.getStatus() == HttpServletResponse.SC_FOUND);
		assertNotNull(redirectUrl);
    }

	private void assertIsLoggedOut(MockHttpServletRequest request) {
		UserSession session = new UserSession(request);
		assertFalse(session.isAuthenticated());
	}

	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		servlet.doPost(request, response);
		assertIsRedirect(response);
		assertIsLoggedOut(request);
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		servlet.doGet(request, response);
		assertIsRedirect(response);
		assertIsLoggedOut(request);
	}
}
