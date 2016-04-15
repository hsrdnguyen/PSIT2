package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
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

    @Override
    protected boolean hasMembers() {
        return true;
    }

    @Override
    protected String getTemplateFolder() {
        return "group_templates/";
    }

    private void checkNameNotEmpty(Group group) {
        if (name == null || name.trim().isEmpty()) {
            group.addFieldError("name", ErrorMessageConstants.ERROR_NO_NAME);
        } else {
            name = name.trim();
        }
    }

    private void checkNameIsUnique(Group group) throws HttpBeanException {
        try {
            if (getService(IGroupDataHandler.class).getGroupByName(name) != null) {
                group.setName(name);
                group.addFieldError("name", ErrorMessageConstants.ERROR_GROUP_NAME_ALREADY_EXISTS);
            }
        } catch (DataHandlerException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ErrorMessageConstants.DATAHANDLER_EXPCEPTION);
        }
    }

    @Override
    public boolean hasIdentifier() {
        return name != null || getId() != null;
    }

    @Override
    public Group create() throws HttpBeanException, DataHandlerException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        Group group = new Group(null, null, new Date(System.currentTimeMillis()), 0, getAccessingUser().getId(), "", "");
        checkNameNotEmpty(group);
        checkNameIsUnique(group);
        checkParameterDescription(group);
        if (group.isValid()) {
            group.setName(name);
            group.setDescription(description);
            groupDataHandler.addGroup(group);
        }
        return group;
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
                e.printStackTrace();
                throw new HttpBeanDatabaseException();
            }
            return groupList;
        }
        return new ArrayList<>();
    }

    @Override
    public void update(Group group) throws HttpBeanException, DataHandlerException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        checkParameterDescription(group);
        checkNameNotEmpty(group);
        if (group.isValid()) {
            if (!name.equals(group.getName())) {
                checkNameIsUnique(group);
            }
        }
        if (group.isValid()) {
            group.setName(name);
            group.setDescription(description);
            System.out.println("Updating: name: " + name + " description: " + description);
            if (!groupDataHandler.updateGroup(group)) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_GROUP);
            }
        }
    }

    @Override
    public void destroy(Group group) throws HttpBeanException, DataHandlerException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        if (!groupDataHandler.deleteGroup(group)) {
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
        System.out.println("Got description : " + description);
        this.description = description;
    }

    /**
     * Set the group name
     *
     * @param name
     */
    public void setName(String name) {
        System.out.println("Got name : " + name);
        this.name = name;
    }

    /**
     * @return The group name
     */
    public String getName() {
        return name;
    }

}
