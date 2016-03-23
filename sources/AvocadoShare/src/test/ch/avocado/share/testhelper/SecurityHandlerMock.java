package ch.avocado.share.testhelper;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;

import java.util.HashMap;
import java.util.Map;


public class SecurityHandlerMock implements ISecurityHandler {

    private Map<String, AccessLevelEnum> usersWithAccess;
    private AccessLevelEnum anonymousAccess;

    public SecurityHandlerMock() {
        anonymousAccess = AccessLevelEnum.NONE;
        usersWithAccess = new HashMap<>();
        usersWithAccess.put(UserDataHandlerMock.EXISTING_USER0, AccessLevelEnum.READ);
        usersWithAccess.put(UserDataHandlerMock.EXISTING_USER1, AccessLevelEnum.WRITE);
        usersWithAccess.put(UserDataHandlerMock.EXISTING_USER2, AccessLevelEnum.OWNER);
        usersWithAccess.put(UserDataHandlerMock.EXISTING_USER3, AccessLevelEnum.NONE);
    }

    @Override
    public AccessLevelEnum getAccessLevel(User user, AccessControlObjectBase target) {
        String userId = user.getId();
        if(usersWithAccess.containsKey(userId)) {
            return usersWithAccess.get(userId);
        }
        return AccessLevelEnum.NONE;
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) {
        return getAnonymousAccess();
    }

    public User getUserWithAccess(AccessLevelEnum level) throws ServiceNotFoundException {
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        for(Map.Entry<String, AccessLevelEnum> entry: usersWithAccess.entrySet()) {
            if(entry.getValue() == level) {
                User user = userDataHandler.getUser(entry.getKey());
                if(user == null) {
                    throw new RuntimeException("User with access doesn't exist");
                }
                return user;
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
