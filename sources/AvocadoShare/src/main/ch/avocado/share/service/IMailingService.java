package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;

/**
 * Created by bergm on 21/03/2016.
 */
public interface IMailingService {

    /**
     * Sends an email to the users email with a link to verify the email with the link emailVerification in the user
     * @param user user to send the email to
     * @return true if mail was sent successfully
     */
    boolean sendVerificationEmail(User user);

    /**
     * Sends an email to request access to the given resource
     * @param requestingUser user that requests access
     * @param accessObject object to be accessed -> Owner will receive email
     * @param message messeage of the sender
     * @return true if mail was sent successfully
     */
    boolean sendRequestAccessEmail(User requestingUser, AccessControlObjectBase accessObject, String message);

}
