package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;

import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public abstract class RequestHandlerBeanBase {

    public static final String ERROR_SECURITY_HANDLER = "Security handler not found";
    protected User accessingUser;
    private String method;
    private ISecurityHandler securityHandler = null;
    protected final String ERROR_METHOD_NOT_ALLOWED = "Methode nicht erlaubt.";
    protected static final String ERROR_INVALID_REQUEST = "Ungültige Anfrage";
    protected static final String ERROR_ACCESS_DENIED = "Sie verfügen über zu wenig Zugriffsrecht für diese Aktion.";
    protected static final String ERROR_NOT_LOGGED_IN = "Sie müssen angemeldet sein für diese Aktion.";


    private void methodNotAllowed() throws HttpBeanException {
        throw new HttpBeanException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ERROR_METHOD_NOT_ALLOWED);
    }

    protected TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        methodNotAllowed();
        return TemplateType.ERROR;
    }

    protected TemplateType doGet(HttpServletRequest request) throws HttpBeanException {
        methodNotAllowed();
        return TemplateType.ERROR;
    }

    protected TemplateType doPut(HttpServletRequest request) throws HttpBeanException {
        methodNotAllowed();
        return TemplateType.ERROR;
    }

    protected TemplateType doPatch(HttpServletRequest request) throws HttpBeanException {
        methodNotAllowed();
        return TemplateType.ERROR;
    }

    protected TemplateType doDelete(HttpServletRequest request) throws HttpBeanException {
        methodNotAllowed();
        return TemplateType.ERROR;
    }


    void setAccessingUserFromRequest(HttpServletRequest request) {
        UserSession userSession = new UserSession(request);
        User accessingUser = userSession.getUser();
        setAccessingUser(accessingUser);
    }


    private TemplateType callHttpMethodMethod(HttpServletRequest request) throws HttpBeanException {
        if (request.getMethod().equals("GET")) {
            // Another request can't be simulated with GET.
            setMethod("GET");
        } else if (getMethod() == null) {
            setMethod(request.getMethod());
        }
        TemplateType templateType;
        switch (getMethod()) {
            case "PATCH":
                templateType = doPatch(request);
                break;
            case "POST":
                templateType = doPost(request);
                break;
            case "GET":
                templateType = doGet(request);
                break;
            case "DELETE":
                templateType = doDelete(request);
                break;
            case "PUT":
                templateType = doPut(request);
            default:
                throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ResourceBean.ERROR_INVALID_REQUEST);
        }
        return templateType;
    }


    public TemplateType executeRequest(HttpServletRequest request, HttpServletResponse response) {
        setAccessingUserFromRequest(request);
        try {
            return callHttpMethodMethod(request);
        } catch (HttpBeanException httpException) {
            try {
                response.sendError(httpException.getStatusCode(), httpException.getDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @return The security handler to use.
     * @throws HttpBeanException If there is an error while getting the security handler this excpetion is thrown.
     */
    protected ISecurityHandler getSecurityHandler() throws HttpBeanException {
        if (securityHandler == null) {
            try {
                securityHandler = ServiceLocator.getService(ISecurityHandler.class);
            } catch (ServiceNotFoundException e) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_SECURITY_HANDLER);
            }
        }
        return securityHandler;
    }

    /**
     * Ensures that the {@link #getAccessingUser() accessing user} is authenticated.
     *
     * @throws HttpBeanException thrown if the user is not authenticated.
     */
    protected void ensureIsAuthenticated() throws HttpBeanException {
        if (getAccessingUser() == null) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ResourceBean.ERROR_NOT_LOGGED_IN);
        }
    }

    /**
     * Checks if the {@link #getAccessingUser() accessing user} has the required level of access.
     *
     * @param target        The access target
     * @param requiredLevel The required level of access on the target.
     * @throws HttpBeanException If the required access is not met.
     */
    protected void ensureHasAccess(AccessControlObjectBase target, AccessLevelEnum requiredLevel) throws HttpBeanException {
        if (target == null) throw new IllegalArgumentException("target is null");
        if (requiredLevel == null) throw new IllegalArgumentException("requiredLevel is null");
        ISecurityHandler securityHandler = getSecurityHandler();
        AccessLevelEnum grantedAccessLevel;
        if (getAccessingUser() == null) {
            grantedAccessLevel = securityHandler.getAnonymousAccessLevel(target);
        } else {
            grantedAccessLevel = securityHandler.getAccessLevel(getAccessingUser(), target);
        }
        if (!grantedAccessLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ResourceBean.ERROR_ACCESS_DENIED);
        }
    }

    /**
     * Sets the the user which accesses the object.
     *
     * @param accessingUser the user or null if the user is not authenticated.
     */
    public void setAccessingUser(User accessingUser) {
        this.accessingUser = accessingUser;
    }

    /**
     * Get the accessing user.
     *
     * @return Returns the accessing user object if the user is authenticated.
     * Otherwise null is returned.
     */
    public User getAccessingUser() {
        return accessingUser;
    }

    /**
     * Returns the HTTP method which will be used to process the request
     *
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the HTTP method.
     *
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }
}
