package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
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
     * @throws MailingServiceException if the email could not be sent
     */
    void sendVerificationEmail(User user) throws MailingServiceException;

    /**
     * Sends an email to request access to the given resource
     * @param requestingUser user that requests access
     * @param object object to be accessed -> Owner will receive email
     * @throws MailingServiceException if the email could not be sent
     */
    void sendRequestAccessEmail(User requestingUser, User owningUser, AccessControlObjectBase object) throws MailingServiceException;

    /**
     * Sends an email to the user with a link to reset his password.
     * @param user user to send the email to
     * @throws MailingServiceException if the email could not be sent
     */
    void sendPasswordResetEmail(User user) throws MailingServiceException;
}
