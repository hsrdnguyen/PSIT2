package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This bean can be used to control (and only control) access to a AccessControlObjectBase.
 * It is not intended to display relationship.
 */
public abstract class MemberControlBean<T extends AccessControlObjectBase> extends RequestHandlerBeanBase {

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
     * @return The identity for which the rights can be changed.
     * @throws HttpBeanException
     */
    public AccessIdentity getOwnerIdentity() throws HttpBeanException {
        if (ownerIdentity != null) {
            return ownerIdentity;
        }
        // only the group id or the user id can be set
        if ((getUserId() == null && getGroupId() == null)) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ErrorMessageConstants.ERROR_MISSING_GROUP_OR_USER_ID);
        }
        if ((getUserId() != null && getGroupId() != null)) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ErrorMessageConstants.ERROR_BOTH_USER_AND_GROUP_ID_SET);
        }
        if (getUserId() != null) {
            try {
                ownerIdentity = getService(IUserDataHandler.class).getUser(getUserId());
            } catch (DataHandlerException e) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
            if (ownerIdentity != null) {
                return ownerIdentity;
            }
        } else {
            try {
                ownerIdentity = getService(IGroupDataHandler.class).getGroup(getGroupId());
            } catch (DataHandlerException e) {
                throw new HttpBeanDatabaseException();
            }
            if (ownerIdentity != null) {
                return ownerIdentity;
            }
        }
        throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_GROUP_OR_USER_NOT_FOUND);
    }


    private void applyAccess() throws HttpBeanException {
        ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.MANAGE);
        AccessIdentity identityWhichRightsAreToChange = getOwnerIdentity();
        ISecurityHandler securityHandler = getSecurityHandler();
        T target = getTarget();
        if (target.getId().equals(identityWhichRightsAreToChange.getId())) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, "Besitzer der Rechte und Ziel sind gleich.");
        }
        try {
            if (!securityHandler.setAccessLevel(identityWhichRightsAreToChange, target, level)) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_UNABLE_TO_SET_RIGHTS);
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
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
                    System.out.println(ACTION_CREATE_MEMBER);
                    ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.MANAGE);
                    return TemplateType.CREATE;

                case ACTION_EDIT_MEMBER:
                    System.out.println(ACTION_EDIT_MEMBER);
                    ensureAccessingUserHasAccess(getTarget(), AccessLevelEnum.MANAGE);
                    AccessIdentity ownerIdentity = getOwnerIdentity();
                    if (ownerIdentity instanceof User) {
                        request.setAttribute("MemberUser", ownerIdentity);
                    } else if (ownerIdentity instanceof Group) {
                        request.setAttribute("MemberGroup", ownerIdentity);
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
     * @return a list of members with access level
     * @throws HttpBeanException
     * @todo Should this method check if the accessing user has read rights on the identities?
     */
    public Group[] getMemberGroups() throws HttpBeanException {
        checkAccessLevel();
        ISecurityHandler securityHandler = getSecurityHandler();
        try {
            return securityHandler.getGroupsWithAccess(getLevel(), getTarget());
        } catch (DataHandlerException e) {
            throw new HttpBeanDatabaseException();
        }
    }

    /**
     * @return a list of members with access level
     * @throws HttpBeanException
     * @todo Should this method check if the accessing user has read rights on the identities?
     */
    public User[] getMemberUsers() throws HttpBeanException {
        checkAccessLevel();
        ISecurityHandler securityHandler = getSecurityHandler();
        try {
            return securityHandler.getUsersWithAccessIncluding(getLevel(), getTarget());
        } catch (DataHandlerException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
        }
    }


    /**
     * Checks the field {@link #getLevel() level}
     *
     * @throws HttpBeanException
     */
    private void checkAccessLevel() throws HttpBeanException {
        if (getLevel() == null) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ErrorMessageConstants.ERROR_LEVEL_MISSING);
        }
    }

    private void ensureHasNoAccess(AccessIdentity identity) throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        try {
            if (!AccessLevelEnum.NONE.containsLevel(securityHandler.getAccessLevel(identity, getTarget()))) {
                throw new HttpBeanException(HttpServletResponse.SC_CONFLICT, ErrorMessageConstants.ERROR_ACCESS_ALREADY_EXISTS);
            }
        } catch (DataHandlerException e) {
            throw new HttpBeanDatabaseException();
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
            case "MANAGE":
                setLevel(AccessLevelEnum.MANAGE);
                break;
            case "OWNER":
                setLevel(AccessLevelEnum.OWNER);
                break;
            default:
                throw new IllegalArgumentException("Unknown level");
        }
    }

    /**
     * @param target the target to which the access belongs.
     */
    public void setTarget(T target) {
        this.target = target;
    }

    public String getTargetId() {
        if (target != null) {
            return target.getId();
        }
        return targetId;
    }

    public void setTargetId(String targetId) {
        if (this.targetId == null || !this.targetId.equals(targetId)) {
            this.targetId = targetId;
            target = null;
        }
    }

    /**
     * @return The target on which access will be changed or created
     * @throws HttpBeanException
     */
    public T getTarget() throws HttpBeanException {
        if (target == null && targetId != null) {
            target = getTargetById(targetId);
        }
        if (target == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_TARGET_NOT_FOUND);
        }
        return target;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the id of the user whose rights will be changed
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the id of the user whose rights will be changed
     */
    public void setUserId(String userId) {
        if (this.userId == null || !this.userId.equals(userId)) {
            ownerIdentity = null;
            this.userId = userId;
            groupId = null;
        }
    }

    /**
     * @return the id of the group whose rights will be changed
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the id of the group whose rights will be changed
     */
    public void setGroupId(String groupId) {
        if (this.groupId == null || !userId.equals(groupId)) {
            ownerIdentity = null;
            this.groupId = groupId;
            userId = null;
        }
    }
}
