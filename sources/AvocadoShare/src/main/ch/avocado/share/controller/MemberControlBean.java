package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This bean can be used to control (and only control) access to a AccessControlObjectBase.
 * It is not intended to display relationship.
 */
public abstract class MemberControlBean<T extends AccessControlObjectBase> extends RequestHandlerBeanBase {
    private static final String ERROR_SERVICELOCATOR = "Servicelocator error";
    private static final String ERROR_GROUP_OR_USER_NOT_FOUND = "Gruppe oder Benutzer konnte nichte gefunden werden.";
    private static final String ERROR_ACCESS_ALREADY_EXISTS = "Es existiert bereits ein Zugriffsrecht.";
    private static final String ERROR_TARGET_NOT_FOUND = "Access target not found.";
    private static final String ERROR_LEVEL_MISSING = "Parameter 'level' missing.";
    private static final String ERROR_MISSING_GROUP_OR_USER_ID = "Parameter 'groupId' oder 'userId' fehlen.";
    private static final String ERROR_BOTH_USER_AND_GROUP_ID_SET = "Es darf nur einer der Parameter 'groupId' oder 'userId' gesetzt sein.";
    private static final String ERROR_UNABLE_TO_SET_RIGHTS = "Zugriffsrechte konnten nicht gesetzt werden.";

    public static final String ACTION_CREATE_MEMBER = "create_member";
    public static final String ACTION_EDIT_MEMBER = "edit_member";


    private String userId = null;
    private String groupId = null;
    private AccessLevelEnum level = null;
    private AccessIdentity ownerIdentity = null;
    private T target;
    private String targetId;
    private String action;


    protected String getTemplateFolder() {
        return "member_templates/";
    }


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
    public AccessIdentity getOwnerIdentity() throws HttpBeanException {
        if (ownerIdentity != null) {
            return ownerIdentity;
        }
        // only the group id or the user id can be set
        if ((getUserId() == null && getGroupId() == null)) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_MISSING_GROUP_OR_USER_ID);
        }
        if((getUserId() != null && getGroupId() != null)) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_BOTH_USER_AND_GROUP_ID_SET);
        }
        if (getUserId() != null) {
            ownerIdentity = getUserDataHandler().getUser(getUserId());
            if (ownerIdentity != null) {
                return ownerIdentity;
            }
        } else {
            ownerIdentity = getGroupDataHandler().getGroup(getUserId());
            if (ownerIdentity != null) {
                return ownerIdentity;
            }
        }
        throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ERROR_GROUP_OR_USER_NOT_FOUND);
    }


    private void applyAccess() throws HttpBeanException {
        ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.OWNER);
        AccessIdentity identityWhichRightsAreToChange = getOwnerIdentity();
        ISecurityHandler securityHandler = getSecurityHandler();
        if(!securityHandler.setAccessLevel(identityWhichRightsAreToChange, getTarget(), level)) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_UNABLE_TO_SET_RIGHTS);
        }
    }

    /**
     * Create a new access
     *
     * @throws HttpBeanException
     */
    public void createAccess() throws HttpBeanException {
        checkAccessLevel();
        if (getLevel() == AccessLevelEnum.NONE) {
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
        if (request == null) throw new IllegalArgumentException("request is null");
        replaceAccess();
        return doGet(request);
    }

    @Override
    protected TemplateType doPost(HttpServletRequest request) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        createAccess();
        return doGet(request);
    }

    @Override
    protected TemplateType doGet(HttpServletRequest request) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (getLevel() == null) {
            setLevel(AccessLevelEnum.READ);
        }
        request.setAttribute("Target", getTarget());
        if (getAction() != null) {
            switch (getAction()) {
                case ACTION_CREATE_MEMBER:
                    ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.OWNER);
                    return TemplateType.CREATE;
                case ACTION_EDIT_MEMBER:
                    ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.OWNER);
                    AccessIdentity ownerIdentity = getOwnerIdentity();
                    if (ownerIdentity instanceof User) {
                        request.setAttribute("MemberUser", ownerIdentity);
                    } else if (ownerIdentity instanceof Group) {
                        request.setAttribute("MemberUser", ownerIdentity);
                    } else {
                        throw new RuntimeException("Unknown identity class");
                    }
                    return TemplateType.EDIT;
            }
        }
        ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.READ);
        request.setAttribute("MemberGroups", getMemberGroups());
        request.setAttribute("MemberUsers", getMemberUsers());
        return TemplateType.INDEX;
    }


    /**
     * @todo Should this method check if the accessing user has read rights on the identities?
     * @return a list of members with access level
     * @throws HttpBeanException
     */
    public Group[] getMemberGroups() throws HttpBeanException {
        checkAccessLevel();
        ISecurityHandler securityHandler = getSecurityHandler();
        return securityHandler.getGroupsWithAccess(getLevel(), getTarget());
    }

    /**
     * @todo Should this method check if the accessing user has read rights on the identities?
     * @return a list of members with access level
     * @throws HttpBeanException
     */
    public User[] getMemberUsers() throws HttpBeanException {
        checkAccessLevel();
        ISecurityHandler securityHandler = getSecurityHandler();
        return securityHandler.getUsersWithAccess(getLevel(), getTarget());
    }


    /**
     * Checks the field {@link #getLevel() level}
     * @throws HttpBeanException
     */
    private void checkAccessLevel() throws HttpBeanException {
        if (getLevel() == null) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ERROR_LEVEL_MISSING);
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

    public void setLevel(String level) {
        level = level.toUpperCase();
        switch (level) {
            case "NONE":
                setLevel(AccessLevelEnum.NONE);
                break;
            case "READ":
                setLevel(AccessLevelEnum.READ);
                break;
            case "WRITE":
                setLevel(AccessLevelEnum.WRITE);
                break;
            case "OWNER":
                setLevel(AccessLevelEnum.OWNER);
                break;
            default:
                throw new IllegalArgumentException("Unknown level");
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        }
        if(target == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ERROR_TARGET_NOT_FOUND);
        }
        return target;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
