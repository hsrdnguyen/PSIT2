package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceException;

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

    private void checkNameNotEmpty(Group group) {
        if (name == null || name.trim().isEmpty()) {
            group.addFieldError("name", ErrorMessageConstants.ERROR_NO_NAME);
        } else {
            name = name.trim();
        }
    }

    private void checkNameIsUnique(Group group) throws ServiceNotFoundException, DataHandlerException {
        try {
            getService(IGroupDataHandler.class).getGroupByName(name);
            group.setName(name);
            group.addFieldError("name", ErrorMessageConstants.ERROR_GROUP_NAME_ALREADY_EXISTS);
        } catch (ObjectNotFoundException ignored) {
            // The name is unique
        }
    }

    @Override
    public boolean hasIdentifier() {
        return name != null || getId() != null;
    }

    @Override
    public Group create() throws DataHandlerException, ServiceNotFoundException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        Group group = new Group(null, null, new Date(System.currentTimeMillis()), new Rating(), getAccessingUser().getId(), "", "");
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
    public Group get() throws ServiceException {
        if (!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        Group group = null;
        if (getId() != null) {
            group = groupDataHandler.getGroup(getId());
        } else if (name != null) {
            group = groupDataHandler.getGroupByName(name);
        }
        assert group != null;
        return group;
    }

    @Override
    public List<Group> index() throws ServiceException {
        ISecurityHandler securityHandler = getService(ISecurityHandler.class);
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        if (getAccessingUser() != null) {
            return groupDataHandler.getGroups(securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(getAccessingUser(), AccessLevelEnum.READ));
        }
        return new ArrayList<>();
    }

    @Override
    public void update(Group group) throws ServiceException {
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
            groupDataHandler.updateGroup(group);
        }
    }

    @Override
    public void destroy(Group group) throws DataHandlerException, ServiceNotFoundException, ObjectNotFoundException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        groupDataHandler.deleteGroup(group);
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
