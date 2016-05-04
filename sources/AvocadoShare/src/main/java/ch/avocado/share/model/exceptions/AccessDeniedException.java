package ch.avocado.share.model.exceptions;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;

/**
 * Created by coffeemakr on 04.05.16.
 */
public class AccessDeniedException extends Exception{
    private final AccessControlObjectBase target;
    private final User user;
    private final AccessLevelEnum requiredLevel;

    public AccessDeniedException(User user, AccessControlObjectBase target, AccessLevelEnum requiredLevel) {
        this.target = target;
        this.user = user;
        this.requiredLevel = requiredLevel;
    }
}
