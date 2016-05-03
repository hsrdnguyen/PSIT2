package ch.avocado.share.service.Impl;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.constants.MailingConstants;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IMailingService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by bergm on 21/03/2016.
 */
public class MailingService implements IMailingService {

    @Override
    public boolean sendVerificationEmail(User user) {
        if (user == null) throw new IllegalArgumentException("user is Null");
        if (user.getMail().getVerification() == null) throw new IllegalArgumentException("emailAddressVerification is Null");

        Session session = prepareMessage();
        String email, code;

        email =  Encoder.forUrl(user.getMail().getAddress());
        code = Encoder.forUrl(user.getMail().getVerification().getCode());

        try {
            sendEmail(user, session, MailingConstants.VERIFICATION_SUBJECT, String.format(MailingConstants.VERIFICATION_MESSAGE,
                                    String.format(MailingConstants.VERIFICTAION_URL, code, email)));
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean sendRequestAccessEmail(User requestingUser, User owningUser, File file) {
        if (requestingUser == null) throw new IllegalArgumentException("user is Null");
        if (file == null) throw new IllegalArgumentException("file is Null");
        if (owningUser == null) throw new IllegalArgumentException("owningUser is Null");

        Session session = prepareMessage();

        try {

            String link = String.format(MailingConstants.REQUEST_RESPONSE_URL, file.getId(), requestingUser.getId(), owningUser.getId());
            sendEmail(owningUser, session, MailingConstants.REQUEST_SUBJECT, String.format(MailingConstants.REQUEST_MESSAGE, requestingUser.getFullName(), file.getTitle(), link));
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean sendPasswordResetEmail(User user) {
        if (user == null) throw new IllegalArgumentException("user is Null");
        String code, email;
        Session session = prepareMessage();

        email =  Encoder.forUrl(user.getMail().getAddress());
        code = Encoder.forUrl(user.getPassword().getResetVerification().getCode());

        try {
            sendEmail(user, session, MailingConstants.PASSWORD_RESET_SUBJECT, String.format(MailingConstants.PASSWORD_RESET_MESSAGE, code, email));
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
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
