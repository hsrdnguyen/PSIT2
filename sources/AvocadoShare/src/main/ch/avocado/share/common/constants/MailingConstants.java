package ch.avocado.share.common.constants;

/**
 * Created by bergm on 21/03/2016.
 */
public class MailingConstants {
    public static final String SENDER_MAIL_ADDRESS = "info@avocado-share.com";
    public static final String MAIL_HOST = "info@avocado-share.com";
    public static final String USERNAME = "avocado.share@gmail.com";
    public static final String SMTP_SERVER = "smtp.gmail.com";
    public static final String SMTP_PORT = "465";
    public static final String SMTP_FACTORY_CLASS = " javax.net.ssl.SSLSocketFactory";
    public static final String AUTHENTIFICATION = "true";
    public static final String PASSWORD = "Gruppe13";

    // For Verification
    public static final String VERIFICTAION_URL = "http://127.0.0.1:8080/JSP/verification.jsp?code=%s&mail=%s";
    public static final String VERIFICATION_SUBJECT = "E-Mail Verifikation";
    public static final String VERIFICATION_MESSAGE = "Guten Tag\n\n Um ihr Konto zu aktivieren und Ihre E-Mail-Addresse zu verifizieren, rufen Sie bitte folgenden Link auf: \n\n %s";

    // For permission-request
    public static final String REQUEST_SUBJECT = "E-Mail Verifikation";
    public static final String REQUEST_MESSAGE = "Dear Mail Crawler,\n\n No spam to my email, please!";

    // For password reset request
    public static final String PASSWORD_RESET_SUBJECT = "Password zurücksetzten";
    public static final String PASSWORD_RESET_MESSAGE = "???";
}