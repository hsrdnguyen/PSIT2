package ch.avocado.share.controller;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
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
 * is called an the template {@link #getEditDispatcher(HttpServletRequest)} is included.
 * </li>
 * <li>  Otherwise {@link #get()} is called and  {@link #getDetailDispatcher(HttpServletRequest)} is included</li>
 * </ul>
 * <li>
 * If {@link #hasIdentifier()} is {@code false}:
 * </li><ul><li>
 * If the attribute {@link #getAction() action} is set to {@value ACTION_CREATE}
 * the template {@link #getCreateDispatcher(HttpServletRequest)} to create a new
 * object is included.
 * </li>
 * <li>
 * Otherwise {@link #index()} is called and the template
 * {@link #getIndexDispatcher(HttpServletRequest)} is included
 * </li></ul></ul>
 *
 * @param <E> The subclass of AccessControlObjectBase to handle.
 */
public abstract class ResourceBean<E extends AccessControlObjectBase> extends RequestHandlerBeanBase {
    public static final String ATTRIBUTE_FORM_ERRORS = "ch.avocado.share.controller.FormErrors";

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
    public boolean hasIdentifier() {
        return getId() != null;
    }

    /**
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
     * @param object
     */
    public abstract void update(E object) throws HttpBeanException, DataHandlerException;

    protected boolean updateDescription(AccessControlObjectBase model) {
        boolean updated = false;
        if (getDescription() != null && !getDescription().equals(model.getDescription())) {
            checkParameterDescription(model);
            if (!model.hasErrors()) {
                model.setDescription(getDescription());
                updated = true;
            }
        }
        return updated;
    }

    protected abstract boolean hasMembers();

    /**
     * Destroy the object
     *
     * @throws HttpBeanException
     */
    public abstract void destroy(E object) throws HttpBeanException, DataHandlerException;

    /**
     * Replace the object
     *
     * @throws HttpBeanException
     */
    public void replace(E object) throws HttpBeanException, DataHandlerException {
        throw new HttpBeanException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Replacement not allowed");
    }

    public abstract String getAttributeName();

    public String getPluralAttributeName() {
        return getAttributeName() + "s";
    }

    public Members getMembers(E object) throws HttpBeanException {
        if(object == null) throw new IllegalStateException("object is null");
        Members members;
        if (!hasMembers()) {
            return null;
        }
        try {
            members = Members.fromIdsWithRights(getUsersWithAccess(object), getGroupsWithAccess(object), object);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR, ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService());
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR, ErrorMessageConstants.DATAHANDLER_EXPCEPTION);
        }
        return members;
    }

    private Map<String, AccessLevelEnum> getUsersWithAccess(E object) throws HttpBeanException, DataHandlerException {
        return getService(ISecurityHandler.class).getUsersWithAccessIncluding(AccessLevelEnum.READ, object);
    }

    private Map<String, AccessLevelEnum> getGroupsWithAccess(E object) throws HttpBeanException, DataHandlerException {
        return getService(ISecurityHandler.class).getGroupsWithAccessIncluding(AccessLevelEnum.READ, object);
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

    public void setFormErrorsInRequestAttribute(HttpServletRequest request) {
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

    protected void checkParameterDescription(AccessControlObjectBase model) {
        if (getDescription() == null || getDescription().trim().isEmpty()) {
            model.addFieldError("description", ErrorMessageConstants.ERROR_NO_DESCRIPTION);
        } else {
            setDescription(getDescription().trim());
        }
    }
}
