package ch.avocado.share.common.constants.sql;

/**
 * Category related queries
 */
public final class CategoryConstants {
    public static final String SELECT_BY_NAME = "SELECT object_id, name FROM category WHERE name = ?";

    public static final String SELECT_BY_NAME_AND_OBJECT_ID = "SELECT object_id, name FROM category WHERE name = ? AND object_id = ?";

    public static final String SQL_SELECT_CATEGORIES_BY_OBJECT_ID = "SELECT name FROM category WHERE object_id = ?";

    public static final String SQL_ADD_CATEGORY = "INSERT INTO category (object_id, name) VALUES (?, ?)";

    public static final String SQL_DELETE_CATEGORY_FROM_OBJECT = "DELETE FROM category WHERE name = ? AND object_id = ?";
}
