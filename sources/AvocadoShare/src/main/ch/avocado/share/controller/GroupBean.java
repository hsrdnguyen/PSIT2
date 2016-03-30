package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * GroupBean is used to create, change and delete {@link Group}s.
 */
public class GroupBean extends ResourceBean<Group> {

    private String name;
    private String description;


    private void checkNameNotEmpty() {
        if (name == null || name.trim().isEmpty()) {
            addFormError("name", ErrorMessageConstants.ERROR_NO_NAME);
        } else {
            name = name.trim();
        }
    }

    private void checkNameIsUnique() throws HttpBeanException {
        IGroupDataHandler groupDataHandler = getGroupDataHandler();
        if (groupDataHandler.getGroupByName(name) != null) {
            addFormError("name", ErrorMessageConstants.ERROR_GROUP_NAME_ALREADY_EXISTS);
        }
    }

    private void checkParameterDescription() {
        if (description == null || description.trim().isEmpty()) {
            addFormError("description", ErrorMessageConstants.ERROR_NO_DESCRIPTION);
        } else {
            description = description.trim();
        }
    }


    @Override
    protected boolean hasIdentifier() {
        return name != null || getId() != null;
    }


    @Override
    public Group create() throws HttpBeanException {
        IGroupDataHandler groupDataHandler = getGroupDataHandler();
        // IUserDataHandler userDataHandler = getUserDataHandler();
        checkNameNotEmpty();
        checkNameIsUnique();
        checkParameterDescription();
        if (!hasErrors()) {
            Group group = new Group(null, null, new Date(System.currentTimeMillis()), 0, getAccessingUser().getId(), description, name);
            String newGroupId = groupDataHandler.addGroup(group);
            if (newGroupId == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
            group = groupDataHandler.getGroup(newGroupId);
            if (group == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
            return group;
        }
        // TODO: return group even when parameters were incorrect
        return null;
    }

    @Override
    public Group get() throws HttpBeanException {
        if(!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IGroupDataHandler groupDataHandler = getGroupDataHandler();
        Group group = null;
        if(getId() != null) {
            group = groupDataHandler.getGroup(getId());
        }else if (name != null) {
            group = groupDataHandler.getGroupByName(name);
        }

        if (group == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_GROUP);
        }
        return group;
    }

    @Override
    public Group[] index() throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        if(getAccessingUser() != null) {
            return securityHandler.getObjectsOnWhichIdentityHasAccessLevel(Group.class, getAccessingUser(), AccessLevelEnum.READ);
        }
        return new Group[0];
    }

    @Override
    public void update() throws HttpBeanException {
        IGroupDataHandler groupDataHandler = getGroupDataHandler();
        checkParameterDescription();
        checkNameNotEmpty();
        Group group = getObject();
        if (!hasErrors()) {
            if (!name.equals(group.getName())) {
                checkNameIsUnique();
            }
        }
        if(!hasErrors()) {
            group.setName(name);
            group.setDescription(description);
            if (!groupDataHandler.updateGroup(group)) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
        }
    }

    @Override
    public void destroy() throws HttpBeanException {
        IGroupDataHandler groupDataHandler = getGroupDataHandler();
        if (!groupDataHandler.deleteGroup(getObject())) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
        }
    }

    @Override
    public String getAttributeName() {
        return "Group";
    }

    /**
     * Get group description
     * @return The description of the group
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the group description
     * @param description The description of the group
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the group name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The group name
     */
    public String getName() {
        return name;
    }

    private IUserDataHandler getUserDataHandler() throws HttpBeanException {
        IUserDataHandler userDataHandler = null;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_INTERNAL_SERVER);
        }
        return userDataHandler;
    }

    private IGroupDataHandler getGroupDataHandler() throws HttpBeanException {
        IGroupDataHandler groupDataHandler = null;
        try {
            groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_INTERNAL_SERVER);
        }
        return groupDataHandler;
    }

}
