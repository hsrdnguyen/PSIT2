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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public abstract class ResourceServlet<E extends AccessControlObjectBase> extends GenericServlet {

    private static final String PARAMETER_ACTION = "action";
    public static final String ATTRIBUTE_FORM_ERROR = "FormError";
    public static final String ERROR_ACTION_NOT_ALLOWED = "Aktion nicht erlaubt: ";
    public static final String ERROR_SET_CONTROLLER_ATTRIBUTES_FAILED = "Controller konnte nicht inititialisiert werden.";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";
    /**
     * Simulated request parameter.
     */
    public final String PARAMETER_METHOD = "method";


    protected abstract Class<? extends ResourceBean<E>> getBeanClass();

    private HashMap<String, ViewRenderer> contentRenderer = new HashMap<>();

    private void registerRenderer(String contentType, ViewRenderer renderer) {
        contentRenderer.put(contentType, renderer);
    }

    abstract protected ViewRenderer getHtmlRenderer();

    protected String getUrlForView(HttpServletRequest request, View view, E object) {
        if(view == null) throw new IllegalArgumentException("view is null");
        String url = request.getServletPath();
        switch (view) {
            case EDIT:
                if(object == null) throw new IllegalArgumentException("object is null for edit");
                url += "?" + PARAMETER_ACTION + "=" + ACTION_EDIT;
                break;
            case CREATE:
                url += "?" + PARAMETER_ACTION + "=" + ACTION_CREATE;
                break;
            case DETAIL:
                if(object == null) throw new IllegalArgumentException("object is null for detail");
                url += "?id=" + object.getId();
                break;
            case LIST:
                break;
        }
        return url;
    }

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

    private void ensureAccess(String userId, String objectId, Action action) throws HttpBeanException {
        if (objectId == null) throw new IllegalArgumentException("objectId is null");
        if (action == null) throw new IllegalArgumentException("action is null");
        AccessLevelEnum requiredLevel = getRequiredAccessForAction(action);
        AccessLevelEnum allowedLevel = getAccessOnObject(userId, objectId);
        if (!allowedLevel.containsLevel(requiredLevel)) {
            throw new HttpBeanException(HttpStatusCode.FORBIDDEN, ERROR_ACTION_NOT_ALLOWED + action.name());
        }
    }

    private ResourceBean<E> getResourceBean(HttpServletRequest request) throws HttpBeanException {
        ResourceBean<E> bean;
        try {
            bean = getBeanClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Could not create bean");
        }
        setBeanAttributes(request, bean);
        return bean;
    }

    private String getSetterName(String parameter) {
        return "set" + parameter.substring(0, 1).toUpperCase() + parameter.substring(1);
    }


    private void throwMethodNotAllowed(String method) throws HttpBeanException {
        if (method == null) throw new IllegalArgumentException("method is null");
        throw new HttpBeanException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ErrorMessageConstants.METHOD_NOT_ALLOWED + method);
    }


    private HttpMethod getMethodFromRequest(HttpServletRequest request) throws HttpBeanException {
        HttpMethod method = HttpMethod.fromString(request.getMethod());
        if (method == HttpMethod.POST) {
            if (request.getParameter(PARAMETER_METHOD) != null) {
                HttpMethod simulatedMethod = HttpMethod.fromString(request.getParameter(PARAMETER_METHOD));
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

    private Action getActionFromMethod(HttpMethod method) {
        if(method == null) throw new IllegalArgumentException("method is null");
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


    private void renderViewConfig(HttpServletRequest request, HttpServletResponse response, ViewConfig config) throws HttpBeanException, ServletException, IOException {
        String contentType = request.getContentType();
        if(contentType.contains(";")) {
            contentType = contentType.split(";")[0];
        }
        contentType = contentType.trim();
        ViewRenderer renderer = contentRenderer.get(contentType);
        if(renderer == null) {
            throw new HttpBeanException(HttpStatusCode.NOT_ACCEPTABLE, "Kein Renderer gefunden für den Typ.");
        }
        renderer.renderView(config);
    }

    private void executeBeanAndRenderResult(HttpServletRequest request, HttpServletResponse response) throws HttpBeanException, IOException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (response == null) throw new IllegalArgumentException("response is null");
        HttpMethod method = getMethodFromRequest(request);
        Action action = getActionFromMethod(method);
        ResourceBean<E> bean = getResourceBean(request);
        ViewConfig viewConfig = null;
        View redirectTo = null;
        E object = null;
        Members members;
        try {
            switch (action) {
                case VIEW:
                    renderView(request, response);
                case REPLACE:
                    throw new HttpBeanException(HttpStatusCode.NOT_IMPLEMENTED, "Replace not implemented");
                case UPDATE: {
                    object = bean.get();
                    bean.update(object);
                    if (object.hasErrors()) {
                        members = bean.getMembers(object);
                        viewConfig = new DetailViewConfig(View.EDIT, request, response, object, members);
                    } else {
                        redirectTo = View.DETAIL;
                    }
                    break;
                }
                case DELETE: {
                    object = bean.get();
                    bean.destroy(object);
                    redirectTo = View.LIST;
                    break;
                }
                case CREATE: {
                    object = bean.create();
                    if(object.hasErrors()) {
                        viewConfig = new DetailViewConfig(View.CREATE, request, response, object, new Members(object));
                    } else {
                        redirectTo = View.DETAIL;
                    }
                }
            }
        } catch (DataHandlerException e) {
            throw new HttpBeanException(e);
        }
        if(redirectTo != null) {
            assert viewConfig == null;
            response.sendRedirect(getUrlForView(request, redirectTo, object));
        } else {
            assert viewConfig != null;

        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        //request.getRequestDispatcher("includes/header.jsp").include(request, response);
        try {
            executeBeanAndRenderResult(request, response);
        } catch (HttpBeanException e) {
            // we cannot call sendError() after the response has been committed
            request.setAttribute("ErrorStatus", e.getStatusCode());
            request.setAttribute("ErrorMessage", e.getDescription());
            request.getRequestDispatcher("includes/error.jsp").include(request, response);
        }
        //request.getRequestDispatcher("includes/footer.jsp").include(request, response);
    }

    private boolean tryInvokeSetterOfBean(ResourceBean<E> bean, Object value, String setterName) throws HttpBeanException {
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

    private View getViewForActionView(ResourceBean<E> bean, HttpServletRequest request) {
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

    private void setAccessingUserAttribute(ResourceBean<E> bean, HttpServletRequest request) {
        UserSession session = new UserSession(request);
        if (session.getUser() != null) {
            bean.setAccessingUser(session.getUser());
        }
    }


    private void setBeanAttributesFromMultipart(HttpServletRequest request, ResourceBean<E> bean) throws HttpBeanException {
        List<FileItem> items;
        try {
            items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        } catch (FileUploadException e) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        for (FileItem item : items) {
            String setterName = getSetterName(item.getFieldName());
            if (item.isFormField()) {
                String value;
                try {
                    value = item.getString("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                tryInvokeSetterOfBean(bean, value, setterName);
            } else {
                tryInvokeSetterOfBean(bean, item, setterName);
            }
        }
    }

    private void setBeanAttributes(HttpServletRequest request, ResourceBean<E> bean) throws HttpBeanException {
        if(request == null) throw new IllegalArgumentException("request is null");
        if(bean == null) throw new IllegalArgumentException("bean is null");
        setAccessingUserAttribute(bean, request);
        if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            setBeanAttributesFromMultipart(request, bean);
        } else {
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                // Do not try to set simulated method
                if (paramName.equals(PARAMETER_METHOD)) {
                    continue;
                }
                System.out.println("trying " + getSetterName(paramName));
                tryInvokeSetterOfBean(bean, request.getParameter(paramName), getSetterName(paramName));
            }
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException("Not a HTTP request or response");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        service(httpRequest, httpResponse);
    }

    protected View renderView(HttpServletRequest request, HttpServletResponse response) throws HttpBeanException, DataHandlerException {
        if (request == null) throw new IllegalArgumentException("request is null");
        ResourceBean<E> bean = getResourceBean(request);
        UserSession session = new UserSession(request);
        View view = getViewForActionView(bean, request);
        if (view == View.LIST) {
            List<E> objects = bean.index();
            if (objects == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ErrorMessageConstants.ERROR_INDEX_FAILED);
            }
            for (E object : objects) {
                ensureAccess(session.getUserId(), object.getId(), Action.VIEW);
            }
            request.setAttribute(bean.getPluralAttributeName(), objects);
        } else {
            ensureAccess(session.getUserId(), bean.getId(), Action.VIEW);
            E object = bean.get();
            if (object == null) {
                throw new HttpBeanException(HttpStatusCode.NOT_FOUND,
                        ErrorMessageConstants.OBJECT_NOT_FOUND);
            }
            request.setAttribute(bean.getAttributeName(), object);
            request.setAttribute("Members", bean.getMembers(object));
        }
        return view;
    }
}
