package ch.avocado.share.controller;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Members;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Bean to modify, view or list resources.
 * No access check is done in this methods.
 *
 * @param <E> The subclass of AccessControlObjectBase to handle.
 */
public abstract class ResourceBean<E extends AccessControlObjectBase> implements Serializable {

    private String id;

    private String description;

    private static final int DESCRIPTION_MAX_LENGTH = 512;

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
     * @param object
     * @throws HttpBeanException
     */
    public abstract void update(E object) throws HttpBeanException, DataHandlerException;

    /**
     * Update the descriptipon of the object
     * @param object the object on which the description is updated if there is no error.
     * @return true if the description has been changed.
     */
    protected boolean updateDescription(AccessControlObjectBase object) {
        boolean updated = false;
        if (getDescription() != null && !getDescription().equals(object.getDescription())) {
            checkParameterDescription(object);
            if (!object.hasErrors()) {
                object.setDescription(getDescription());
                updated = true;
            }
        }
        return updated;
    }

    /**
     * @return {@code true} if the resource has members and these should be loaded.
     */
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


    public Members getMembers(E object) throws HttpBeanException {
        if (object == null) throw new IllegalStateException("object is null");
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
            if(getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                model.addFieldError("description", ErrorMessageConstants.DESCRIPTION_TOO_LONG);
            }
        }
    }


    private User accessingUser;

    /**
     * Mapper for {@link ServiceLocator#getService(Class)} which raises a  HttpBeanException instead.
     *
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
     * Checks if the {@link #getAccessingUser() accessing user} has the required level of access.
     *
     * @param target        The access target
     * @param requiredLevel The required level of access on the target.
     * @throws HttpBeanException If the required access is not met.
     */
    protected void ensureAccessingUserHasAccess(AccessControlObjectBase target, AccessLevelEnum requiredLevel) throws HttpBeanException {
        if (target == null) throw new IllegalArgumentException("target is null");
        if (requiredLevel == null) throw new IllegalArgumentException("requiredLevel is null");
        ISecurityHandler securityHandler = getService(ISecurityHandler.class);
        AccessLevelEnum grantedAccessLevel;
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
