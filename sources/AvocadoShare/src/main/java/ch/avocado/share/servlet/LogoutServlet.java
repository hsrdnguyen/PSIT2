package ch.avocado.share.servlet;

import ch.avocado.share.controller.UserSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/logout", name = "Logout")
public class LogoutServlet extends HttpServlet{

	private static final long serialVersionUID = 422503186682008414L;

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserSession userSession = new UserSession(request);
		userSession.clearAuthentication();
		response.sendRedirect(request.getServletContext().getContextPath());
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}
