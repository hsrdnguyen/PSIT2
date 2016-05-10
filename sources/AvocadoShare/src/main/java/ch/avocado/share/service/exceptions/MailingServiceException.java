package ch.avocado.share.service.exceptions;

import ch.avocado.share.common.constants.ErrorMessageConstants;

/**
 * Created by kunzlio on 09.05.2016.
 */
public class MailingServiceException extends ServiceException {
    private final String mailTo;

    public MailingServiceException(String description, String mailTo, Throwable originalCause){
        super(description, originalCause);
        this.mailTo = mailTo;
    }

    public MailingServiceException(String mailTo, Throwable originalCause){
        super(ErrorMessageConstants.ERROR_SEND_MAIL_FAILED, originalCause);
        this.mailTo = mailTo;
    }

    public String getMailTo(){
        return this.mailTo;
    }
}
