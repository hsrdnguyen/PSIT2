package ch.avocado.share.servlet;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

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

    private void assertIsRedirect(HttpServletResponse response) {
		String redirectUrl = response.getHeader("location");
		assertTrue(response.getStatus() == HttpServletResponse.SC_FOUND);
		assertNotNull(redirectUrl);
    }
    
	@Test
	public void testDoLoginWithInvalidCredentials() throws ServletException, IOException {
		request.setParameter(LoginServlet.FIELD_EMAIL, INVALID_LOGIN_EMAIL);
		request.setParameter(LoginServlet.FIELD_PASSWORD, INVALID_LOGIN_PASSWORD);
		servlet.doPost(request, response);
		String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
		assertNotNull(loginError);
		assertFalse(loginError.isEmpty());
	}

	@Test
	public void testDoLoginWithMissingPasswordCredentials() throws ServletException, IOException {
		request.setParameter(LoginServlet.FIELD_EMAIL, INVALID_LOGIN_EMAIL);
		servlet.doPost(request, response);
		String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
		assertNotNull(loginError);
		assertFalse(loginError.isEmpty());
	}

	@Test
	public void testDoLoginWithMissingEmailCredentials() throws ServletException, IOException {
		request.setParameter(LoginServlet.FIELD_PASSWORD, INVALID_LOGIN_PASSWORD);
		servlet.doPost(request, response);
		String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
		assertNotNull(loginError);
		assertFalse(loginError.isEmpty());
	}


	public void testDoLoginWithValidCredentials() {
		// TODO: Implement
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		servlet.doGet(request, response);
		assertIsRedirect(response);
	}

}
