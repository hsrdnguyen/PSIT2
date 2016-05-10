package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserSession {

	private static final String SESSION_UID = "uid";
	private String userId = null;
	private User user;
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
		if(session == null) throw new NullPointerException("session can't be null");
		this.session = session;
        userId = (String) this.session.getAttribute(SESSION_UID);
        user = null;
    }

    private void loadUser(String userId) {
		if(userId == null) return;
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
			user = userDataHandler.getUser(userId);
		} catch (ObjectNotFoundException | ServiceNotFoundException | DataHandlerException ignored) {
			ignored.printStackTrace();
        }
	}

	/**
	 * @return {@code True} is the user is authenticated
     */
	public boolean isAuthenticated() {
		if (getUser() == null) {
			return false;
		}
		return true;
	}

	/**
	 * Authenticate the session to the user.
	 * @param user The user.
     */
	public void authenticate(User user) {
        if(user == null) throw new NullPointerException("user is null");
        if(user.getId() == null) throw new IllegalArgumentException("user's id is null");
		this.user = user;
		this.userId = user.getId();
		session.setAttribute(SESSION_UID, user.getId());
	}

	public void clearAuthentication() {
        user = null;
		userId = null;
		session.removeAttribute(SESSION_UID);
	}
	
	/**
	 * @return The user id of the session owner
	 */
	public User getUser() {
        if(userId == null) {
			return null;
		}
		if(user == null) {
			loadUser(userId);
		}
		return user;
	}

	public String getUserId() {
		return userId;
	}
}
