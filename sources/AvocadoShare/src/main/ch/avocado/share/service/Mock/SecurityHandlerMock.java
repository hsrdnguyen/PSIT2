package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock for Security handler. This class makes use of {@link UserDataHandlerMock} and {@link GroupDataHandlerMock}
 * so you should make sure these two handlers are installed as well.
 */
public class SecurityHandlerMock implements ISecurityHandler {

    private Map<String, AccessLevelEnum> identityWithAccess;
    private AccessLevelEnum anonymousAccess;

    public SecurityHandlerMock() {
        anonymousAccess = AccessLevelEnum.NONE;
        identityWithAccess = new HashMap<>();
        identityWithAccess.put(UserDataHandlerMock.EXISTING_USER0, AccessLevelEnum.READ);
        identityWithAccess.put(UserDataHandlerMock.EXISTING_USER1, AccessLevelEnum.WRITE);
        identityWithAccess.put(UserDataHandlerMock.EXISTING_USER2, AccessLevelEnum.OWNER);
        identityWithAccess.put(UserDataHandlerMock.EXISTING_USER3, AccessLevelEnum.NONE);
        identityWithAccess.put(GroupDataHandlerMock.EXISTING_GROUP0, AccessLevelEnum.READ);
        identityWithAccess.put(GroupDataHandlerMock.EXISTING_GROUP1, AccessLevelEnum.WRITE);
        identityWithAccess.put(GroupDataHandlerMock.EXISTING_GROUP2, AccessLevelEnum.OWNER);
        identityWithAccess.put(GroupDataHandlerMock.EXISTING_GROUP3, AccessLevelEnum.NONE);
    }


    @Override
    public AccessLevelEnum getAccessLevel(AccessIdentity identity, AccessControlObjectBase target) {
        if(identity == null) throw new IllegalArgumentException("identity is null");
        if(target == null) throw new IllegalArgumentException("target is null");
        if(identity.getId().equals(target.getId())) {
            return AccessLevelEnum.NONE;
        }
        String identityId = identity.getId();
        if(identityWithAccess.containsKey(identityId)) {
            return identityWithAccess.get(identityId);
        }
        return AccessLevelEnum.NONE;
    }

    @Override
    public boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel) {
        if(identity == null) throw new IllegalArgumentException("identity is null");
        if(accessLevel == null) throw new IllegalArgumentException("accessLevel is null");
        identityWithAccess.put(identity.getId(), accessLevel);
        return true;
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) {
        return getAnonymousAccess();
    }

    @Override
    public Group[] getGroupsWithAccess(AccessLevelEnum accessLevelEnum, AccessControlObjectBase target) {
        if(accessLevelEnum == null) throw new IllegalArgumentException("accessLevelEnum is null");
        if(target == null) throw new IllegalArgumentException("target is null");
        List<Group> identityList = new ArrayList<>();
        IGroupDataHandler groupDataHandler;
        try {
            groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        for (Map.Entry<String, AccessLevelEnum> entry: identityWithAccess.entrySet()) {
            if(entry.getValue().containsLevel(accessLevelEnum)) {
                Group group = null;
                try {
                    group = groupDataHandler.getGroup(entry.getKey());
                } catch (DataHandlerException ignored) {
                }
                if(group != null && !group.getId().equals(target.getId())) {
                    identityList.add(group);
                }
            }
        }
        Group[] groups = new Group[identityList.size()];
        return identityList.toArray(groups);
    }

    @Override
    public User[] getUsersWithAccessIncluding(AccessLevelEnum accessLevelEnum, AccessControlObjectBase target) throws DataHandlerException {
        if(accessLevelEnum == null) throw new IllegalArgumentException("accessLevelEnum is null");
        if(target == null) throw new IllegalArgumentException("target is null");
        List<User> identityList = new ArrayList<>();
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        for (Map.Entry<String, AccessLevelEnum> entry: identityWithAccess.entrySet()) {
            if(entry.getValue().containsLevel(accessLevelEnum)) {
                User user = userDataHandler.getUser(entry.getKey());
                if(user != null && !user.getId().equals(target.getId())) {
                    identityList.add(user);
                }
            }
        }
        User[] users = new User[identityList.size()];
        return identityList.toArray(users);
    }

    private Group[] getAllGroups() {
        GroupDataHandlerMock groupDataHandler;
        try{
            groupDataHandler = (GroupDataHandlerMock) ServiceLocator.getService(IGroupDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        return groupDataHandler.getAllGroups();
    }

    private User[] getAllUsers() {
        UserDataHandlerMock userDataHandler;
        try{
            userDataHandler = (UserDataHandlerMock) ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        return userDataHandler.getAllUsers();
    }

    private Module[] getAllModules() {
        ModuleDataHandlerMock moduleDataHandler;
        try {
            moduleDataHandler = (ModuleDataHandlerMock) ServiceLocator.getService(IModuleDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        return moduleDataHandler.getAllModules();
    }

    public Object getAllFiles() {
        FileDataHandlerMock fileDataHandler;
        try {
            fileDataHandler = (FileDataHandlerMock) ServiceLocator.getService(IFileDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        return fileDataHandler.getAll(File.class);
    }

    @Override
    public <I extends AccessControlObjectBase> I[] getObjectsOnWhichIdentityHasAccessLevel(Class<I> clazz, AccessIdentity identity, AccessLevelEnum accessLevelEnum) {
        if(clazz == null) throw new IllegalArgumentException("clazz can't be null");
        if(identity == null) throw new IllegalArgumentException("identity can't be null");
        if(accessLevelEnum == null) throw new IllegalArgumentException("accessLevelEnum can't be null");
        AccessLevelEnum grantedLevel = identityWithAccess.get(identity.getId());
        if(grantedLevel == null || !grantedLevel.containsLevel(accessLevelEnum)) {
            return (I[]) new AccessControlObjectBase[0];
        }
        if(clazz.equals(Group.class)) {
            return (I[]) getAllGroups();
        }else if(clazz.equals(User.class)) {
            return (I[]) getAllUsers();
        }else if(clazz.equals(Module.class)) {
            return (I[]) getAllModules();
        }else if(clazz.equals(File.class)) {
            return (I[]) getAllFiles();
        }
        return null;
    }

    /**
     * @param level the level of access the group should have
     * @return A group object which the required access level to all objects.
     * @throws ServiceNotFoundException
     */
    public Group getGroupWithAccess(AccessLevelEnum level) throws ServiceNotFoundException, DataHandlerException {
        if(level == null) throw new IllegalArgumentException("level is null");
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        for(Map.Entry<String, AccessLevelEnum> entry: identityWithAccess.entrySet()) {
            if(entry.getValue() == level) {
                Group group = groupDataHandler.getGroup(entry.getKey());
                if(group != null) {
                    return group;
                }
            }
        }
        throw new RuntimeException("No user with access found");
    }

    /**
     * @param level the level of access the user should have
     * @return A user object which the required access level to all objects.
     * @throws ServiceNotFoundException
     */
    public User getUserWithAccess(AccessLevelEnum level) throws ServiceNotFoundException, DataHandlerException {
        if(level == null) throw new IllegalArgumentException("level is null");
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        for(Map.Entry<String, AccessLevelEnum> entry: identityWithAccess.entrySet()) {
            if(entry.getValue() == level) {
                User user = userDataHandler.getUser(entry.getKey());
                if(user != null) {
                    return user;
                }
            }
        }
        throw new RuntimeException("No user with access found");
    }

    /**
     * @return The anonymous access level to all objects
     */
    public AccessLevelEnum getAnonymousAccess() {
        return anonymousAccess;
    }
    /**
     * @param anonymousAccess The anonymous access level to all objects
     */
    public void setAnonymousAccess(AccessLevelEnum anonymousAccess) {
        if(anonymousAccess == null) throw new IllegalArgumentException("anonymousAccess is null");
        this.anonymousAccess = anonymousAccess;
    }

    static public void use() throws Exception {
        UserDataHandlerMock.use();
        GroupDataHandlerMock.use();
        ModuleDataHandlerMock.use();
        if(!ServiceLocator.getService(ISecurityHandler.class).getClass().equals(SecurityHandlerMock.class)) {
            ServiceLocatorModifier.setService(ISecurityHandler.class, new SecurityHandlerMock());
        }
    }


}
