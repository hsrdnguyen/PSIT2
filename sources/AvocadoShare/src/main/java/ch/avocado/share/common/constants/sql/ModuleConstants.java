package ch.avocado.share.common.constants.sql;

import static ch.avocado.share.common.constants.sql.Tables.OWNERSHIP_TABLE;

/**
 * Created by coffeemakr on 01.05.16.
 */
public final class ModuleConstants {
    private static final String table = "avocado_share.module";
    public static final String INSERT_QUERY = "INSERT INTO " + table + "(id, name) VALUES (?, ?)";
    public static final int INSERT_QUERY_ID_INDEX = 1;
    public static final int INSERT_QUERY_NAME_INDEX = 2;

    private static final String SELECT_COLUMNS = "m.id, description, name, creation_date, owner.owner_id";
    public static final int RESULT_INDEX_ID = 1;
    public static final int RESULT_INDEX_DESCRIPTION = 2;
    public static final int RESULT_INDEX_NAME = 3;
    public static final int RESULT_INDEX_CREATION_DATE = 4;
    public static final int RESULT_INDEX_OWNER = 5;

    public static final String SELECT_WITHOUT_WHERE = "" +
            "SELECT " + SELECT_COLUMNS + " FROM " + table + " AS m " +
            "   JOIN avocado_share.access_control as a " +
            "       ON m.id = a.id " +
            "   LEFT JOIN " + OWNERSHIP_TABLE + " AS owner " +
            "       ON a.id = owner.object_id ";

    public static final String SEARCH_QUERY = "" +
            "SELECT " + SELECT_COLUMNS + " FROM " + table + " AS m " +
            "   JOIN avocado_share.access_control as a " +
            "       ON m.id = a.id " +
            "   LEFT JOIN avocado_share.ownership AS owner " +
            "       ON a.id = owner.object_id " +
            "   WHERE title LIKE (?) OR description LIKE (?) ";

    public static final String SELECT_QUERY = SELECT_WITHOUT_WHERE + " WHERE a.id = ? ";
    public static final String SELECT_BY_ID_LIST = SELECT_WITHOUT_WHERE  + " WHERE a.id IN ";

    public static final int SELECT_QUERY_INDEX_ID = 1;

    public static final String UPDATE_QUERY = "UPDATE " + table + " SET name=? WHERE id=?";
    public static final int UPDATE_QUERY_INDEX_ID = 2;
    public static final int UPDATE_QUERY_INDEX_NAME = 1;
    public static final String SELECT_FILES = "SELECT file_id FROM avocado_share.uploaded_into WHERE module_id = ?";
    public static final int SELECT_FILES_INDEX_MODULE = 1;
}
