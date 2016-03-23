package ch.avocado.share.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.EmailAddress;
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
            user = new User("???", null, new Date(System.currentTimeMillis()), 0,  "", "???", UserPassword.fromPassword("1234") ,"prename", "surname",  "avatar", new EmailAddress(true, "muellcy1@students.zhaw.ch", null));
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

    /**
     * Check if the session user has access to the target.
     * @param requiredLevel The required access level
     * @param target The accessed object
     * @return True if the user has the required permissions
     *
    public boolean hasAccess(AccessLevelEnum requiredLevel, AccessControlObjectBase target) {
        User user = getUser();
        AccessLevelEnum grantedLevel;
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            return false;
        }
        if(user != null) {
            grantedLevel = securityHandler.getAccessLevel(user, target);
        } else {
            grantedLevel = securityHandler.getAnonymousAccessLevel(target);
        }
        return grantedLevel.containsLevel(requiredLevel);
    }*/

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
