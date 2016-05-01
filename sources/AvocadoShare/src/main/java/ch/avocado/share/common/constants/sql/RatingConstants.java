package ch.avocado.share.common.constants.sql;

/**
 * Created by kunzlio1 on 18.04.2016.
 */
public class RatingConstants {
    public static final int MIN_RATING_VALUE = 0;
    public static final int MAX_RATING_VALUE = 4;
    public static final String SQL_ADD_RATING = "INSERT INTO rating (object_id, identity_id, rating) VALUES (?, ?)";
    public static final String SQL_DELETE_RATING = "DELETE FROM rating WHERE object_id = ? AND identity_id = ?";
    public static final String SQL_UPDATE_RATING = "UPDATE rating SET rating = ? WHERE object_id = ? AND identity_id = ?";
    public static final String SQL_SELECT_BY_USER_ID_AND_OBJECT_ID = "SELECT rating FROM rating " +
            "WHERE identity_id = ? AND object_id = ?";
    public static final String SQL_SELECT_BY_OBJECT_ID = "SELECT object_id, identity_id, rating FROM rating " +
            "WHERE object_id = ?";
}
