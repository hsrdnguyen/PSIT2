package ch.avocado.share.testhelper;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.util.HashMap;
import java.util.Map;


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
        String identityId = identity.getId();
        if(identityWithAccess.containsKey(identityId)) {
            return identityWithAccess.get(identityId);
        }
        return AccessLevelEnum.NONE;
    }

    @Override
    public boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel) {
        return false;
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) {
        return getAnonymousAccess();
    }

    /**
     * @param level the level of access the group should have
     * @return A group object which the required access level to all objects.
     * @throws ServiceNotFoundException
     */
    public Group getGroupWithAccess(AccessLevelEnum level) throws ServiceNotFoundException {
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
    public User getUserWithAccess(AccessLevelEnum level) throws ServiceNotFoundException {
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


    public AccessLevelEnum getAnonymousAccess() {
        return anonymousAccess;
    }

    public void setAnonymousAccess(AccessLevelEnum anonymousAccess) {
        this.anonymousAccess = anonymousAccess;
    }
}
