package ch.avocado.share.common.constants;

/**
 * Created by bergm on 22/03/2016.
 */
public class SQLQueryConstants {
    public static final String INSERT_FILE_QUERY = "INSERT INTO file(id, title, description, lastchanged) VALUES (?, ?, ?, ?)";

    //ACCESS CONTROL DATA OBJECT
    public static final String INSERT_ACCESS_CONTROL_QUERY = "INSERT INTO avocado_share.access_control(id, creation_date) VALUES (DEFAULT, DEFAULT) ";

    //USER DATA QUERIES
    public static final String INSERT_USER_QUERY = "INSERT INTO avocado_share.identity(id, prename, surname, avatar, description, password) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String INSERT_MAIL_QUERY = "INSERT INTO avocado_share.email(identity_id, address, verified)VALUES (?, ?, FALSE)";
    public static final String INSERT_MAIL_VERIFICATION_QUERY = "INSERT INTO avocado_share.email_verification(identity_id, address, expiry, verification_code) VALUES (?, ?, ?, ?)";

    public static final String SET_MAIL_TO_VERIFIED = "UPDATE avocado_share.email SET verified=TRUE WHERE identity_id=?";

    //GROUP
    public static final String SQL_SELECT_GROUP_BY_ID = "SELECT id, name, description, creation_date FROM access_group AS g JOIN access_control AS o ON g.id = o.id WHERE g.id = %s";

    //CATEGORY
    public static final String SQL_SELECT_CATEGORY_BY_NAME = "SELECT object_id, name FROM category WHERE name = '{}'";

    public static final String SQL_SELECT_CATEGORY_BY_NAME_AND_OBJECT_ID = "SELECT object_id, name FROM category WHERE name = {} AND object_id = {}";

    public static final String SQL_ADD_CATEGORY = "INSERT INTO category (object_id, name) ({}, '{}')";

    public static final String SQL_DELETE_CATEGORY_FROM_OBJECT = "DELETE FROM category WHERE name = {} AND object_id = {}";
}
