package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IMailingService;

public class MailingServiceMock implements IMailingService {

    @Override
    public boolean sendVerificationEmail(User user) {
        return true;
    }

    @Override
    public boolean sendRequestAccessEmail(User requestingUser, User owningUser, File file) {
        return true;
    }

    @Override
    public boolean sendPasswordResetEmail(User user) {
        return true;
    }

    public static void use() throws IllegalAccessException {
        ServiceLocatorModifier.setService(IMailingService.class, new MailingServiceMock());
    }
}
