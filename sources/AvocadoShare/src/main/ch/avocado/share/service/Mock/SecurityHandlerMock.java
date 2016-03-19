package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.ISecurityHandler;

/**
 * Created by bergm on 19/03/2016.
 */
public class SecurityHandlerMock implements ISecurityHandler {
    @Override
    public AccessLevelEnum getAccessLevel(User user, AccessControlObjectBase target) {
        return null;
    }
}
