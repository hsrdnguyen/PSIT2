package ch.avocado.share.common.constants.sql;

/**
 * SQL Constants to query and update User
 */
public class UserConstants {
    //USER DATA QUERIES
    public static final String INSERT_USER_QUERY = "INSERT INTO avocado_share.identity(id, prename, surname, avatar, password) VALUES (?, ?, ?, ?, ?)";
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
    public static final String SELECT_PASSWORD_VERIFICATION_QUERY = "SELECT id, expiry, code FROM avocado_share.password_reset WHERE id=?";
    public static final String UPDATE_USER_QUERY = "UPDATE avocado_share.identity SET prename=?, surname=?, avatar=?, password=? WHERE id=?";
    public static final String INSERT_MAIL_QUERY = "INSERT INTO avocado_share.email(identity_id, address, verified)VALUES (?, ?, FALSE)";
    public static final String SELECT_MAIL_QUERY = "SELECT identity_id, address, verified FROM avocado_share.email WHERE identity_id=?";
    public static final String INSERT_MAIL_VERIFICATION_QUERY = "INSERT INTO avocado_share.email_verification(identity_id, address, expiry, verification_code) VALUES (?, ?, ?, ?)";
    public static final String SELECT_EMAIL_VERIFICATION  = "SELECT expiry, verification_code FROM avocado_share.email_verification WHERE identity_id=? AND address=?";
    public static final int SELECT_EMAIL_VERIFICATION_USER_ID_INDEX = 1;
    public static final int SELECT_EMAIL_VERIFICATION_ADDRESS_INDEX = 2;
    public static final int EMAIL_VERIFICATION_RESULT_CODE_INDEX = 2;
    public static final int EMAIL_VERIFICATION_RESULT_EXPIRY_INDEX = 1;
    public static final String SET_MAIL_TO_VERIFIED = "UPDATE avocado_share.email SET verified=TRUE WHERE identity_id=?";

    private static final String SELECT_USER_SELECTED_COLUMNS = "I.id, prename, surname, avatar, description, password, address, verified, creation_date";
    public static final String SELECT_USER_BY_MAIL_QUERY = "" +
            "SELECT " + SELECT_USER_SELECTED_COLUMNS + " " +
            "  FROM avocado_share.identity AS I " +
            "  JOIN avocado_share.email AS E " +
            "    ON I.id=E.identity_id " +
            "  JOIN avocado_share.access_control AS O " +
            "    ON O.id=I.id " +
            "  WHERE E.address=?";
    public static final String SELECT_USER_QUERY = "" +
            "SELECT " + SELECT_USER_SELECTED_COLUMNS + " FROM  " +
            "  avocado_share.identity AS I " +
            "  JOIN avocado_share.email AS E " +
            "    ON I.id=E.identity_id " +
            "  JOIN avocado_share.access_control AS O " +
            "    ON O.id=I.id " +
            "  WHERE I.id=?";
}
