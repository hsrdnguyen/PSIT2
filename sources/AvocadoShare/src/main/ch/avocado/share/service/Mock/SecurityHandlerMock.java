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
            return AccessLevelEnum.MANAGE;
        }
        String identityId = identity.getId();
        if(identityWithAccess.containsKey(identityId)) {
            return identityWithAccess.get(identityId);
        }
        return AccessLevelEnum.NONE;
    }

    @Override
    public AccessLevelEnum getAccessLevel(String identityId, String targetId) throws DataHandlerException {
        return identityWithAccess.get(identityId);
    }

    @Override
    public boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel) {
        if(identity == null) throw new IllegalArgumentException("identity is null");
        if(accessLevel == null) throw new IllegalArgumentException("accessLevel is null");
        identityWithAccess.put(identity.getId(), accessLevel);
        return true;
    }

    @Override
    public boolean setAccessLevel(String identityId, String targetId, AccessLevelEnum accessLevel) throws DataHandlerException {
        // TODO: check params
        identityWithAccess.put(identityId, accessLevel);
        return true;
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) {
        return getAnonymousAccess();
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(String targetId) throws DataHandlerException {
        return getAnonymousAccess();
    }

    @Override
    public boolean setAnonymousAccessLevel(AccessControlObjectBase object, AccessLevelEnum level) throws DataHandlerException {
        anonymousAccess = level;
        return true;
    }

    @Override
    public Map<String, AccessLevelEnum> getGroupsWithAccessIncluding(AccessLevelEnum accessLevelEnum, AccessControlObjectBase target) throws DataHandlerException {
        if(accessLevelEnum == null) throw new IllegalArgumentException("accessLevelEnum is null");
        if(target == null) throw new IllegalArgumentException("target is null");
        Map<String, AccessLevelEnum> identityList = new HashMap<>();
        IGroupDataHandler groupDataHandler;
        try {
            groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        for (Map.Entry<String, AccessLevelEnum> entry: identityWithAccess.entrySet()) {
            if(entry.getValue().containsLevel(accessLevelEnum)) {
                AccessIdentity identity = groupDataHandler.getGroup(entry.getKey());
                if(identity != null && !identity.getId().equals(target.getId())) {
                    identityList.put(identity.getId(), entry.getValue());
                }
            }
        }
        return identityList;
    }

    public Map<String, AccessLevelEnum> getUsersWithAccessIncluding(AccessLevelEnum accessLevelEnum, AccessControlObjectBase target) throws DataHandlerException {
        if(accessLevelEnum == null) throw new IllegalArgumentException("accessLevelEnum is null");
        if(target == null) throw new IllegalArgumentException("target is null");
        Map<String, AccessLevelEnum> identityList = new HashMap<>();
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        for (Map.Entry<String, AccessLevelEnum> entry: identityWithAccess.entrySet()) {
            if(entry.getValue().containsLevel(accessLevelEnum)) {
                AccessIdentity identity = userDataHandler.getUser(entry.getKey());
                if(identity != null && !identity.getId().equals(target.getId())) {
                    identityList.put(identity.getId(), entry.getValue());
                }
            }
        }
        return identityList;
    }

    @Override
    public List<String> getIdsOfObjectsOnWhichIdentityHasAccess(AccessIdentity identity, AccessLevelEnum accessLevelEnum) throws DataHandlerException {
        List<String> ids = new ArrayList<>(500);
        if(identityWithAccess.containsKey(identity.getId())) {
            for(Group group: getAllGroups()) {
                ids.add(group.getId());
            }
            for(User user: getAllUsers()) {
                ids.add(user.getId());
            }
            for(Module user: getAllModules()) {
                ids.add(user.getId());
            }
            for(File user: getAllFiles()) {
                ids.add(user.getId());
            }
        }
        return ids;
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

    public File[] getAllFiles() {
        FileDataHandlerMock fileDataHandler;
        try {
            fileDataHandler = (FileDataHandlerMock) ServiceLocator.getService(IFileDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
        return fileDataHandler.getAll(File.class);
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
