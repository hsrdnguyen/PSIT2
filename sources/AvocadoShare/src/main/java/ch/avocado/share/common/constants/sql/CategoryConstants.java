package ch.avocado.share.common.constants.sql;

import static ch.avocado.share.common.constants.sql.Tables.*;

/**
 * Category related queries
 */
public final class CategoryConstants {
    public static final String SELECT_BY_NAME = "SELECT object_id, name FROM " + CATEGORY_TABLE + " WHERE name = ?";

    public static final String SELECT_BY_NAME_AND_OBJECT_ID = "SELECT object_id, name FROM " + CATEGORY_TABLE + " WHERE name = ? AND object_id = ?";

    public static final String SQL_SELECT_CATEGORIES_BY_OBJECT_ID = "" +
            "SELECT object_id, name FROM " + CATEGORY_TABLE  +
            "   WHERE name in (" +
            "   SELECT name FROM " + CATEGORY_TABLE +
            "       WHERE object_id = ? " +
            " ) " +
            " ORDER BY name ";

    public static final String SQL_ADD_CATEGORY = "INSERT INTO " + CATEGORY_TABLE + " (object_id, name) VALUES (?, ?)";

    public static final String SQL_DELETE_CATEGORY_FROM_OBJECT = "DELETE FROM " + CATEGORY_TABLE + " WHERE name = ? AND object_id = ?";
}
