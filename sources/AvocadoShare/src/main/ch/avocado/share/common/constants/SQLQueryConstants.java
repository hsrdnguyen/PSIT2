package ch.avocado.share.common.constants;

/**
 * Created by bergm on 22/03/2016.
 */
public class SQLQueryConstants {
    //ACCESS CONTROL DATA OBJECT
    public static final String INSERT_ACCESS_CONTROL_QUERY = "INSERT INTO avocado_share.access_control(id, creation_date) VALUES (DEFAULT, DEFAULT) ";
    public static final String DELETE_ACCESS_CONTROL_QUERY = "DELETE FROM access_control WHERE id = ?";
    public static final String SELECT_ACCESS_CONTROL_QUERY = "SELECT id, creation_date FROM avocado_share.access_control WHERE id=?";

    //USER DATA QUERIES
    public static final String INSERT_USER_QUERY = "INSERT INTO avocado_share.identity(id, prename, surname, avatar, description, password) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String SELECT_USER_QUERY = "SELECT id, prename, surname, avatar, description, password FROM  avocado_share.identity WHERE id=?";
    public static final String UPDATE_USER_QUERY = "UPDATE avocado_share.identity SET prename=?, surname=?, avatar=?, description=?, password=? WHERE id=?";
    public static final String INSERT_MAIL_QUERY = "INSERT INTO avocado_share.email(identity_id, address, verified)VALUES (?, ?, FALSE)";
    public static final String SELECT_MAIL_QUERY = "SELECT identity_id, address, verified FROM avocado_share.email WHERE identity_id=?";
    public static final String INSERT_MAIL_VERIFICATION_QUERY = "INSERT INTO avocado_share.email_verification(identity_id, address, expiry, verification_code) VALUES (?, ?, ?, ?)";

    public static final String SET_MAIL_TO_VERIFIED = "UPDATE avocado_share.email SET verified=TRUE WHERE identity_id=?";


    //GROUP
    public static final String INSERT_GROUP_QUERY = "INSERT INTO avocado_share.access_group(name, description) VALUES (?, ?)";
    public static final int INSERT_GROUP_QUERY_NAME_INDEX = 1;
    public static final int INSERT_GROUP_QUERY_DESCRIPTION_INDEX = 2;


    public static final String GET_GROUP_BY_ID = "SELECT id, name, description, creation_date FROM access_group AS g JOIN access_control AS o ON g.id = o.id WHERE g.id = ?";
    /** Index of the id in get group by id query */
    public static final int GET_GROUP_BY_ID_ID_INDEX = 1;
    /** Index of the id in a get group result */
    public static final int GROUP_RESULT_ID_INDEX = 1;
    /** Index of the name in a get group result */
    public static final int GROUP_RESULT_NAME_INDEX = 2;
    /** Index of the description in a get group result */
    public static final int GROUP_RESULT_DESCRIPTION_INDEX = 3;
    /** Index of the creation date in a get group result */
    public static final int GROUP_RESULT_CREATION_DATE = 4;

    //CATEGORY
    public static final String SQL_SELECT_CATEGORY_BY_NAME = "SELECT object_id, name FROM category WHERE name = '?'";

    public static final String SQL_SELECT_CATEGORY_BY_NAME_AND_OBJECT_ID = "SELECT object_id, name FROM category WHERE name = '?' AND object_id = ?";

    public static final String SQL_ADD_CATEGORY = "INSERT INTO category (object_id, name) (?, '?')";

    public static final String SQL_DELETE_CATEGORY_FROM_OBJECT = "DELETE FROM category WHERE name = '?' AND object_id = ?";

    //FILE
    public static final String SELECT_FILE_BY_ID_QUERY = "SELECT o.id, title, description, last_changed, creation_date, path FROM file AS f JOIN access_control AS o ON f.id = o.id WHERE o.id = ?";
    public static final String SELECT_FILE_BY_TITLE_QUERY = "SELECT o.id, title, description, last_changed, creation_date, path FROM file AS f JOIN access_control AS o ON f.id = o.id WHERE title = ?";
    public static final String INSERT_FILE_QUERY = "INSERT INTO file (id, title, description, last_changed, path) (?, ?, ?, ?, ?)";
    public static final String DELETE_FILE_QUERY = "DELETE FROM file WHERE id = ?";
    public static final String UPDATE_FILE_QUERY = "UPDATE file SET title=?, description=?, last_changed=?, path=? WHERE id = ?";
}
