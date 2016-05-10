package ch.avocado.share.service;

import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.MailingServiceException;

/**
 * Created by bergm on 21/03/2016.
 */
public interface IMailingService {

    /**
     * Sends an email to the users email with a link to verify the email with the link emailVerification in the user
     * @param user user to send the email to
     * @return true if mail was sent successfully
     */
    boolean sendVerificationEmail(User user) throws MailingServiceException;

    /**
     * Sends an email to request access to the given resource
     * @param requestingUser user that requests access
     * @param file object to be accessed -> Owner will receive email
     * @return true if mail was sent successfully
     */
    boolean sendRequestAccessEmail(User requestingUser, User owningUser, File file) throws MailingServiceException;

    /**
     * Sends an email to the user with a link to reset his password.
     * @param user user to send the email to
     * @return true if mail was sent successfully
     */
    boolean sendPasswordResetEmail(User user) throws MailingServiceException;
}
