package ch.avocado.share.service.Impl;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.constants.MailingConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.exceptions.MailingServiceException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by bergm on 21/03/2016.
 */
public class MailingService implements IMailingService {

    @Override
    public boolean sendVerificationEmail(User user) throws MailingServiceException {
        if (user == null) throw new NullPointerException("user is Null");
        if (user.getMail().getVerification() == null) throw new IllegalArgumentException("emailAddressVerification is Null");

        Session session = prepareMessage();
        String email, code;

        email =  Encoder.forUrl(user.getMail().getAddress());
        code = Encoder.forUrl(user.getMail().getVerification().getCode());

        try {
            sendEmail(user, session, MailingConstants.VERIFICATION_SUBJECT, String.format(MailingConstants.VERIFICATION_MESSAGE,
                                    String.format(MailingConstants.VERIFICTAION_URL, code, email)));
        } catch (MessagingException e) {
            throw new MailingServiceException(ErrorMessageConstants.ERROR_SEND_VERIFICATION_MAIL, user.getMail().getAddress(), e);
        }
        return true;
    }

    @Override
    public boolean sendRequestAccessEmail(User requestingUser, User owningUser, AccessControlObjectBase object) throws MailingServiceException {
        if (requestingUser == null) throw new NullPointerException("user is Null");
        if (object == null) throw new NullPointerException("object is Null");
        if (owningUser == null) throw new NullPointerException("owningUser is Null");

        Session session = prepareMessage();

        try {
            String link = String.format(MailingConstants.REQUEST_RESPONSE_URL, object.getId(), requestingUser.getId(), owningUser.getId());
            sendEmail(owningUser, session, MailingConstants.REQUEST_SUBJECT, String.format(MailingConstants.REQUEST_MESSAGE, requestingUser.getFullName(), object.getReadableName(), link));
        } catch (MessagingException e) {
            throw new MailingServiceException(ErrorMessageConstants.ERROR_SEND_ACCESS_REQUEST_MAIL, owningUser.getMail().getAddress(), e);
        }
        return true;
    }

    @Override
    public boolean sendPasswordResetEmail(User user) throws MailingServiceException {
        if (user == null) throw new NullPointerException("user is Null");
        String code, email;
        Session session = prepareMessage();

        email =  Encoder.forUrl(user.getMail().getAddress());
        code = Encoder.forUrl(user.getPassword().getResetVerification().getCode());

        try {
            sendEmail(user, session, MailingConstants.PASSWORD_RESET_SUBJECT, String.format(MailingConstants.PASSWORD_RESET_MESSAGE, code, email));
        } catch (MessagingException e) {
            throw new MailingServiceException(ErrorMessageConstants.ERROR_SEND_PASSWORD_RESET_MAIL, user.getMail().getAddress(), e);
        }
        return true;
    }

    private void sendEmail(User user, Session session, String subject, String message) throws MessagingException {
        Message email = new MimeMessage(session);
        email.setFrom(new InternetAddress(MailingConstants.SENDER_MAIL_ADDRESS));
        email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getMail().getAddress()));
        email.setSubject(subject);
        email.setText(message);

        Transport.send(email);
    }

    private Session prepareMessage() {
        Properties props = new Properties();
        props.put("mail.smtp.host", MailingConstants.SMTP_SERVER);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtps.socketFactory.port", MailingConstants.SMTP_PORT);
        props.put("mail.smtps.socketFactory.class", MailingConstants.SMTP_FACTORY_CLASS);
        props.put("mail.smtp.auth", MailingConstants.AUTHENTIFICATION);
        props.put("mail.smtp.port", MailingConstants.SMTP_PORT);

        return Session.getDefaultInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MailingConstants.USERNAME, MailingConstants.PASSWORD);
                    }
                });
    }
}
