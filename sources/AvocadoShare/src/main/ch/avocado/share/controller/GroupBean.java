package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        try {
            if (getService(IGroupDataHandler.class).getGroupByName(name) != null) {
                addFormError("name", ErrorMessageConstants.ERROR_GROUP_NAME_ALREADY_EXISTS);
            }
        } catch (DataHandlerException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ErrorMessageConstants.ERROR_DATABASE);
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
    public Group create() throws HttpBeanException, DataHandlerException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        checkNameNotEmpty();
        checkNameIsUnique();
        checkParameterDescription();
        if (!hasErrors()) {
            Group group = new Group(null, null, new Date(System.currentTimeMillis()), 0, getAccessingUser().getId(), description, name);
            groupDataHandler.addGroup(group);
            return group;
        }
        // TODO: return group even when parameters were incorrect
        return null;
    }

    @Override
    public Group get() throws HttpBeanException, DataHandlerException {
        if (!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        Group group = null;
        if (getId() != null) {
            group = groupDataHandler.getGroup(getId());
        } else if (name != null) {
            group = groupDataHandler.getGroupByName(name);
        }
        if (group == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_GROUP);
        }
        return group;
    }

    @Override
    public List<Group> index() throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        List<Group> groupList;
        if (getAccessingUser() != null) {
            try {
                groupList = groupDataHandler.getGroups(securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(getAccessingUser(), AccessLevelEnum.READ));
            } catch (DataHandlerException e) {
                throw new HttpBeanDatabaseException();
            }
            return groupList;
        }
        return new ArrayList<Group>();
    }

    @Override
    public void update() throws HttpBeanException, DataHandlerException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        checkParameterDescription();
        checkNameNotEmpty();
        Group group = getObject();
        if (!hasErrors()) {
            if (!name.equals(group.getName())) {
                checkNameIsUnique();
            }
        }
        if (!hasErrors()) {
            group.setName(name);
            group.setDescription(description);
            if (!groupDataHandler.updateGroup(group)) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_GROUP);
            }
        }
    }

    @Override
    public void destroy() throws HttpBeanException, DataHandlerException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        if (!groupDataHandler.deleteGroup(getObject())) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_GROUP);
        }
    }

    @Override
    public String getAttributeName() {
        return "Group";
    }

    /**
     * Get group description
     *
     * @return The description of the group
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the group description
     *
     * @param description The description of the group
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the group name
     *
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

}
