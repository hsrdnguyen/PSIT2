package ch.avocado.share.servlet;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.avocado.share.servlet.LogoutServlet;

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
    
	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		servlet.doPost(request, response);
		assertIsRedirect(response);
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		servlet.doGet(request, response);
		assertIsRedirect(response);
	}

}
