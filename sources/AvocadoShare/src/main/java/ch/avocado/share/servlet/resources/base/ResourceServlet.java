package ch.avocado.share.servlet.resources.base;

import ch.avocado.share.common.HttpMethod;
import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * The resource servlet is used to handle request to modify or view a resource..
 *
 * @param <E> The class of the resource.
 */
public abstract class ResourceServlet<E extends AccessControlObjectBase> extends GenericServlet {

    public static final String PARAMETER_ACTION = "action";
    public static final String ERROR_ACTION_NOT_ALLOWED = "Aktion nicht erlaubt: ";
    public static final String ERROR_SET_CONTROLLER_ATTRIBUTES_FAILED = "Controller konnte nicht inititialisiert werden.";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";

    /**
     * Simulated request parameter. See {@link ResourceServlet#executeBeanAndRenderResult(HttpServletRequest, HttpServletResponse)}
     */
    public static final String PARAMETER_METHOD = "method";

    /**
     * Create a new ResourceServlet.
     * The constructor will call {@link ResourceServlet#getHtmlRenderer()} and register it to the default HTML types.
     */
    public ResourceServlet() {
    }

    /**
     * @return The controller class which can be created with an empty constructor.
     */
    protected abstract ResourceBean<E> getBean();

    private HashMap<String, ViewRenderer> contentRenderer = null;

    /**
     * Add a new renderer for a content type.
     *
     * @param contentType The content type (not null)
     * @param renderer    The renderer (not null)
     */
    private void registerRenderer(String contentType, ViewRenderer renderer) {
        if (contentType == null) throw new IllegalArgumentException("contentType is null");
        if (renderer == null) throw new IllegalArgumentException("renderer is null");
        contentRenderer.put(contentType, renderer);
    }

    /**
     * @return An renderer which can be used to render the object the views in html.
     */
    abstract protected ViewRenderer getHtmlRenderer();

    /**
     * Get the url for a view. The request is required to determinate the context path of the application.
     *
     * @param request The request to any servlet in this application.
     * @param view    The view for which the url should be returned.
     * @param object  If the view is for a single object the object has to be provided. (EDIT and DETAIL)
     * @return The url to the object.
     */
    protected String getUrlForView(HttpServletRequest request, View view, E object) {
        if (view == null) throw new IllegalArgumentException("view is null");
        String url = request.getServletPath();
        switch (view) {
            case EDIT:
                if (object == null) throw new IllegalArgumentException("object is null for edit");
                url += "?" + PARAMETER_ACTION + "=" + ACTION_EDIT;
                break;
            case CREATE:
                url += "?" + PARAMETER_ACTION + "=" + ACTION_CREATE;
                break;
            case DETAIL:
                if (object == null) throw new IllegalArgumentException("object is null for detail");
                url += "?id=" + object.getId();
                break;
            case LIST:
                break;
        }
        return url;
    }

    /**
     * Returns the required levels of access for an action.
     *
     * @param action The action
     * @return The required access level. If the access level is {@code null} an unauthenticated user can execute this
     * action.
     */
    protected AccessLevelEnum getRequiredAccessForAction(Action action) {
        switch (action) {
            case VIEW:
                return AccessLevelEnum.READ;
            case UPDATE:
                return AccessLevelEnum.OWNER;
            case DELETE:
                return AccessLevelEnum.MANAGE;
            case REPLACE:
                return AccessLevelEnum.MANAGE;
            case CREATE:
                return AccessLevelEnum.NONE;
            default:
                throw new RuntimeException("Action not implemented");
        }
    }

    /**
     * Returns the granted access on the object.
     *
     * @param userId   The identifier of the user to check.
     * @param objectId The identifier of the object.
     * @return The granted access level.
     * @throws HttpBeanException If there is an error while querying the granted access
     *                           level this exception is thrown.
     */
    private AccessLevelEnum getAccessOnObject(String userId, String objectId) throws HttpBeanException {
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR,
                    ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService());
        }
        try {
            if (userId != null) {
                return securityHandler.getAccessLevel(userId, objectId);
            } else {
                return securityHandler.getAnonymousAccessLevel(objectId);
            }
        } catch (DataHandlerException e) {
            throw new HttpBeanException(e);
        }
    }

    /**
     * @param userId   The user which can execute this actions.
     * @param objectId The object on which the actions can be executed.
     * @return A list of all actions allowed for the user.
     * @throws HttpBeanException
     */
    private List<Action> getAllowedActionsForUser(String userId, String objectId) throws HttpBeanException {
        List<Action> actions = new LinkedList<>();
        AccessLevelEnum allowedLevel = getAccessOnObject(userId, objectId);
        for (Action action : Action.values()) {
            AccessLevelEnum requiredLevel = getRequiredAccessForAction(action);
            if (allowedLevel.containsLevel(requiredLevel)) {
                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * Check if the user has the required access level for the action.
     *
     * @param userId   The user which will execute the action
     * @param objectId The object on which the action will is executed.
     * @param action   The action to be executed.
     * @throws HttpBeanException If the user has not enough access rights.
     */
    private void ensureAccess(String userId, String objectId, Action action) throws HttpBeanException {
        if (objectId == null) throw new IllegalArgumentException("objectId is null");
        if (action == null) throw new IllegalArgumentException("action is null");
        AccessLevelEnum requiredLevel = getRequiredAccessForAction(action);
        AccessLevelEnum allowedLevel = getAccessOnObject(userId, objectId);
        System.out.println("Allowd level: " + allowedLevel);
        System.out.println("User id: " + userId);
        System.out.println("ObjectId: " + objectId);
        System.out.println("Required: " + requiredLevel);
        if (!allowedLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(HttpStatusCode.FORBIDDEN, ERROR_ACTION_NOT_ALLOWED + action.name());
        }
    }

    /**
     * Construct a new controller and set its attributes accordingly.
     *
     * @param request   The request is used to extract the accessing user and set it to controller. (not null)
     * @param parameter Parsed parameters. They will be used to set the attributes of the controller. (not null)
     * @return The controller object. (not null)
     * @throws HttpBeanException If something wen't wrong this execption is thrown.
     */
    private ResourceBean<E> getResourceBean(HttpServletRequest request, Map<String, Object> parameter) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (parameter == null) throw new IllegalArgumentException("parameter is null");
        ResourceBean<E> bean;
        bean = getBean();
        setAccessingUserAttribute(bean, request);
        setBeanAttributes(bean, parameter);
        return bean;
    }

    /**
     * Sets the attributes of the controller using the parameters.
     *
     * @param controller The controller object (not null)
     * @param parameter  The parameters (not null)
     * @throws HttpBeanException
     */
    private void setBeanAttributes(ResourceBean<E> controller, Map<String, Object> parameter) throws HttpBeanException {
        if (parameter == null) throw new IllegalArgumentException("parameter is null");
        if (controller == null) throw new IllegalArgumentException("controller is null");
        for (Map.Entry<String, Object> parameterEntry : parameter.entrySet()) {
            String setterName = getSetterName(parameterEntry.getKey());
            System.out.println("Trying to invoke " + setterName);
            tryInvokeSetterOfBean(controller, setterName, parameterEntry.getValue());
        }
    }

    /**
     * Return the name of the setter. This method prefixes the field name with "set" and capitalizes the name.
     *
     * @param fieldName The name of the field
     * @return The name of the setter.
     */
    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }


    private void throwMethodNotAllowed(String method) throws HttpBeanException {
        if (method == null) throw new IllegalArgumentException("method is null");
        throw new HttpBeanException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ErrorMessageConstants.METHOD_NOT_ALLOWED + method);
    }

    /**
     * We need this method because HTML forms can only send POST and GET requests and we want to be able to "simulate"
     * another method. This can be done by setting the parameter {@value PARAMETER_METHOD} to the required method.
     *
     * @param request   The http request (not null)
     * @param parameter The parsed parameters (not null)
     * @return The http method to be executed.
     * @throws HttpBeanException
     */
    private HttpMethod getMethodFromRequest(HttpServletRequest request, Map<String, Object> parameter) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (parameter == null) throw new IllegalArgumentException("parameter is null");
        HttpMethod method = HttpMethod.fromString(request.getMethod());
        if (method == HttpMethod.POST) {
            if (parameter.containsKey(PARAMETER_METHOD)) {
                HttpMethod simulatedMethod = HttpMethod.fromString((String) parameter.get(PARAMETER_METHOD));
                if (simulatedMethod != null) {
                    method = simulatedMethod;
                }
            }
        }
        if (method == null) {
            throwMethodNotAllowed(request.getMethod());
        }
        return method;
    }

    /**
     * Returns the action associated with the http method
     *
     * @param method The method
     * @return The action
     */
    private Action getActionFromMethod(HttpMethod method) {
        if (method == null) throw new IllegalArgumentException("method is null");
        switch (method) {
            case POST:
                return Action.CREATE;
            case GET:
                return Action.VIEW;
            case PUT:
                return Action.REPLACE;
            case PATCH:
                return Action.UPDATE;
            case DELETE:
                return Action.DELETE;
        }
        throw new RuntimeException("Method not checked: " + method);
    }

    /**
     * @param request The request
     * @return A list of accepted encodings
     */
    private List<String> getAcceptedEncodings(HttpServletRequest request) {
        String accepted = request.getHeader("Accept");
        List<String> encodings = new ArrayList<>(1);
        for (String type : accepted.split(",")) {
            System.out.println("Type: " + type);
            if (type.contains(";")) {
                type = type.split(";")[0];
            }
            type = type.trim();
            encodings.add(type);
        }
        return encodings;
    }

    /**
     * @param request
     * @param response
     * @param config
     * @throws HttpBeanException
     * @throws ServletException
     * @throws IOException
     */
    private void renderViewConfig(HttpServletRequest request, HttpServletResponse response, ViewConfig config) throws HttpBeanException, ServletException, IOException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (response == null) throw new IllegalArgumentException("response is null");
        if (config == null) throw new IllegalArgumentException("config is null");
        ViewRenderer renderer = null;
        for (String contentType : getAcceptedEncodings(request)) {
            if (contentType != null) {
                if (contentType.contains(";")) {
                    contentType = contentType.split(";")[0];
                }
                contentType = contentType.trim();
                renderer = getContentRenderer().get(contentType);
                break;
            }
        }
        if (renderer == null) {
            throw new HttpBeanException(HttpStatusCode.NOT_ACCEPTABLE, "Kein Renderer gefunden für den Typ.");
        }
        System.out.println("callling renderer: " + renderer);
        renderer.renderView(config);

    }

    /**
     * @param request The request
     * @param response The response
     * @param action The succeeded action
     * @param object The object or null
     * @throws IOException
     */
    protected void redirectAfterSuccess(HttpServletRequest request, HttpServletResponse response, Action action, E object) throws IOException {
        View view;
        switch (action) {
            case VIEW:
                throw new IllegalArgumentException("action is VIEW");
            case DELETE:
                view = View.LIST;
                break;
            case UPDATE:
            case REPLACE:
            case CREATE:
                view = View.DETAIL;
                break;
            default:
                throw new RuntimeException("action not implemented");
        }
        response.sendRedirect(getUrlForView(request, view, object));
    }

    private void executeBeanAndRenderResult(HttpServletRequest request, HttpServletResponse response) throws HttpBeanException, IOException, ServletException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (response == null) throw new IllegalArgumentException("response is null");
        Map<String, Object> parameter = getParameter(request);
        HttpMethod method = getMethodFromRequest(request, parameter);
        Action action = getActionFromMethod(method);
        ResourceBean<E> bean = getResourceBean(request, parameter);
        ViewConfig viewConfig = null;
        boolean succeeded = false;
        E object = null;
        Members members;
        UserSession session = new UserSession(request);
        try {
            switch (action) {
                case VIEW: {
                    viewConfig = getConfigForActionView(bean, session, request, response);
                    break;
                }
                case REPLACE:
                    throw new HttpBeanException(HttpStatusCode.NOT_IMPLEMENTED, "Replace not implemented");
                case UPDATE: {
                    object = bean.get();
                    ensureAccess(session.getUserId(), object.getId(), Action.UPDATE);
                    bean.update(object);
                    if (object.hasErrors()) {
                        members = bean.getMembers(object);
                        viewConfig = new DetailViewConfig(View.EDIT, request, response, object, members);
                    } else {
                        succeeded = true;
                    }
                    break;
                }
                case DELETE: {
                    object = bean.get();
                    ensureAccess(session.getUserId(), object.getId(), Action.DELETE);
                    bean.destroy(object);
                    succeeded = true;
                    break;
                }
                case CREATE: {
                    if (getRequiredAccessForAction(Action.CREATE) != null) {
                        if(!session.isAuthenticated()) {
                            throw new HttpBeanException(HttpStatusCode.UNAUTHORIZED, ErrorMessageConstants.NOT_LOGGED_IN);
                        }
                    }
                    object = bean.create();
                    if (object.hasErrors()) {
                        viewConfig = new DetailViewConfig(View.CREATE, request, response, object, new Members(object));
                    } else {
                        succeeded = true;
                    }
                    break;
                }
                default:
                    throw new RuntimeException("Action not implemented");
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanException(e);
        }
        if (succeeded) {
            redirectAfterSuccess(request, response, action, object);
        } else if (viewConfig != null) {
            renderViewConfig(request, response, viewConfig);
        }
    }

    /**
     * Wrapper method for {@link #service(HttpServletRequest, HttpServletResponse)} which casts the request and response
     * into HTTP request and HTTP responses. An ServletException is thrown if they are not of this type.
     *
     * @param req the request
     * @param res the response
     * @throws ServletException
     * @throws IOException
     * @see GenericServlet#service(ServletRequest, ServletResponse)
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException("Not a HTTP request or response");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        service(httpRequest, httpResponse);
    }


    /**
     * Handle the request and response. In the most cases if an error occurs there will be no ServletException thrown.
     * Instead this method sets call {@link HttpServletResponse#sendError(int, String)} with an appropriate mesasge and
     * status code.
     *
     * @param request  the request
     * @param response the response
     * @throws IOException
     * @throws ServletException
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (response == null) throw new IllegalArgumentException("response is null");
        try {
            executeBeanAndRenderResult(request, response);
        } catch (HttpBeanException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(e.getStatusCode(), e.getMessage());
            } else {
                throw new ServletException(e.getMessage());
            }
        }
    }

    /**
     * @param bean       the bean on which the setter should be invoked (not null)
     * @param setterName the name of the setter method (not null)
     * @param value      the value to set (not null)
     * @return {@code true} if the method was found. Otherwise {@code false}  is returned.
     * @throws HttpBeanException if something went wrong while calling the setter.
     */
    private boolean tryInvokeSetterOfBean(ResourceBean<E> bean, String setterName, Object value) throws HttpBeanException {
        if (bean == null) throw new IllegalArgumentException("bean is null");
        if (value == null) throw new IllegalArgumentException("value is null");
        Class<?> classOrSuperclass = bean.getClass();
        // Search for method in this class or super-classes
        while (ResourceBean.class.isAssignableFrom(classOrSuperclass)) {
            Method[] methods = classOrSuperclass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    try {
                        method.invoke(bean, value);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR, ERROR_SET_CONTROLLER_ATTRIBUTES_FAILED);
                    }
                    return true;
                }
            }
            classOrSuperclass = classOrSuperclass.getSuperclass();
        }
        return false;
    }

    /**
     * Returns the {@link View} to be renderer for the {@link Action#VIEW}.
     *
     * @param bean    The controller (not null). This is mainly used to check if the controller has an identifier.
     * @param request The request (not null)
     * @return The view
     */
    private View getViewForActionView(ResourceBean<E> bean, HttpServletRequest request) {
        if (bean == null) throw new IllegalArgumentException("bean is null");
        if (request == null) throw new IllegalArgumentException("request is null");
        String actionParameter = request.getParameter(PARAMETER_ACTION);
        if (bean.hasIdentifier()) {
            if (actionParameter != null && actionParameter.equals(ACTION_EDIT)) {
                return View.EDIT;
            }
            return View.DETAIL;
        } else {
            if (actionParameter != null && actionParameter.equals(ACTION_CREATE)) {
                return View.CREATE;
            }
            return View.LIST;
        }
    }

    /**
     * Sets the accessing user in the (by calling {@link ResourceBean#setAccessingUser(User)}).
     *
     * @param bean    The controller
     * @param request The request
     */
    private void setAccessingUserAttribute(ResourceBean<E> bean, HttpServletRequest request) {
        UserSession session = new UserSession(request);
        if (session.getUser() != null) {
            bean.setAccessingUser(session.getUser());
        }
    }

    /**
     * Parses the parameter of the request.
     *
     * @param request The request.
     * @return A map of parameter - values.
     * @throws HttpBeanException
     */
    private Map<String, Object> getParameter(HttpServletRequest request) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            return getMultipartParameter(request);
        } else {
            Map<String, Object> parameter = new HashMap<>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                parameter.put(paramName, request.getParameter(paramName));
            }
            return parameter;
        }
    }

    /**
     * Parses mulipart parameters.
     *
     * @param request The request
     * @return A map of Parameter name mapped to the value.
     * @throws HttpBeanException
     */
    private Map<String, Object> getMultipartParameter(HttpServletRequest request) throws HttpBeanException {
        HashMap<String, Object> parameter = new HashMap<>();
        List<FileItem> items;
        try {
            items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        } catch (FileUploadException e) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        for (FileItem item : items) {
            if (item.isFormField()) {
                try {
                    parameter.put(item.getFieldName(), item.getString("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Found file: " + item.getFieldName());
                parameter.put(item.getFieldName(), item);
            }
        }
        return parameter;
    }

    /**
     * Prepare the configuration for the {@link Action#VIEW}.
     *
     * @param controller The controller
     * @param request    The request
     * @param response   The response
     * @return The view config.
     * @throws HttpBeanException
     * @throws DataHandlerException
     */
    protected ViewConfig getConfigForActionView(ResourceBean<E> controller, UserSession session, HttpServletRequest request, HttpServletResponse response) throws HttpBeanException, DataHandlerException {
        if (request == null) throw new IllegalArgumentException("request is null");
        ViewConfig viewConfig;
        View view = getViewForActionView(controller, request);
        if (view == View.LIST) {
            List<E> objects = controller.index();
            if (objects == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ErrorMessageConstants.ERROR_INDEX_FAILED);
            }
            List<Model> models = new ArrayList<>(objects.size());
            for (E object : objects) {
                models.add(object);
                System.out.println("Checking access for: " + object);
                ensureAccess(session.getUserId(), object.getId(), Action.VIEW);
            }
            viewConfig = new ListViewConfig(view, request, response, models);
        } else {
            E object = null;
            Members members = null;
            if (controller.hasIdentifier()) {
                ensureAccess(session.getUserId(), controller.getId(), Action.VIEW);
                object = controller.get();
                if (object == null) {
                    throw new HttpBeanException(HttpStatusCode.NOT_FOUND,
                            ErrorMessageConstants.OBJECT_NOT_FOUND);
                }
                members = controller.getMembers(object);
            }
            viewConfig = new DetailViewConfig(view, request, response, object, members);
        }
        return viewConfig;
    }

    /**
     * A map with the content type as the key and the renderer as the value.
     * This is mainly used in
     * {@link ResourceServlet#renderViewConfig(HttpServletRequest, HttpServletResponse, ViewConfig) renderViewConfig}.
     */
    private HashMap<String, ViewRenderer> getContentRenderer() {
        if(contentRenderer == null) {
            contentRenderer = new HashMap<>();
            ViewRenderer renderer = getHtmlRenderer();
            registerRenderer("text/html", renderer);
            registerRenderer("application/xhtml+xml", renderer);
        }
        return contentRenderer;
    }
}
