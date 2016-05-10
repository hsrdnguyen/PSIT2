package ch.avocado.share.common.constants.sql;

import static ch.avocado.share.common.constants.sql.Tables.*;

public class SecurityConstants {

    public static final String SELECT_ANONYMOUS_ACCESS_LEVEL = "" +
            "SELECT alevel.readable, alevel.writable, alevel.manageable, false" +
            "  FROM " + RIGHTS_LEVEL_TABLE + " AS alevel" +
            "  NATURAL JOIN " + DEFAULT_ACCESS_TABLE + " AS arights " +
            "  WHERE arights.object_id = ?";

    public static final String ADD_ANONYMOUS_ACCESS_LEVEL = "" +
            "INSERT INTO " + DEFAULT_ACCESS_TABLE + " (object_id, level) " +
            " VALUES (?, ?)";

    public static final int ADD_ANONYMOUS_ACCESS_LEVEL_OBJECT_ID_INDEX = 1;

    public static final int ADD_ANONYMOUS_ACCESS_LEVEL_LEVEL_INDEX = 2;

    public static final String UPDATE_ANONYMOUS_ACCESS_LEVEL = "" +
            "UPDATE " + DEFAULT_ACCESS_TABLE + " SET level = ? " +
            " WHERE object_id = ?";

    public static final int UPDATE_ANONYMOUS_ACCESS_LEVEL_LEVEL_INDEX = 1;
    public static final int UPDATE_ANONYMOUS_ACCESS_LEVEL_OBJECT_ID_INDEX = 2;
    public static final int SELECT_ACCESS_LEVEL_READ_INDEX = 1;
    public static final int SELECT_ACCESS_LEVEL_WRITE_INDEX = 2;
    public static final int SELECT_ACCESS_LEVEL_MANAGE_INDEX = 3;
    public static final int SELECT_ACCESS_LEVEL_OWNER_INDEX = 4;

    public static final String SELECT_ACCESS_LEVEL_QUERY = "" +
            "SELECT level FROM " + RIGHTS_LEVEL_TABLE + " " +
            "    WHERE readable=? AND writable=? AND manageable=?";

    public static final String SET_ACCESS_LEVEL_QUERY =
            "UPDATE " + RIGHTS_TABLE + " SET LEVEL= ? " +
                    "    WHERE object_id = ? AND owner_id = ?";

    public static final String ADD_ACCESS_LEVEL_QUERY = "" +
            "INSERT INTO " + RIGHTS_TABLE + " (object_id, owner_id, level) " +
            "    VALUES (?, ?, ?)";

    public static final int ADD_ACCESS_LEVEL_QUERY_INDEX_OBJECT = 1;

    public static final int ADD_ACCESS_LEVEL_QUERY_INDEX_OWNER = 2;

    public static final int ADD_ACCESS_LEVEL_QUERY_INDEX_LEVEL = 3;

    public static final String DELETE_ACCESS_LEVEL_QUERY = "" +
            "DELETE FROM " + RIGHTS_TABLE + " " +
            "    WHERE object_id = ? AND owner_id = ? ";

    public static final int DELETE_ACCESS_LEVEL_QUERY_INDEX_OBJECT_ID = 1;

    public static final int DELETE_ACCESS_LEVEL_QUERY_INDEX_OWNER_ID = 2;


    private static final String SELECT_WITH_ACCESS_FORMAT = "" +
            "SELECT  id, readable, writable, manageable, false " +
            "FROM %s AS g " +
            "JOIN " + RIGHTS_TABLE + " AS r  " +
            "  ON r.owner_id = g.id " +
            "NATURAL JOIN " + RIGHTS_LEVEL_TABLE + " as l  " +
            "WHERE " +
            "  r.object_id = ? " +
            "  AND (l.readable <> false OR l.writable <> false OR l.manageable <> false ) " +
            "UNION " +
            "   SELECT o.owner_id, true, true, true, true FROM " + OWNERSHIP_TABLE + " AS o " +
            "   JOIN %s AS og " +
            "   ON o.owner_id = og.id " +
            "       WHERE o.object_id = ? " +
            "";

    public static final String SELECT_GROUPS_WITH_ACCESS_ON_OBJECT = String.format(SELECT_WITH_ACCESS_FORMAT, GROUP_TABLE, GROUP_TABLE);

    public static final String SELECT_USER_WITH_ACCESS_ON_OBJECT = String.format(SELECT_WITH_ACCESS_FORMAT, USER_TABLE, USER_TABLE);

    public static final String SELECT_TARGETS_WITH_ACCESS = "" +
            "SELECT object_id " +
            "   FROM " + RIGHTS_TABLE + " AS r " +
            "       WHERE r.owner_id = ? " +
            "       AND r.level >= ? " +
            "UNION SELECT object_id  FROM " + OWNERSHIP_TABLE + " AS o" +
            "   WHERE o.owner_id = ? " +
            "UNION SELECT file_id FROM avocado_share.uploaded_into AS u " +
            "   JOIN " + RIGHTS_TABLE + " AS ur" +
            "   ON ur.object_id = u.module_id " +
            "       WHERE ur.owner_id = ? " +
            "       AND ur.level >= ? ";

    public static final int SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX = 1;
    public static final int SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX_2 = 3;
    public static final int SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX_3 = 4;
    public static final int SELECT_TARGETS_WITH_ACCESS_LEVEL_INDEX = 2;
    public static final int SELECT_TARGETS_WITH_ACCESS_LEVEL_INDEX_2 = 5;
    private static final String SELECT_MODULE_RIGHTS_FOR_FILE = "" +
            "SELECT readable, writable, manageable, FALSE FROM access_level AS level2\n" +
            "JOIN (SELECT\n" +
            "  rights_from_module.level\n" +
            "FROM\n" +
            "  " + RIGHTS_TABLE + " AS rights_from_module\n" +
            "  JOIN " + UPLOADED_TABLE + " AS ui\n" +
            "    ON ui.module_id = rights_from_module.object_id\n" +
            "  WHERE ui.file_id = ? AND rights_from_module.owner_id = ?) AS file_rights_from_module\n" +
            "  ON file_rights_from_module.level = level2.level\n " +
            " UNION " +
            " SELECT true, true, true, false FROM " + OWNERSHIP_TABLE +
            "   JOIN " + UPLOADED_TABLE +
            "       ON module_id = object_id\n " +
            "       WHERE owner_id = ? AND file_id  = ? ";

    public static final String SELECT_ACCESS_LEVEL_INCLUDING_INHERITED = "" +
            "SELECT " +
            "  readable, writable, manageable, false " +
            "FROM " + RIGHTS_LEVEL_TABLE + " AS l " +
            "JOIN " + RIGHTS_TABLE + " AS r " +
            "  ON r.level = l.level " +
            " WHERE r.owner_id = ? AND r.object_id = ? " +
            " UNION SELECT true, true, true, true FROM " + OWNERSHIP_TABLE + " AS o" +
            "  WHERE o.owner_id = ? AND o.object_id = ? " +
            " UNION SELECT " +
            "    glevel.readable, glevel.writable, glevel.manageable, false" +
            "  FROM " + RIGHTS_LEVEL_TABLE + " AS glevel " +
            "  JOIN " + RIGHTS_TABLE + " AS grights " +
            "    ON grights.level = glevel.level " +
            "  JOIN " + GROUP_TABLE + " AS gro " +
            "    ON grights.owner_id = gro.id " +
            "    WHERE " +
            "      EXISTS( " +
            "        SELECT 1 " +
            "          FROM " + RIGHTS_TABLE + " x " +
            "          NATURAL JOIN " + RIGHTS_LEVEL_TABLE + " y " +
            "          WHERE x.owner_id = ? AND x.object_id = gro.id AND y.readable = TRUE" +
            "       )" +
            "       AND grights.object_id = ?" +
            " UNION " + SELECT_ANONYMOUS_ACCESS_LEVEL +
            " UNION " + SELECT_MODULE_RIGHTS_FOR_FILE;
}
