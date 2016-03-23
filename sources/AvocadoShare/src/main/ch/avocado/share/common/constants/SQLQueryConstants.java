package ch.avocado.share.common.constants;

/**
 * Created by bergm on 22/03/2016.
 */
public class SQLQueryConstants {
    public static final String INSERT_FILE_QUERY = "INSERT INTO file(id, title, description, lastchanged) VALUES ('%s', '%s', '%s', '%s')";

    //ACCESS CONTROL DATA OBJECT
    public static final String INSERT_ACCESS_CONTROL_QUERY = "INSERT INTO avocado_share.access_control(id, creation_date) VALUES (DEFAULT, DEFAULT) ";

    //USER DATA QUERIES
    public static final String INSERT_USER_QUERY = "INSERT INTO avocado_share.identity(id, prename, surname, avatar, description, password) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";
    public static final String INSERT_MAIL_QUERY = "INSERT INTO avocado_share.email(identity_id, address, verified)VALUES ('%s', '%s', FALSE)";
    public static final String INSERT_MAIL_VERIFICATION_QUERY = "INSERT INTO avocado_share.email_verification(identity_id, address, expiry, verification_code) VALUES ('%s', '%s', '%s', '%s')";

    public static final String SET_MAIL_TO_VERIFIED = "UPDATE avocado_share.email SET verified=TRUE WHERE identity_id='%s'";


}
