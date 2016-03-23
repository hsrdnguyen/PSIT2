package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanException;
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

/**
 * This class should be used for all resources and does the following things:
 * * Check access
 * * Execute appropriate method (get, create, index, etc.)
 * * Include the template accordingly.
 * <p>
 * Once handleRequest is called, this class checks the method of the request.<br/>
 * * GET: Display a form to edit or create an object, display a single element or display a list of elements.<br/>
 * * POST: create a new object (if there is no method parameter in the request)<br/>
 * * PATCH: update the object<br/>
 * * PUT: Replace an element (not implemented yet)
 * <p>
 * The GET request is somewhat special. There are four different cases for a GET-request. Checking is performed
 * in this order:
 * <ul>
 * <li>If {@link #hasIdentifier()} is {@code true}:</li>
 * <ul>
 * <li>  If the attribute {@link #getAction() action} is set to {@value ACTION_EDIT} {@link #get()}
 *       is called an the template {@link #getEditDispatcher(HttpServletRequest)} is included.
 * </li>
 * <li>  Otherwise {@link #get()} is called and  {@link #getDetailDispatcher(HttpServletRequest)} is included</li>
 * </ul>
 * <li>
 *    If {@link #hasIdentifier()} is {@code false}:
 * </li><ul><li>
 *       If the attribute {@link #getAction() action} is set to {@value ACTION_CREATE}
 *       the template {@link #getCreateDispatcher(HttpServletRequest)} to create a new
 *       object is included.
 * </li>
 * <li>
 *       Otherwise {@link #index()} is called and the template
 *        {@link #getIndexDispatcher(HttpServletRequest)} is included
 * </li></ul></ul>
 *
 * @param <E> The subclass of AccessControlObjectBase to handle.
 */
public abstract class ResourceBean<E extends AccessControlObjectBase> implements Serializable {
    private static final String ERROR_INVALID_REQUEST = "Ungültige Anfrage";
    private static final String ERROR_ACCESS_DENIED = "Sie verfügen über zu wenig Zugriffsrecht für diese Aktion.";
    private static final String ATTRIBUTE_FORM_ERRORS = "ch.avocado.share.controller.FormErrors";
    private static final String ERROR_NOT_LOGGED_IN = "Sie müssen angemeldet sein für diese Aktion.";

    /**
     * The default template returned by {@link #getIndexDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_LIST = "list.jsp";
    /**
     * The default template returned by {@link #getDetailDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_DETAILS = "details.jsp";
    /**
     * The default template returned by {@link #getEditDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_EDIT = "edit.jsp";
    /**
     * The default template returned by {@link #getErrorDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_ERROR = "error.jsp";
    /**
     * The default template returned by {@link #getCreateDispatcher(HttpServletRequest)}.
     */
    private static final String TEMPLATE_CREATE = "create.jsp";

    /**
     * The object which is returned by {@link #get()} is stored in this field.
     * @see #getObject()
     */
    private E object;

    private String id;
    private String method;
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
     *
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
     * Updates the object which can be accessed through getObject().
     * Use addFormError if there are invalid or missing parameters.
     *
     * @throws HttpBeanException
     */
    public abstract void update() throws HttpBeanException;


    public abstract void destroy() throws HttpBeanException;


    void setAccessingUserFromRequest(HttpServletRequest request) {
        UserSession userSession = new UserSession(request);
        User accessingUser = userSession.getUser();
        setAccessingUser(accessingUser);
    }


    public abstract String getAttributeName();

    public String getPluralAttributeName() {
        return getAttributeName() + "s";
    }


    /**
     * Get the dispatcher to render a list of objects. This list will be available in the
     * {@link HttpServletRequest#getAttribute(String) servlet attribute} named
     * named {@link #getPluralAttributeName()} and has the type {@link E E[]}.
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_LIST} in the same folder.
     */
    protected RequestDispatcher getIndexDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(TEMPLATE_LIST);
    }

    /**
     * Get the dispatcher to render a single object. This object will be available in the
     * {@link HttpServletRequest#getAttribute(String) servlet attribute} named
     * named {@link #getAttributeName()}} and has the type {@link E}.
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_DETAILS} in the same folder.
     */
    protected RequestDispatcher getDetailDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(TEMPLATE_DETAILS);
    }

    /**
     * Get the dispatcher to edit a single object. This object will be available in the
     * {@link HttpServletRequest#getAttribute(String) servlet attribute} named
     * named {@link #getAttributeName()}} and has the type {@link E}.
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_EDIT} in the same folder.
     */
    protected RequestDispatcher getEditDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(TEMPLATE_EDIT);
    }

    /**
     * Get the dispatcher to render a unknown fatal error.
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_ERROR} in the same folder.
     */
    protected RequestDispatcher getErrorDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(TEMPLATE_ERROR);
    }

    /**
     * Get the dispatcher to render a create a new  object.
     * @param request The http request
     * @return A {@link RequestDispatcher} which renders the file {@value TEMPLATE_CREATE} in the same folder.
     */
    protected RequestDispatcher getCreateDispatcher(HttpServletRequest request) {
        return request.getRequestDispatcher(TEMPLATE_CREATE);
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

    private void ensureIsAuthenticated() throws HttpBeanException {
        if (accessingUser == null) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ERROR_NOT_LOGGED_IN);
        }
    }

    private void ensureHasAccess(E target, AccessLevelEnum requiredLevel) throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        AccessLevelEnum grantedAccessLevel;
        if (accessingUser == null) {
            grantedAccessLevel = securityHandler.getAnonymousAccessLevel(target);
        } else {
            grantedAccessLevel = securityHandler.getAccessLevel(accessingUser, target);
        }
        if (!grantedAccessLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ERROR_ACCESS_DENIED);
        }
    }

    private TemplateType executeRequest(HttpServletRequest request, HttpServletResponse response) throws HttpBeanException {
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
                // TODO: replace element
            default:
                throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_INVALID_REQUEST);
        }
        return templateType;
    }

    /**
     * Handle DELETE request
     *
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doDelete(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        object = get();
        ensureHasAccess(object, AccessLevelEnum.OWNER);
        destroy();
        if (!hasErrors()) {
            templateType = TemplateType.INDEX;
        } else {
            templateType = TemplateType.EDIT;
            request.setAttribute(getAttributeName(), object);
        }
        return templateType;
    }

    /**
     * Handle POST request
     *
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doPatch(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        System.out.println("PATCH");
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
     *
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doGet(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        if (hasIdentifier()) {
            templateType = doGetOnObject(request);
        } else {
            templateType = doGetOnIndex(request);
        }
        return templateType;
    }

    /**
     * Handle GET requests on a single object
     *
     * @param request
     * @return The template type
     * @throws HttpBeanException
     */
    private TemplateType doGetOnObject(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        object = get();
        if (isEdit()) {
            System.out.println("EDIT");
            templateType = TemplateType.EDIT;
            ensureHasAccess(object, AccessLevelEnum.WRITE);
        } else {
            System.out.println("DETAIL");
            templateType = TemplateType.DETAIL;
            ensureHasAccess(object, AccessLevelEnum.READ);
        }
        request.setAttribute(getAttributeName(), object);
        return templateType;
    }

    /**
     * Handle GET request on the index (not on a single object)
     *
     * @param request
     * @return The template type
     * @throws HttpBeanException
     */
    private TemplateType doGetOnIndex(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        if (isCreate()) {
            System.out.println("CREATE");
            ensureIsAuthenticated();
            templateType = TemplateType.CREATE;
        } else {
            E[] objectList = index();
            System.out.println("INDEX");
            for (E objectInList : objectList) {
                ensureHasAccess(objectInList, AccessLevelEnum.READ);
            }
            templateType = TemplateType.INDEX;
            request.setAttribute(getPluralAttributeName(), objectList);
        }
        return templateType;
    }


    /**
     * Handle PUT request
     *
     * @param request
     * @return
     * @throws HttpBeanException
     */
    private TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        E object = create();
        request.setAttribute(getAttributeName(), object);
        if (hasErrors()) {
            templateType = TemplateType.CREATE;
        } else {
            templateType = TemplateType.DETAIL;
        }
        return templateType;

    }

    /**
     * Execute a request
     *
     * @param request
     * @param response
     * @throws ServletException
     */
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (response == null) throw new IllegalArgumentException("response is null");
        setAccessingUserFromRequest(request);
        if (request.getMethod().equals("GET")) {
            // Another request can't be simulated with GET.
            setMethod("GET");
        } else if (getMethod() == null) {
            setMethod(request.getMethod());
        }
        TemplateType templateType;
        try {
            templateType = executeRequest(request, response);
        } catch (HttpBeanException httpException) {
            try {
                response.sendError(httpException.getStatusCode(), httpException.getDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (hasErrors()) {
            request.setAttribute(ATTRIBUTE_FORM_ERRORS, this.formErrors);
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


    /**
     * Add a error related to a parameter
     *
     * @param parameter the parameter
     * @param message   the message describing the error
     */
    protected void addFormError(String parameter, String message) {
        this.formErrors.put(parameter, message);
    }

    /**
     * @return True if there occured errors while processing a request
     */
    public boolean hasErrors() {
        return !formErrors.isEmpty();
    }

    /**
     * @return True of the request has the action parameter set to {@value ACTION_EDIT}
     */
    private boolean isEdit() {
        return ACTION_EDIT.equals(action);
    }

    /**
     * @return True of the request has the action parameter set to {@value ACTION_CREATE}
     */
    private boolean isCreate() {
        return ACTION_CREATE.equals(action);
    }

    /**
     * @return The action parameter
     */
    public String getAction() {
        return action;
    }

    /**
     * Set the action parameter
     *
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the object or null if get() wasn't called or failed.
     * @return The object or null.
     */
    protected E getObject() {
        return object;
    }

    /**
     * Returns the if of the object
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set the identifiert of the object
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
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

    public Map<String, String> getFormErrors() {
        return new HashMap<>(this.formErrors);
    }
}
