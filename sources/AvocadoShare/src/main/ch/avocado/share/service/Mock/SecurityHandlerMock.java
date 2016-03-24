package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.AccessIdentity;
import ch.avocado.share.service.ISecurityHandler;

/**
 * Created by bergm on 19/03/2016.
 */
public class SecurityHandlerMock implements ISecurityHandler {

    @Override
    public AccessLevelEnum getAccessLevel(AccessIdentity identity, AccessControlObjectBase target) {
        return null;
    }

    @Override
    public boolean setAccessLevel(AccessIdentity identity, AccessControlObjectBase target, AccessLevelEnum accessLevel) {
        return false;
    }

    @Override
    public AccessLevelEnum getAnonymousAccessLevel(AccessControlObjectBase target) {
        return AccessLevelEnum.NONE;
    }
}
