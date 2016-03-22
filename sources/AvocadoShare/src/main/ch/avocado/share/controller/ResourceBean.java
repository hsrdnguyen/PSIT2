package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.RequestParameterException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class ResourceBean<E extends AccessControlObjectBase> implements Serializable {
    public static final String ERROR_INVALID_REQUEST = "Ungültige Anfrage";
    public static final String ERROR_ACCESS_DENIED = "Sie verfügen über zu wenig Zugriffsrecht für diese Aktion.";

    private E object;

    private Map<String, String> formErrors = new HashMap<>();

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
     * The action parameter
     */
    private String action;

    private User accessingUser;

    private ISecurityHandler securityHandler = null;


    /**
     * This method returns true if the bean is supplied with an identifier
     * to find a unique element. (key or unique attribute)
     * This method is used to determinate if the list of elements is requested (index())
     * or a single object get().
     * @return True if the bean has a valid identifier
     */
    protected abstract boolean hasIdentifier();


    public abstract E create() throws HttpBeanException;

    /**
     * Load and returns a single Object by using the given parameters.
     *
     * @return The object (never null)
     * @throws HttpBeanException
     */
    public abstract E get() throws HttpBeanException;

    /**
     * Returns a list filtered by the given parameters
     *
     * @return A list of objects
     * @throws HttpBeanException
     */
    public abstract E[] index() throws HttpBeanException;

    /**
     * Use addFormError if there are invalid or missing parameters.
     * @return
     * @throws HttpBeanException
     * @throws RequestParameterException
     */
    public abstract void update() throws HttpBeanException;

    public abstract void destroy() throws HttpBeanException;


    void setAccessingUserFromRequest(HttpServletRequest request) {
        UserSession userSession = new UserSession(request);
        User accessingUser = userSession.getUser();
        setAccessingUser(accessingUser);
    }


    public abstract String getAttributeName();

    public String getPluralName() {
        return getAttributeName() + "s";
    }

    private RequestDispatcher getIndexDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher("list.jsp");
    }

    private RequestDispatcher getDetailDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher("details.jsp");
    }

    private RequestDispatcher getEditDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher("edit.jsp");
    }

    private RequestDispatcher getErrorDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher("error.jsp");
    }

    private RequestDispatcher getCreateDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher("create.jsp");
    }

    private void dispatchEvent(HttpServletRequest request, HttpServletResponse response, TemplateType templateType) throws ServletException {
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
            case ERROR:
                dispatcher = getErrorDispatcher(request);
                break;
        }
        if (dispatcher == null) {
            throw new RuntimeException("Template not found: " + templateType);
        }
        try {
            dispatcher.include(request, response);
            // TODO: error handling
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private ISecurityHandler getSecurityHandler() throws HttpBeanException {
        if (securityHandler == null) {
            try {
                securityHandler = ServiceLocator.getService(ISecurityHandler.class);
            } catch (ServiceNotFoundException e) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Security handler not found");
            }
        }
        return securityHandler;
    }

    void ensureHasAccess(E target, AccessLevelEnum requiredLevel) throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        AccessLevelEnum grantedAccessLevel;
        if(accessingUser == null) {
            grantedAccessLevel = securityHandler.getAnonymousAccessLevel(target);
        } else {
            grantedAccessLevel = securityHandler.getAccessLevel(accessingUser, target);
        }
        if (!grantedAccessLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ERROR_ACCESS_DENIED);
        }
    }

    private TemplateType executeRequest(HttpServletRequest request, HttpServletResponse response) throws HttpBeanException {
        TemplateType templateType = TemplateType.ERROR;
        E object;
        E[] objectList;

        switch (request.getMethod()) {
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
                // TODO: replace element
            default:
                throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_INVALID_REQUEST);
        }
        return templateType;
    }

    /**
     * Handle DELETE request
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doDelete(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        object = get();
        ensureHasAccess(object, AccessLevelEnum.OWNER);
        destroy();
        if(!hasErrors()) {
            templateType = TemplateType.INDEX;
        } else {
            templateType = TemplateType.EDIT;
            request.setAttribute(getAttributeName(), object);
        }
        return templateType;
    }

    /**
     * Handle POST request
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doPatch(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        object = get();
        update();
        if (!hasErrors()) {
            // On success show details
            templateType = TemplateType.DETAIL;
        } else {
            // On failure show edit formular again
            templateType = TemplateType.EDIT;
        }
        request.setAttribute(getAttributeName(), object);
        return templateType;
    }

    /**
     * Handle GET request
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doGet(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        E object;
        E[] objectList;
        if (hasIdentifier()) {
            object = get();
            if (isEdit()) {
                templateType = TemplateType.EDIT;
                ensureHasAccess(object, AccessLevelEnum.WRITE);
            } else {
                templateType = TemplateType.DETAIL;
                ensureHasAccess(object, AccessLevelEnum.READ);
            }
            request.setAttribute(getAttributeName(), object);
        } else {
            if(isCreate()) {
                templateType = TemplateType.CREATE;
            } else {
                objectList = index();
                for (E objectInList : objectList) {
                    ensureHasAccess(objectInList, AccessLevelEnum.READ);
                }
                templateType = TemplateType.INDEX;
                request.setAttribute(getPluralName(), objectList);
            }
        }
        return templateType;
    }

    /**
     * Handle PUT request
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        E object = create();
        request.setAttribute(getAttributeName(), object);
        if(hasErrors()) {
            templateType = TemplateType.CREATE;
        } else {
            templateType = TemplateType.DETAIL;
        }
        return templateType;

    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        setAccessingUserFromRequest(request);
        TemplateType templateType;
        try {
            templateType = executeRequest(request, response);
        } catch (HttpBeanException httpExeption) {
            try {
                response.sendError(httpExeption.getStatusCode(), httpExeption.getDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        dispatchEvent(request, response, templateType);
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


    protected void addFormError(String parameter, String message) {
        this.formErrors.put(parameter, message);
    }

    public boolean hasErrors() {
        return formErrors.isEmpty();
    }

    private boolean isEdit() {
        return ACTION_EDIT.equals(action);
    }

    private boolean isCreate() {
        return ACTION_CREATE.equals(action);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    protected E getObject() {
        return object;
    }
}
