package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;

/**
 * Created by bergm on 15/03/2016.
 */
public interface ISecurityHandler {

    /**
     * checks and returns what access the given user has on the target.
     * @param user user to be checked
     * @param target target that should be accessed
     * @return access-level of the user in the target
     */
    AccessLevelEnum getAccessLevel(User user, AccessControlObjectBase target);

    /**
     * checks and returns what access level an anonymous (unauthenticated) user
     * has to the target.
     * @param target target to check the access to
     * @return access level of an anonymous user
     */
    AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target);
}
