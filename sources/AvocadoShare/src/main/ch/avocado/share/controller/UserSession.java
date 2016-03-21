package ch.avocado.share.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.util.Date;

public class UserSession {
	
	static final String SESSION_UID = "uid";
	private User user;
	private HttpSession session;
    // TODO: remove when authentication users are implemented;
    private final boolean ALWAYS_AUTHENTICATED = true;

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
        String userId = (String) this.session.getAttribute(SESSION_UID);
        user = null;
        findUser(userId);
        if(ALWAYS_AUTHENTICATED) {
            user = new User("???", null, new Date(System.currentTimeMillis()), null,  "", "???", UserPassword.fromPassword("1234") ,"prename", "surname",  "avatar", new EmailAddress(true, "muellcy1@students.zhaw.ch", null));
        }
    }

    private void findUser(String userId) {
        if(userId == null) return;
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return;
        }
        user = userDataHandler.getUser(userId);
    }


	public boolean isAuthenticated() {
		return getUser() != null;
	}
	
	public void authenticate(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        this.user = user;
		session.setAttribute(SESSION_UID, user.getId());
	}
	
	public void clearAuthentication() {
        user = null;
		session.removeAttribute(SESSION_UID);
	}
	
	/**
	 * @return The user id of the session owner
	 */
	public User getUser() {
        return user;
    }
}
