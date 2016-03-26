package ch.avocado.share.service;

import ch.avocado.share.model.data.*;

/**
 * Created by bergm on 15/03/2016.
 */
public interface ISecurityHandler {

    /**
     * checks and returns what access the given user or group has on the target.
     * @param identity user or group to be checked
     * @param target target that should be accessed
     * @return access-level of the user in the target
     */
    AccessLevelEnum getAccessLevel(AccessIdentity identity, AccessControlObjectBase target);


    /**
     * Sets the access from the user on target to the given level.
     * @param identity owner of the new access level
     * @param target accessed object
     * @param accessLevel the new level
     * @return {@code true} if the execution was successful.
     */
    boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel);

    /**
     * checks and returns what access level an anonymous (unauthenticated) user
     * has to the target.
     * @param target target to check the access to
     * @return access level of an anonymous user
     */
    AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target);

    Group[] getGroupsWithAccess(AccessLevelEnum accessLevel, AccessControlObjectBase target);

    User[] getUsersWithAccess(AccessLevelEnum accessLevel, AccessControlObjectBase target);

    <I extends AccessControlObjectBase> I[] getObjectsOnWithIdentityHasAccessLevel(Class<I> clazz, AccessIdentity identity, AccessLevelEnum accessLevelEnum);
}
