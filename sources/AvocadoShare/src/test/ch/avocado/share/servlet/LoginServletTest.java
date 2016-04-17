package ch.avocado.share.servlet;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.SecurityHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.Mock.UserDataHandlerMock;
import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.avocado.share.servlet.LoginServlet;
import org.springframework.mock.web.MockServletContext;

public class LoginServletTest {

    private LoginServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @SuppressWarnings("unused")
	private static final String VALID_LOGIN_EMAIL = null;
    @SuppressWarnings("unused")
	private static final String VALID_LOGIN_PASSWORD = null;

    private static final String INVALID_LOGIN_EMAIL = "nobody@zhaw.ch";
    private static final String INVALID_LOGIN_PASSWORD = "notexisting";


    @Before
    public void setUp() {
        servlet = new LoginServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
	}

	/**
	 * Test that the
	 * @throws ServletException
	 * @throws IOException
     */
	@Test
	public void testDoLoginWithInvalidCredentials() throws ServletException, IOException {
		request.setParameter(LoginServlet.FIELD_EMAIL, INVALID_LOGIN_EMAIL);
		request.setParameter(LoginServlet.FIELD_PASSWORD, INVALID_LOGIN_PASSWORD);
		servlet.doPost(request, response);
		String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
		assertNotNull(loginError);
		assertFalse(loginError.isEmpty());
        UserSession userSession = new UserSession(request);
        assertFalse(userSession.isAuthenticated());
	}

	@Test
	public void testDoLoginWithMissingPasswordCredentials() throws ServletException, IOException {
		request.setParameter(LoginServlet.FIELD_EMAIL, INVALID_LOGIN_EMAIL);
		servlet.doPost(request, response);
		String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
		assertNotNull(loginError);
		assertFalse(loginError.isEmpty());
        UserSession userSession = new UserSession(request);
        assertFalse(userSession.isAuthenticated());
	}

	@Test
	public void testDoLoginWithMissingEmailCredentials() throws ServletException, IOException {
		request.setParameter(LoginServlet.FIELD_PASSWORD, INVALID_LOGIN_PASSWORD);
		servlet.doPost(request, response);
		String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
		assertNotNull(loginError);
		assertFalse(loginError.isEmpty());
        UserSession userSession = new UserSession(request);
        assertFalse(userSession.isAuthenticated());
	}

    @Test
	public void testDoLoginWithValidCredentials() throws Exception {
        UserDataHandlerMock.use();
        SecurityHandlerMock.use();
        String password = "1234";
        User user = ServiceLocator.getService(IUserDataHandler.class).getUser(UserDataHandlerMock.EXISTING_USER0);
		assertTrue(user.getPassword().matchesPassword(password));
        request.setParameter(LoginServlet.FIELD_PASSWORD, password);
        request.setParameter(LoginServlet.FIELD_EMAIL, user.getMail().getAddress());
        servlet.doPost(request, response);
        String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
        //assertNull(loginError);
        UserSession userSession = new UserSession(request);
        assertTrue(userSession.isAuthenticated());
	}

	@After
	public void restoreServices() throws Exception {
		ServiceLocatorModifier.restore();
	}
}
