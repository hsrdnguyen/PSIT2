package ch.avocado.share.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.avocado.share.model.data.User;

public class UserSession {
	
	static final String SESSION_UID = "uid";
	
	private HttpSession session;

	/**
	 * Helper method to check if the request is null.
	 * It throws an {@link IllegalArgumentException} if the request is null.
	 * @param request The request to check
	 * @return The same request
	 */
	private static HttpServletRequest checkRequestNotNull(HttpServletRequest request) {
		if(request == null) {
			throw new IllegalArgumentException("request can't be null");
		}
		return request;
	}
	
	/**
	 * Create UserSession from request. If the request
	 * has no associated session it will create one.
	 * @param request The HTTP request.
	 */
	public UserSession(HttpServletRequest request) {
		this(checkRequestNotNull(request).getSession(true));
	}
	
	/**
	 * Create UserSession from an existing session.
	 * @param session
	 */
	private UserSession(HttpSession session) {
		if(session == null) {
			throw new IllegalArgumentException("session can't be null");
		}
		this.session = session;
	}


	public boolean isAuthenticated() {
		if(getUserId() != null) {
			return true;
		}
		return false;
	}
	
	public void authenticate(User user) {
		session.setAttribute(SESSION_UID, user.getId());
	}
	
	public void clearAuthentication() {
		session.removeAttribute(SESSION_UID);
	}
	
	/**
	 * @return The user id of the session owner
	 */
	private String getUserId() {
		return (String) this.session.getAttribute(SESSION_UID);
	}
}
