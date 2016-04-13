package ch.avocado.share.service;

import ch.avocado.share.model.data.*;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.List;
import java.util.Map;

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
    AccessLevelEnum getAccessLevel(AccessIdentity identity, AccessControlObjectBase target) throws DataHandlerException;

    /**
     * checks and returns what access the given user or group has on the target.
     * @param identityId identifier  of user or group to be checked
     * @param targetId identifier of target that should be accessed
     * @return access-level of the user in the target
     */
    AccessLevelEnum getAccessLevel(String identityId, String targetId) throws DataHandlerException;


    /**
     * Sets the access of the user on target to the given level.
     * @param identity owner of the new access level
     * @param target accessed object
     * @param accessLevel the new level
     * @return {@code true} if the execution was successful.
     */
    boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel) throws DataHandlerException;

    /**
     * Sets the access of the identity on the target to the given level.
     * This method should only be used if the identity or target is not retrieved.
     * @param identityId identifier of the owner of the new access level
     * @param targetId identifier of the new level
     */
    boolean setAccessLevel(String identityId, String targetId, AccessLevelEnum accessLevel) throws DataHandlerException;

    /**
     * checks and returns what access level an anonymous (unauthenticated) user
     * has to the target.
     * @param target target to check the access to
     * @return access level of an anonymous user
     */
    AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) throws DataHandlerException;


    boolean setAnonymousAccessLevel(AccessControlObjectBase object, AccessLevelEnum level) throws DataHandlerException;


    Map<String, AccessLevelEnum> getGroupsWithAccessIncluding(AccessLevelEnum accessLevel, AccessControlObjectBase target) throws DataHandlerException;

    Map<String, AccessLevelEnum> getUsersWithAccessIncluding(AccessLevelEnum accessLevel, AccessControlObjectBase target) throws DataHandlerException;

    List<String> getIdsOfObjectsOnWhichIdentityHasAccess(AccessIdentity identity, AccessLevelEnum accessLevelEnum) throws DataHandlerException;
}
