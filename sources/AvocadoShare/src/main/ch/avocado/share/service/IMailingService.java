package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;

/**
 * Created by bergm on 21/03/2016.
 */
public interface IMailingService {

    boolean sendVerificationEmail(User user, EmailAddressVerification emailAddressVerification);

    boolean sendRequestAccessEmail(User requestingUser, AccessControlObjectBase accessObject, String message);

}
