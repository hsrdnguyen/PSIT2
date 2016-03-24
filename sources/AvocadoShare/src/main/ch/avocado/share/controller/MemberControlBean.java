package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This bean can be used to control (and only control) access to a AccessControlObjectBase.
 * It is not intended to display relationship.
 */
public abstract class MemberControlBean<T extends AccessControlObjectBase> extends RequestHandlerBeanBase {
    public static final String ERROR_SERVICELOCATOR = "Servicelocator error";
    public static final String ERROR_GROUP_OR_USER_NOT_FOUND = "Gruppe oder Benutzer konnte nichte gefunden werden.";
    public static final String ERROR_ACCESS_ALREADY_EXISTS = "Es existiert bereits ein Zugriffsrecht.";
    public static final String ERROR_TARGET_NOT_FOUND = "Access target not found.";

    private String ownerId = null;
    private AccessLevelEnum level;
    private AccessIdentity ownerIdentity = null;
    private T target;
    private String targetId;


    abstract protected T getTargetById(String id) throws HttpBeanException;

    /**
     * @return An IGroupDataHandler
     * @throws HttpBeanException
     */
    protected IGroupDataHandler getGroupDataHandler() throws HttpBeanException {
        IGroupDataHandler groupDataHandler;
        try {
            groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_SERVICELOCATOR);
        }
        return groupDataHandler;
    }

    /**
     * @return An IUserDataHandler
     * @throws HttpBeanException
     */
    protected IUserDataHandler getUserDataHandler() throws HttpBeanException {
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_SERVICELOCATOR);
        }
        return userDataHandler;
    }

    /**
     * @return The identity for which the rights can be changed.
     * @throws HttpBeanException
     */
    private AccessIdentity getOwnerIdentity() throws HttpBeanException {
        if(ownerIdentity != null) {
            return ownerIdentity;
        }
        if(getOwnerId() == null) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_INVALID_REQUEST);
        }
        ownerIdentity = getUserDataHandler().getUser(getOwnerId());
        if(ownerIdentity != null) {
            return ownerIdentity;
        }
        ownerIdentity = getGroupDataHandler().getGroup(getOwnerId());
        if(ownerIdentity != null) {
            return ownerIdentity;
        }
        throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ERROR_GROUP_OR_USER_NOT_FOUND);
    }


    private void applyAccess() throws HttpBeanException {
        ensureHasAccess(getTarget(), AccessLevelEnum.OWNER);
        AccessIdentity identityWhichRightsAreToChange = getOwnerIdentity();
        ISecurityHandler securityHandler = getSecurityHandler();
        securityHandler.setAccessLevel(identityWhichRightsAreToChange, getTarget(), level);
    }

    /**
     * Create a new access
     * @throws HttpBeanException
     */
    public void createAccess() throws HttpBeanException {
        checkAccessLevel();
        if(getLevel() == AccessLevelEnum.NONE) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, "Can't add none access level");
        }
        ensureHasNoAccess(getOwnerIdentity());
        applyAccess();
    }

    public void replaceAccess() throws HttpBeanException {
        checkAccessLevel();
        applyAccess();
    }

    @Override
    protected TemplateType doPut(HttpServletRequest request) throws HttpBeanException {
        if(request == null) throw new IllegalArgumentException("request is null");
        replaceAccess();
        return TemplateType.INDEX;
    }

    @Override
    protected TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        if(request == null) throw new IllegalArgumentException("request is null");
        createAccess();
        return TemplateType.INDEX;
    }

    /**
     * Checks the field {@link #getLevel() level}
     * @throws HttpBeanException
     */
    private void checkAccessLevel() throws HttpBeanException {
        if (level != null) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_INVALID_REQUEST);
        }
    }

    private void ensureHasNoAccess(AccessIdentity identity) throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        if(!AccessLevelEnum.NONE.containsLevel(securityHandler.getAccessLevel(identity, getTarget()))) {
            throw new HttpBeanException(HttpServletResponse.SC_CONFLICT, ERROR_ACCESS_ALREADY_EXISTS);
        }
    }

    public AccessLevelEnum getLevel() {
        return level;
    }

    public void setLevel(AccessLevelEnum level) {
        this.level = level;
    }


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @param target the target to which the access belongs.
     */
    public void setTarget(T target) {
        this.target = target;
    }

    public String getTargetId() {
        if(target != null) {
            return target.getId();
        }
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
        target = null;
    }

    public T getTarget() throws HttpBeanException {
        if(target == null && targetId != null) {
            target = getTargetById(targetId);
            if(target == null) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ERROR_TARGET_NOT_FOUND);
            }
        }
        return target;
    }
}
