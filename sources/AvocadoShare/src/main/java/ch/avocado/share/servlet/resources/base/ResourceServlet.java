package ch.avocado.share.servlet.resources.base;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.AccessDeniedException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static ch.avocado.share.common.HttpStatusCode.*;
import static ch.avocado.share.common.constants.ErrorMessageConstants.*;


/**
 * The resource servlet is used to handle request to modify or view a resource..
 *
 * @param <E> The class of the resource.
 */
public abstract class ResourceServlet<E extends AccessControlObjectBase> extends ExtendedHttpServlet {

    public static final String PARAMETER_ACTION = "action";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";

    private static final String ERROR_SET_CONTROLLER_ATTRIBUTES_FAILED = "Controller konnte nicht inititialisiert werden.";
    private static final String ERROR_ACTION_NOT_ALLOWED = "Aktion nicht erlaubt: ";

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
        if (contentType == null) throw new NullPointerException("contentType is null");
        if (renderer == null) throw new NullPointerException("renderer is null");
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
        if (view == null) throw new NullPointerException("view is null");
        String url = request.getServletPath();
        switch (view) {
            case EDIT:
                if (object == null) throw new NullPointerException("object is null for edit");
                url += "?" + PARAMETER_ACTION + "=" + ACTION_EDIT;
                break;
            case CREATE:
                url += "?" + PARAMETER_ACTION + "=" + ACTION_CREATE;
                break;
            case DETAIL:
                if (object == null) throw new NullPointerException("object is null for detail");
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
            throw new HttpBeanException(INTERNAL_SERVER_ERROR,
                    SERVICE_NOT_FOUND + e.getService());
        }
        try {
            if (userId != null) {
                return securityHandler.getAccessLevel(userId, objectId);
            } else {
                return securityHandler.getAnonymousAccessLevel(objectId);
            }
        } catch (DataHandlerException e) {
            // TODO: log
            e.printStackTrace();
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
     * @return The granted level.
     * @throws HttpBeanException If the user has not enough access rights.
     */
    private AccessLevelEnum ensureAccess(String userId, String objectId, Action action) throws HttpBeanException {
        if (objectId == null) throw new NullPointerException("objectId is null");
        if (action == null) throw new NullPointerException("action is null");
        AccessLevelEnum requiredLevel = getRequiredAccessForAction(action);
        AccessLevelEnum allowedLevel = getAccessOnObject(userId, objectId);
        if (!allowedLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(FORBIDDEN, ERROR_ACTION_NOT_ALLOWED + action.name());
        }
        return allowedLevel;
    }

    /**
     * Construct a new controller and set its attributes accordingly.
     *
     * @param parameter Parsed parameters. They will be used to set the attributes of the controller. (not null)
     * @return The controller object. (not null)
     * @throws HttpBeanException If something wen't wrong this execption is thrown.
     */
    private ResourceBean<E> getResourceBean(Map<String, Object> parameter) throws HttpBeanException {
        if (parameter == null) throw new NullPointerException("parameter is null");
        ResourceBean<E> bean;
        bean = getBean();
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
        if (parameter == null) throw new NullPointerException("parameter is null");
        if (controller == null) throw new NullPointerException("controller is null");
        for (Map.Entry<String, Object> parameterEntry : parameter.entrySet()) {
            String setterName = getSetterName(parameterEntry.getKey());
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


    /**
     * @param request The request
     * @return A list of accepted encodings
     */
    private List<String> getAcceptedEncodings(HttpServletRequest request) {
        String accepted = request.getHeader("Accept");
        List<String> encodings = new ArrayList<>(1);
        for (String type : accepted.split(",")) {
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
    private void renderViewConfig(HttpServletRequest request, HttpServletResponse response, ViewConfig config) throws HttpBeanException, IOException {
        if (request == null) throw new NullPointerException("request is null");
        if (response == null) throw new NullPointerException("response is null");
        if (config == null) throw new NullPointerException("config is null");
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
            throw new HttpBeanException(NOT_ACCEPTABLE, "Kein Renderer gefunden für den Typ.");
        }
        try {
            renderer.renderView(config);
        } catch (ServletException e) {
            throw new HttpBeanException(INTERNAL_SERVER_ERROR, NOT_RENDERABLE, e);
        }
    }

    /**
     * @param request  The request
     * @param response The response
     * @param action   The succeeded action
     * @param object   The object or null
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


    @Override
    protected void doView(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpBeanException, IOException {
        ResourceBean<E> bean = getResourceBean(parameter);
        if (session.isAuthenticated()) {
            bean.setAccessingUser(session.getUser());
        }
        ViewConfig viewConfig;
        try {
            viewConfig = getConfigForActionView(bean, session, request, response);
        } catch (ServiceException e) {
            throw new HttpBeanException(e);
        }
        renderViewConfig(request, response, viewConfig);
    }

    @Override
    protected void doReplace(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpBeanException {
        throw new HttpBeanException(NOT_IMPLEMENTED, "Replace not implemented");
    }


    @Override
    protected void doCreate(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpBeanException, IOException {
        ResourceBean<E> bean = getResourceBean(parameter);
        if (session.isAuthenticated()) {
            bean.setAccessingUser(session.getUser());
        }
        if (getRequiredAccessForAction(Action.CREATE) != null) {
            if (!session.isAuthenticated()) {
                throw new HttpBeanException(UNAUTHORIZED, NOT_LOGGED_IN);
            }
        }
        E object = null;
        try {
            object = bean.create();
        } catch (ServiceException e) {
            throw new HttpBeanException(e);
        } catch (AccessDeniedException e) {
            throw new HttpBeanException(e);
        }
        if (object.hasErrors()) {
            DetailViewConfig viewConfig = new DetailViewConfig(View.CREATE, request, response, object, new Members(object), AccessLevelEnum.OWNER);
            renderViewConfig(request, response, viewConfig);
        } else {
            redirectAfterSuccess(request, response, Action.CREATE, object);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpBeanException, IOException {
        ResourceBean<E> bean = getResourceBean(parameter);
        if (session.isAuthenticated()) {
            bean.setAccessingUser(session.getUser());
        }
        E object = null;
        try {
            object = bean.get();
            ensureAccess(session.getUserId(), object.getId(), Action.DELETE);
            bean.destroy(object);
        } catch (ServiceException e) {
            throw new HttpBeanException(e);
        }
        redirectAfterSuccess(request, response, Action.DELETE, object);
    }

    @Override
    protected void doUpdate(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpBeanException, IOException {
        ResourceBean<E> bean = getResourceBean(parameter);
        if (session.isAuthenticated()) {
            bean.setAccessingUser(session.getUser());
        }
        AccessLevelEnum level;
        E object;
        try {
            object = bean.get();
            level = ensureAccess(session.getUserId(), object.getId(), Action.UPDATE);
            bean.update(object);
        } catch (ServiceException e) {
            throw new HttpBeanException(e);
        }
        if (object.hasErrors()) {
            Members members = bean.getMembers(object);
            DetailViewConfig viewConfig = new DetailViewConfig(View.EDIT, request, response, object, members, level);
            renderViewConfig(request, response, viewConfig);
        } else {
            redirectAfterSuccess(request, response, Action.UPDATE, object);
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
        if (bean == null) throw new NullPointerException("bean is null");
        if (value == null) throw new NullPointerException("value is null");
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
                        throw new HttpBeanException(INTERNAL_SERVER_ERROR, ERROR_SET_CONTROLLER_ATTRIBUTES_FAILED);
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
        if (bean == null) throw new NullPointerException("bean is null");
        if (request == null) throw new NullPointerException("request is null");
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
     * Prepare the configuration for the {@link Action#VIEW}.
     *
     * @param controller The controller
     * @param request    The request
     * @param response   The response
     * @return The view config.
     * @throws HttpBeanException
     * @throws DataHandlerException
     */
    protected ViewConfig getConfigForActionView(ResourceBean<E> controller, UserSession session, HttpServletRequest request, HttpServletResponse response) throws HttpBeanException, ServiceException {
        if (request == null) throw new NullPointerException("request is null");
        ViewConfig viewConfig;
        View view = getViewForActionView(controller, request);
        if (view == View.LIST) {
            List<E> objects = controller.index();
            if (objects == null) {
                throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR,
                        ERROR_INDEX_FAILED);
            }
            List<Model> models = new ArrayList<>(objects.size());
            for (E object : objects) {
                models.add(object);
                ensureAccess(session.getUserId(), object.getId(), Action.VIEW);
            }
            viewConfig = new ListViewConfig(view, request, response, models);
        } else {
            E object = null;
            Members members = null;
            AccessLevelEnum level = null;
            if (controller.hasIdentifier()) {
                level = ensureAccess(session.getUserId(), controller.getId(), Action.VIEW);
                object = controller.get();
                if (object == null) {
                    throw new HttpBeanException(NOT_FOUND,
                            OBJECT_NOT_FOUND);
                }
                members = controller.getMembers(object);
            }
            viewConfig = new DetailViewConfig(view, request, response, object, members, level);
        }
        return viewConfig;
    }

    /**
     * A map with the content type as the key and the renderer as the value.
     * This is mainly used in
     * {@link ResourceServlet#renderViewConfig(HttpServletRequest, HttpServletResponse, ViewConfig) renderViewConfig}.
     */
    private HashMap<String, ViewRenderer> getContentRenderer() {
        if (contentRenderer == null) {
            contentRenderer = new HashMap<>();
            ViewRenderer renderer = getHtmlRenderer();
            registerRenderer("text/html", renderer);
            registerRenderer("application/xhtml+xml", renderer);
        }
        return contentRenderer;
    }
}
