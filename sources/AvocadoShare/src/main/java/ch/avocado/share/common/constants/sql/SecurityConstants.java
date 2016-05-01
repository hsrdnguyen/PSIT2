package ch.avocado.share.common.constants.sql;

/**
 * Created by coffeemakr on 01.05.16.
 */
public class SecurityConstants {
    public static final String SELECT_READING_ACCESS_LEVEL = "SELECT level FROM avocado_share.access_level WHERE readable=TRUE AND writable=FALSE AND manageable=FALSE";
    public static final String SELECT_ACCESS_LEVEL = "" +
            "SELECT " +
            "  readable, writable, manageable, " +
            "  CASE " +
            "    WHEN o.owner_id IS NULL THEN false " +
            "    ELSE true " +
            "  END AS owner " +
            "FROM avocado_share.access_level AS l " +
            "JOIN avocado_share.rights AS r " +
            "  ON r.level = l.level " +
            "LEFT OUTER JOIN avocado_share.ownership AS o" +
            "  ON " +
            "      o.owner_id = r.owner_id " +
            "    AND " +
            "      o.object_id = r.object_id " +
            "WHERE o.owner_id = ? AND o.object_id = ?";
    public static final String SELECT_ANONYMOUS_ACCESS_LEVEL = "" +
            "SELECT alevel.readable, alevel.writable, alevel.manageable, false" +
            "  FROM avocado_share.access_level AS alevel" +
            "  NATURAL JOIN avocado_share.default_access AS arights " +
            "  WHERE arights.object_id = ?";
    public static final String ADD_ANONYMOUS_ACCESS_LEVEL = "" +
            "INSERT INTO avocado_share.default_access (object_id, level) " +
            " VALUES (?, ?)";
    public static final int ADD_ANONYMOUS_ACCESS_LEVEL_OBJECT_ID_INDEX = 1;
    public static final int ADD_ANONYMOUS_ACCESS_LEVEL_LEVEL_INDEX = 2;
    public static final String UPDATE_ANONYMOUS_ACCESS_LEVEL = "" +
            "UPDATE avocado_share.default_access SET level = ? " +
            " WHERE object_id = ?";
    public static final int UPDATE_ANONYMOUS_ACCESS_LEVEL_LEVEL_INDEX = 1;
    public static final int UPDATE_ANONYMOUS_ACCESS_LEVEL_OBJECT_ID_INDEX = 2;
    public static final int SELECT_ACCESS_LEVEL_READ_INDEX = 1;
    public static final int SELECT_ACCESS_LEVEL_WRITE_INDEX = 2;
    public static final int SELECT_ACCESS_LEVEL_MANAGE_INDEX = 3;
    public static final int SELECT_ACCESS_LEVEL_OWNER_INDEX = 4;
    public static final String SELECT_ACCESS_LEVEL_QUERY = "" +
            "SELECT level FROM avocado_share.access_level " +
            "    WHERE readable=? AND writable=? AND manageable=?";
    public static final String SET_ACCESS_LEVEL_QUERY =
            "UPDATE avocado_share.rights SET LEVEL= ? " +
            "    WHERE object_id = ? AND owner_id = ?";
    public static final String ADD_ACCESS_LEVEL_QUERY = "" +
            "INSERT INTO avocado_share.rights (object_id, owner_id, level) " +
            "    VALUES (?, ?, ?)";
    public static final int ADD_ACCESS_LEVEL_QUERY_INDEX_OBJECT = 1;
    public static final int ADD_ACCESS_LEVEL_QUERY_INDEX_OWNER = 2;
    public static final int ADD_ACCESS_LEVEL_QUERY_INDEX_LEVEL = 3;
    public static final String DELETE_ACCESS_LEVEL_QUERY = "" +
            "DELETE FROM avocado_share.rights " +
            "    WHERE object_id = ? AND owner_id = ? ";
    public static final int DELETE_ACCESS_LEVEL_QUERY_INDEX_OBJECT_ID = 1;
    public static final int DELETE_ACCESS_LEVEL_QUERY_INDEX_OWNER_ID = 2;
    public static final String SELECT_DEFAULT_ACCESS_LEVEL_QUERY = "" +
            "SELECT " +
            "  readable, writable, manageable " +
            "FROM avocado_share.access_level AS l " +
            "JOIN avocado_share.default_access AS d " +
            "  ON l.level = d.level";
    public static final String SELECT_GROUPS_WITH_ACCESS_ON_OBJECT = "" +
            "SELECT  id, readable, writable, manageable, false " +
            "FROM " + GroupConstants.TABLE + " AS g " +
            "JOIN avocado_share.rights AS r  " +
            "  ON r.owner_id = g.id " +
            "NATURAL JOIN avocado_share.access_level as l  " +
            "WHERE " +
            "  r.object_id = ? " +
            "  AND (l.readable <> false OR l.writable <> false OR l.manageable <> false ) " +
            "UNION " +
            "   SELECT o.owner_id, true, true, true, true FROM avocado_share.ownership AS o " +
            "   JOIN " + GroupConstants.TABLE + " AS og " +
            "   ON o.owner_id = og.id " +
            "       WHERE o.object_id = ? " +
            "";
    public static final String SELECT_USER_WITH_ACCESS_ON_OBJECT = "" +
            "SELECT  id, readable, writable, manageable, false " +
            "FROM avocado_share.identity AS g " +
            "JOIN avocado_share.rights AS r  " +
            "  ON r.owner_id = g.id " +
            "NATURAL JOIN avocado_share.access_level as l " +
            "WHERE " +
            "  r.object_id = ? " +
            "  AND ( l.readable <> false OR l.writable <> false OR l.manageable <> false ) "+
            "UNION " +
            "   SELECT o.owner_id, true, true, true, true FROM avocado_share.ownership AS o " +
            "   JOIN avocado_share.identity AS og " +
            "   ON o.owner_id = og.id " +
            "       WHERE o.object_id = ? " +
            "";
    public static final String SELECT_TARGETS_WITH_ACCESS = "" +
            "SELECT object_id " +
            "   FROM avocado_share.rights AS r " +
            "       WHERE r.owner_id = ? " +
            "       AND r.level >= ? " +
            "UNION SELECT object_id  FROM avocado_share.ownership AS o" +
            "   WHERE o.owner_id = ? " +
            "UNION SELECT file_id FROM avocado_share.uploaded_into AS u " +
            "   JOIN avocado_share.rights AS ur" +
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
            "  rights AS rights_from_module\n" +
            "  JOIN uploaded_into AS ui\n" +
            "    ON ui.module_id = rights_from_module.object_id\n" +
            "  WHERE ui.file_id = ? AND rights_from_module.owner_id = ?) AS file_rights_from_module\n" +
            "  ON file_rights_from_module.level = level2.level\n ";
    public static final String SELECT_ACCESS_LEVEL_INCLUDING_INHERITED = "" +
            "SELECT " +
            "  readable, writable, manageable, false " +
            "FROM avocado_share.access_level AS l " +
            "JOIN avocado_share.rights AS r " +
            "  ON r.level = l.level " +
            " WHERE r.owner_id = ? AND r.object_id = ? " +
            " UNION SELECT true, true, true, true FROM avocado_share.ownership AS o" +
            "  WHERE o.owner_id = ? AND o.object_id = ? " +
            " UNION SELECT " +
            "    glevel.readable, glevel.writable, glevel.manageable, false" +
            "  FROM avocado_share.access_level AS glevel " +
            "  JOIN avocado_share.rights AS grights " +
            "    ON grights.level = glevel.level " +
            "  JOIN avocado_share.access_group AS gro " +
            "    ON grights.owner_id = gro.id " +
            "    WHERE " +
            "      EXISTS( " +
            "        SELECT 1 " +
            "          FROM avocado_share.rights x " +
            "          NATURAL JOIN avocado_share.access_level y " +
            "          WHERE x.owner_id = ? AND x.object_id = gro.id AND y.readable = TRUE" +
            "       )" +
            "       AND grights.object_id = ?" +
            " UNION " + SELECT_ANONYMOUS_ACCESS_LEVEL +
            " UNION " + SELECT_MODULE_RIGHTS_FOR_FILE;
}
