package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;

/**
 * Created by bergm on 15/03/2016.
 */
public interface ISecurityHandler {

    /**
     * checks and returns what access the given user has on the target
     * @param user user to be checked
     * @param target target that should be accessed
     * @return access-level of the user in the target
     */
    AccessLevelEnum getAccessLevel(User user, AccessControlObjectBase target);

}
