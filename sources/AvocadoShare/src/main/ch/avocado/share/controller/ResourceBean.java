package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class should be used for all resources and does the following things:
 * * Check access
 * * Execute appropriate method (get, create, index, etc.)
 * * Include the template accordingly.
 * <p>
 * Once renderRequest is called, this class checks the method of the request.<br/>
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
public abstract class ResourceBean<E extends AccessControlObjectBase> extends RequestHandlerBeanBase {
    public static final String ATTRIBUTE_FORM_ERRORS = "ch.avocado.share.controller.FormErrors";

    /**
     * The object which is returned by {@link #get()} is stored in this field.
     * @see #getObject()
     */
    private E object;

    private String id;
    private Map<String, String> formErrors = new HashMap<>();

    /**
     * The action parameter
     */
    private String action;
    private String description;

    /**
     * This method returns true if the bean is supplied with an identifier
     * to find a unique element. (key or unique attribute)
     * This method is used to determinate if the list of elements is requested (index())
     * or a single object get().
     *
     * @return True if the bean has a valid identifier
     */
    protected boolean hasIdentifier() {
        return getId() != null;
    }

    /**
     * Make sure you set an error with {@link #addFormError(String, String)}
     * if you return null!
     * @return The new created object or null if there are errors.
     * @throws HttpBeanException
     */
    public abstract E create() throws HttpBeanException, DataHandlerException;

    /**
     * Load and returns a single Object by using the given parameters.
     *
     * @return The object (never null)
     * @throws HttpBeanException
     */
    public abstract E get() throws HttpBeanException, DataHandlerException;

    /**
     * Returns a list filtered by the given parameters
     *
     * @return A list of objects
     * @throws HttpBeanException
     */
    public abstract List<E> index() throws HttpBeanException, DataHandlerException;

    /**
     * Updates the object which can be accessed through getObject().
     * Use addFormError if there are invalid or missing parameters.
     *
     * @throws HttpBeanException
     */
    public abstract void update() throws HttpBeanException, DataHandlerException;

    protected boolean updateDescription(AccessControlObjectBase module) {
        boolean updated = false;
        if (getDescription() != null && !getDescription().equals(module.getDescription())) {
            checkParameterDescription();
            if (!hasErrors()) {
                module.setDescription(getDescription());
                updated = true;
            }
        }
        return updated;
    }

    /**
     * Destroy the object
     * @throws HttpBeanException
     */
    public abstract void destroy() throws HttpBeanException, DataHandlerException;

    /**
     * Replace the object
     * @throws HttpBeanException
     */
    public void replace() throws HttpBeanException, DataHandlerException {
        throw new HttpBeanException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Replacement not allowed");
    }

    public abstract String getAttributeName();

    public String getPluralAttributeName() {
        return getAttributeName() + "s";
    }

    /**
     * Handle DELETE request
     *
     * @param request
     * @return
     * @throws HttpBeanException
     */
    @Override
    protected TemplateType doDelete(HttpServletRequest request) throws HttpBeanException {
        if(request == null) throw new IllegalArgumentException("request is null");
        TemplateType templateType;
        try {
            object = get();
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
        }
        if(object == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_GET_FAILED);
        }
        ensureAccessingUserHasAccess(object, AccessLevelEnum.MANAGE);
        try {
            destroy();
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
        }
        if (!hasErrors()) {
            setFormErrorsInRequestAttribute(request);
            templateType = TemplateType.EDIT;
            request.setAttribute(getAttributeName(), object);
        } else {
            templateType = TemplateType.INDEX;
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
    @Override
    protected TemplateType doPatch(HttpServletRequest request) throws HttpBeanException {
        if(request == null) throw new IllegalArgumentException("request is null");
        TemplateType templateType;
        System.out.println("PATCH");
        try {
            object = get();
        } catch (DataHandlerException e) {
            throw new HttpBeanDatabaseException();
        }
        if(object == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_GET_FAILED);
        }
        ensureAccessingUserHasAccess(object, AccessLevelEnum.WRITE);
        try {
            update();
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
        }
        if (!hasErrors()) {
            // On success show details
            templateType = TemplateType.DETAIL;
        } else {
            // On failure show edit form again
            setFormErrorsInRequestAttribute(request);
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
    @Override
    protected TemplateType doGet(HttpServletRequest request) throws HttpBeanException {
        if(request == null) throw new IllegalArgumentException("request is null");
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
     * This object will be available in the
     * {@link HttpServletRequest#getAttribute(String) servlet attribute} named
     * named {@link #getAttributeName()}} and has the type {@link AccessControlObjectBase}.
     * @param request
     * @return The template type
     * @throws HttpBeanException
     */
    private TemplateType doGetOnObject(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        try {
            object = get();
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
        }
        if(object == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_GET_FAILED);
        }
        if (isEdit()) {
            System.out.println("EDIT");
            templateType = TemplateType.EDIT;
            ensureAccessingUserHasAccess(object, AccessLevelEnum.WRITE);
        } else {
            System.out.println("DETAIL");
            templateType = TemplateType.DETAIL;
            ensureAccessingUserHasAccess(object, AccessLevelEnum.READ);
        }
        request.setAttribute(getAttributeName(), object);
        return templateType;
    }

    /**
     * Handle GET request on the index (not on a single object)
     * This list will be available in the
     * {@link HttpServletRequest#getAttribute(String) servlet attribute} named
     * named {@link #getPluralAttributeName()} and has the type {@link AccessControlObjectBase E[]}.
     * @param request
     * @return The template type
     * @throws HttpBeanException
     */
    private TemplateType doGetOnIndex(HttpServletRequest request) throws HttpBeanException {
        TemplateType templateType;
        if (isCreate()) {
            System.out.println("CREATE");
            ensureIsAuthenticatedToCreate();
            templateType = TemplateType.CREATE;
        } else {
            List<E> objectList;
            try {
                objectList = index();
            } catch (DataHandlerException e) {
                e.printStackTrace();
                throw new HttpBeanDatabaseException();
            }
            if(objectList == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_INDEX_FAILED);
            }
            System.out.println("INDEX");
            for (E objectInList : objectList) {
                ensureAccessingUserHasAccess(objectInList, AccessLevelEnum.READ);
            }
            templateType = TemplateType.INDEX;
            request.setAttribute(getPluralAttributeName(), objectList);
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
    @Override
    protected TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        ensureIsAuthenticatedToCreate();
        TemplateType templateType;
        E object;
        try {
            object = create();
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
        }
        if(object == null && !hasErrors()) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_CREATE_FAILED);
        }
        request.setAttribute(getAttributeName(), object);
        if (hasErrors()) {
            setFormErrorsInRequestAttribute(request);
            templateType = TemplateType.CREATE;
        } else {
            templateType = TemplateType.DETAIL;
        }
        return templateType;
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
        System.out.println("Create?: " + action);
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
     * Returns the identifier of the object
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set the identifier of the object
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getFormErrors() {
        return new HashMap<>(this.formErrors);
    }

    private void setFormErrorsInRequestAttribute(HttpServletRequest request) {
        request.setAttribute(ATTRIBUTE_FORM_ERRORS, getFormErrors());
    }

    /**
     * @return The description of the file
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description of the file
     */
    public void setDescription(String description) {
        this.description = description;
    }

    protected void checkParameterDescription() {
        if (getDescription() == null || getDescription().trim().isEmpty()) {
            addFormError("description", ErrorMessageConstants.ERROR_NO_DESCRIPTION);
        } else {
            setDescription(getDescription().trim());
        }
    }
}
