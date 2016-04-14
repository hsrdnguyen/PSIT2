package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;


public abstract class RequestHandlerBeanBase implements Serializable {
    /**
     * When the parameter action is set to this value
     * and a single element is requested by GET request
     * we use the edit dispatcher provided by getEditDispatcher().
     */
    public static final String ACTION_EDIT = "edit";
    /**
     * If the parameter action is set to this value
     * and the index is requested by a GET request we use
     * getCreateDispatcher().
     */
    public static final String ACTION_CREATE = "create";
    /**
     * The default template returned by {@link #getIndexDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_LIST = "list.jsp";
    /**
     * The default template returned by {@link #getDetailDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_DETAILS = "view.jsp";
    /**
     * The default template returned by {@link #getEditDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_EDIT = "edit.jsp";
    /**
     * The default template returned by {@link #getCreateDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_CREATE = "create.jsp";
    private static final String TEMPLATES_FOLDER = "templates/";
    protected User accessingUser;
    private String method;
    private ISecurityHandler securityHandler = null;


    private void throwMethodNotAllowed(String method) throws HttpBeanException {
        if (method == null) throw new IllegalArgumentException("method is null");
        throw new HttpBeanException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ErrorMessageConstants.METHOD_NOT_ALLOWED + method);
    }

    protected TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        throwMethodNotAllowed("POST");
        return null;
    }

    protected TemplateType doGet(HttpServletRequest request) throws HttpBeanException {
        throwMethodNotAllowed("GET");
        return null;
    }

    public TemplateType doPut(HttpServletRequest request) throws HttpBeanException {
        throwMethodNotAllowed("PUT");
        return null;
    }

    protected TemplateType doPatch(HttpServletRequest request) throws HttpBeanException {
        throwMethodNotAllowed("PATCH");
        return null;
    }

    protected TemplateType doDelete(HttpServletRequest request) throws HttpBeanException {
        throwMethodNotAllowed("DELETE");
        return null;
    }

    protected void ensureIsAuthenticatedToCreate() throws HttpBeanException {
        ensureIsAuthenticated();
    }


    /**
     * Get the dispatcher to render a list of objects.
     *
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_LIST} in the same folder.
     */
    protected RequestDispatcher getIndexDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(getTemplateFolder() + TEMPLATE_LIST);
    }

    /**
     * Get the dispatcher to render a single object.
     *
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_DETAILS} in the same folder.
     */
    protected RequestDispatcher getDetailDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(getTemplateFolder() + TEMPLATE_DETAILS);
    }

    /**
     * Get the dispatcher to edit a single object.
     *
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_EDIT} in the same folder.
     */
    protected RequestDispatcher getEditDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(getTemplateFolder() + TEMPLATE_EDIT);
    }


    /**
     * Get the dispatcher to render a create a new  object.
     *
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_CREATE} in the same folder.
     */
    protected RequestDispatcher getCreateDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(getTemplateFolder() + TEMPLATE_CREATE);
    }

    /**
     * @return The template folder.
     */
    protected String getTemplateFolder() {
        return TEMPLATES_FOLDER;
    }


    public void dispatchEvent(HttpServletRequest request, HttpServletResponse response, TemplateType templateType) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (response == null) throw new IllegalArgumentException("response is null");
        if (templateType == null) throw new IllegalArgumentException("templateType is null");
        RequestDispatcher dispatcher = null;
        switch (templateType) {
            case INDEX:
                dispatcher = getIndexDispatcher(request);
                break;
            case DETAIL:
                dispatcher = getDetailDispatcher(request);
                break;
            case CREATE:
                dispatcher = getCreateDispatcher(request);
                break;
            case EDIT:
                dispatcher = getEditDispatcher(request);
                break;
            default:
                throw new RuntimeException("Template type not found: " + templateType);
        }
        if (dispatcher != null) {
            try {
                response.flushBuffer();
                try {
                    dispatcher.include(request, response);
                } catch (ServletException e) {
                    e.printStackTrace();
                    throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Es besteht ein Fehler in Template.");
                }
                response.flushBuffer();
                // TODO: error handling
            } catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }
    }


    public void setAccessingUserFromRequest(HttpServletRequest request) {
        UserSession userSession = new UserSession(request);
        User accessingUser = userSession.getUser();
        setAccessingUser(accessingUser);
    }


    protected void sendErrorFromHttpBeanException(HttpBeanException httpException, HttpServletResponse response) {
        try {
            response.sendError(httpException.getStatusCode(), httpException.getDescription());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Mapper for {@link ServiceLocator#getService(Class)} which raises a  HttpBeanException instead.
     * @param serviceClass
     * @param <E>
     * @return The service
     * @throws HttpBeanException
     */
    protected static <E> E getService(Class<E> serviceClass) throws HttpBeanException {
        try {
            return ServiceLocator.getService(serviceClass);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService());
        }
    }

    /**
     * @return The security handler to use.
     * @throws HttpBeanException If there is an error while getting the security handler this excpetion is thrown.
     */
    protected ISecurityHandler getSecurityHandler() throws HttpBeanException {
        if (securityHandler == null) {
            securityHandler = getService(ISecurityHandler.class);
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
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ErrorMessageConstants.NOT_LOGGED_IN);
        }
    }

    /**
     * Checks if the {@link #getAccessingUser() accessing user} has the required level of access.
     *
     * @param target        The access target
     * @param requiredLevel The required level of access on the target.
     * @throws HttpBeanException If the required access is not met.
     */
    protected void ensureAccessingUserHasAccess(AccessControlObjectBase target, AccessLevelEnum requiredLevel) throws HttpBeanException {
        if (target == null) throw new IllegalArgumentException("target is null");
        if (requiredLevel == null) throw new IllegalArgumentException("requiredLevel is null");
        ISecurityHandler securityHandler = getSecurityHandler();
        AccessLevelEnum grantedAccessLevel;
        // TODO: remove
        System.out.println("Accessing user : " + getAccessingUser());
        try {
            if (getAccessingUser() == null) {
                grantedAccessLevel = securityHandler.getAnonymousAccessLevel(target);
            } else {
                grantedAccessLevel = securityHandler.getAccessLevel(getAccessingUser(), target);
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
        }
        // TODO: remove
        System.out.println("Level: " + grantedAccessLevel);
        if (!grantedAccessLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ErrorMessageConstants.ACCESS_DENIED);
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
}
