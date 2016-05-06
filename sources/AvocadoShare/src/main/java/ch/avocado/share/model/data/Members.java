package ch.avocado.share.model.data;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Members {
    private final Map<User, AccessLevelEnum> users;
    private final Map<Group, AccessLevelEnum> groups;
    private final AccessControlObjectBase target;

    public Members(AccessControlObjectBase target) {
        this(new HashMap<User, AccessLevelEnum>(), new HashMap<Group, AccessLevelEnum>(), target);
    }

    public Members(Map<User, AccessLevelEnum> users, Map<Group, AccessLevelEnum> groups, AccessControlObjectBase target) {
        if(users == null) throw new NullPointerException("users is null");
        if(groups == null) throw new NullPointerException("groups is null");
        if(target == null) throw new NullPointerException("target is null");
        this.users = users;
        this.groups = groups;
        this.target = target;
    }

    static public Members fromIdsWithRights(Map<String, AccessLevelEnum> userIdsWithRights,
                                            Map<String, AccessLevelEnum> groupIdsWithRights,
                                            AccessControlObjectBase target)
            throws ServiceNotFoundException, DataHandlerException {
        if(target == null) throw new NullPointerException("target is null");
        if (userIdsWithRights == null) throw new NullPointerException("userIdsWithRights is null");
        if (groupIdsWithRights == null) throw new NullPointerException("groupIdsWithRights is null");

        Map<User, AccessLevelEnum> usersWithRights = new HashMap<>();
        Map<Group, AccessLevelEnum> groupsWithRights = new HashMap<>();

        if (!userIdsWithRights.isEmpty()) {
            List<User> userList = ServiceLocator.getService(IUserDataHandler.class).getUsers(userIdsWithRights.keySet());
            for (User user : userList) {
                AccessLevelEnum level = userIdsWithRights.get(user.getId());
                usersWithRights.put(user, level);
            }
        }

        if (!groupIdsWithRights.isEmpty()) {
            List<Group> groupList = ServiceLocator.getService(IGroupDataHandler.class).getGroups(groupIdsWithRights.keySet());
            for (Group group : groupList) {
                AccessLevelEnum level = userIdsWithRights.get(group.getId());
                groupsWithRights.put(group, level);
            }
        }
        return new Members(usersWithRights, groupsWithRights, target);
    }

    public Iterable<User> getUsers() {
        return users.keySet();
    }

    public Iterable<Group> getGroups() {
        return groups.keySet();
    }

    public HashMap<User, AccessLevelEnum> getUsersWithAccess() {
        return new HashMap<>(users);
    }

    public HashMap<Group, AccessLevelEnum> getGroupsWithAccess() {
        return new HashMap<>(groups);
    }

    public AccessControlObjectBase getTarget() {
        return target;
    }

    public Map<AccessIdentity, AccessLevelEnum> getIdentitiesWithAccess() {
        Map<AccessIdentity, AccessLevelEnum> map = new HashMap<>();
        map.putAll(users);
        map.putAll(groups);
        return map;
    }

    public Iterable<AccessIdentity> getIdentities() {
        List<AccessIdentity> list = new ArrayList<>(groups.size() + users.size());
        list.addAll(groups.keySet());
        list.addAll(users.keySet());
        return list;
    }
}
