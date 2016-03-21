package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IUserDataHandler;

import javax.servlet.annotation.WebServlet;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by coffeemakr on 21.03.16.
 */

@WebServlet("/group")
public class GroupBean implements Serializable {

    private static final String ERROR_NO_NAME = "Bitte einen Namen eingeben.";
    private static final String ERROR_NO_DESCRIPTION = "Bitte eine Beschreibung angeben.";
    private static final String ERROR_INTERNAL_SERVER = "Interner Server-Fehler.";
    private static final String ERROR_GROUP_NAME_ALREADY_EXISTS = "Eine Gruppe mit diesem Namen existiert bereits.";

    private String name;
    private String description;
    private String errorMessage = null;
    private User owner;
    private Group group = null;
    private String groupId;


    public boolean create() {
        IGroupDataHandler groupDataHandler;
        IUserDataHandler userDataHandler;
        if(owner == null) {
            errorMessage = ERROR_INTERNAL_SERVER;
            return false;
        }

        if (name == null || name.trim().isEmpty()) {
            errorMessage = ERROR_NO_NAME;
            return false;
        } else if (description == null || description.trim().isEmpty()) {
            errorMessage = ERROR_NO_DESCRIPTION;
            return false;
        }

        try {
            groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            errorMessage = ERROR_INTERNAL_SERVER;
            return false;
        }

        description = description.trim();
        name = name.trim();

        group = new Group(null, null, new Date(System.currentTimeMillis()), null, owner.getId(), description, name);

        if(groupDataHandler.getGroupByName(name) != null) {
            errorMessage = ERROR_GROUP_NAME_ALREADY_EXISTS;
            return false;
        }
        if(!groupDataHandler.addGroup(group)) {
            errorMessage = ERROR_INTERNAL_SERVER;
            return false;
        }
        if(!userDataHandler.addUserToGroup(owner, group)){
            errorMessage = ERROR_INTERNAL_SERVER;
            return false;
        }
        return true;
    }

    /**
     * @return null if there is no error.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOwner(User owner) {
        if(owner == null) throw new IllegalArgumentException("owner is null");
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public void setGroupId(String groupId) {
        if(groupId == null) throw new IllegalArgumentException("groupId is null");
        this.groupId = groupId;
        IGroupDataHandler groupDataHandler;
        try {
            groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return;
        }
        group = groupDataHandler.getGroup(groupId);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        if(group == null) throw new IllegalArgumentException("group is null");
        this.group = group;
    }
}
