package ch.avocado.share.common.constants.sql;

/**
 * SQL Constants to query and update User
 */
public class UserConstants {
    //USER DATA QUERIES

    public static final String INSERT_USER_QUERY = "INSERT INTO " + Tables.USER_TABLE + " (id, prename, surname, avatar, password) VALUES (?, ?, ?, ?, ?)";
    public static final String INSERT_PASSWORD_VERIFICATION_QUERY = "INSERT INTO avocado_share.password_reset(id, expiry, code) VALUES (?, ?, ?)";
    public static final String DELETE_USER_QUERY = "DELETE FROM access_control WHERE id = ?";
    public static final int USER_RESULT_ID_INDEX = 1;
    public static final int USER_RESULT_PRENAME_INDEX = 2;
    public static final int USER_RESULT_SURNAME_INDEX = 3;
    public static final int USER_RESULT_AVATAR_INDEX = 4;
    public static final int USER_RESULT_DESCRIPTION_INDEX = 5;
    public static final int USER_RESULT_PASSWORD_INDEX = 6;
    public static final int USER_RESULT_ADDRESS_INDEX = 7;
    public static final int USER_RESULT_VERIFIED_INDEX = 8;
    public static final int USER_RESULT_CREATION_DATE_INDEX = 9;
    public static final int USER_RESULT_RESET_CODE_INDEX = 10;
    public static final int USER_RESULT_RESET_EXPIRY_INDEX = 11;

    public static final String SELECT_PASSWORD_VERIFICATION_QUERY = "SELECT id, expiry, code FROM avocado_share.password_reset WHERE id=?";
    public static final String UPDATE_USER_QUERY = "UPDATE " + Tables.USER_TABLE + "  SET prename=?, surname=?, avatar=?, password=? WHERE id=?";
    public static final String INSERT_MAIL_QUERY = "INSERT INTO avocado_share.email(identity_id, address, verified)VALUES (?, ?, FALSE)";
    public static final String SELECT_MAIL_QUERY = "SELECT identity_id, address, verified FROM avocado_share.email WHERE identity_id=?";
    public static final String INSERT_MAIL_VERIFICATION_QUERY = "INSERT INTO avocado_share.email_verification(identity_id, address, expiry, verification_code) VALUES (?, ?, ?, ?)";
    public static final String SELECT_EMAIL_VERIFICATION  = "SELECT expiry, verification_code FROM avocado_share.email_verification WHERE identity_id=? AND address=?";
    public static final int SELECT_EMAIL_VERIFICATION_USER_ID_INDEX = 1;
    public static final int SELECT_EMAIL_VERIFICATION_ADDRESS_INDEX = 2;
    public static final int EMAIL_VERIFICATION_RESULT_CODE_INDEX = 2;
    public static final int EMAIL_VERIFICATION_RESULT_EXPIRY_INDEX = 1;
    public static final String SET_EMAIL_VERIFICATION = "UPDATE avocado_share.email SET verified = ? WHERE address = ?";
    public static final int SET_EMAIL_VERIFICATION_INDEX_VALID = 1;
    public static final int SET_EMAIL_VERIFICATION_INDEX_ADDRESS = 2;

    private static final String SELECT_USER_SELECTED_COLUMNS = "I.id, prename, surname, avatar, description, password, address, verified, creation_date";
    private static final String SELECT_USERS = "" +
            "SELECT " +
            "  I.id, " +
            "  prename, " +
            "  surname, " +
            "  avatar, " +
            "  description, " +
            "  password, " +
            "  address, " +
            "  verified, " +
            "  creation_date, " +
            "  r.code, " +
            "  r.expiry " +
            "FROM " + Tables.USER_TABLE + "  AS I " +
            "  JOIN avocado_share.email AS E " +
            "    ON E.identity_id = I.id " +
            "  JOIN avocado_share.access_control AS O " +
            "    ON O.id = I.id " +
            "  LEFT JOIN avocado_share.password_reset AS r " +
            "    ON r.id = I.id ";

    public static final String SELECT_USER_BY_MAIL_QUERY = SELECT_USERS + "  WHERE E.address = ?";
    public static final String SELECT_USER_QUERY = SELECT_USERS + "  WHERE I.id = ?";

    public static final String UPDATE_PASSWORD_RESET = "UPDATE avocado_share.password_reset SET code = ?, expiry = ? WHERE id = ?";
    public static final int UPDATE_PASSWORD_RESET_CODE_INDEX = 1;
    public static final int UPDATE_PASSWORD_RESET_EXPIRY_INDEX = 2;
    public static final int UPDATE_PASSWORD_RESET_USER_INDEX = 3;


    public static final String DELETE_RESET_VERIFICATION = "DELETE FROM avocado_share.password_reset WHERE id = ? ";
    public static final String SEARCH_QUERY_LIKE = " LOWER( prename ) LIKE ? OR LOWER( surname ) LIKE ? ";
    public static final String SEARCH_QUERY_START = SELECT_USERS + " WHERE ";
    public static final String SEARCH_QUERY_LINK = " OR ";
    public static final int NUMBER_OF_TERMS_PER_LIKE = 2;
}
