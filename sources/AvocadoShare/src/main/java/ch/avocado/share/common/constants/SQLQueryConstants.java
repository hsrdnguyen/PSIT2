package ch.avocado.share.common.constants;

/**
 * Created by bergm on 22/03/2016.
 */
public class SQLQueryConstants {
    //ACCESS CONTROL DATA OBJECT
    public static final int INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX = 1;
    public static final String INSERT_ACCESS_CONTROL_QUERY = "INSERT INTO avocado_share.access_control(id, creation_date, description) VALUES (DEFAULT, DEFAULT, ?) ";
    public static final String DELETE_ACCESS_CONTROL_QUERY = "DELETE FROM access_control WHERE id = ?";
    public static final String SELECT_ACCESS_CONTROL_QUERY = "SELECT id, creation_date FROM avocado_share.access_control WHERE id=?";

    public static final String UPDATE_ACCESS_CONTROL_DESCRIPTION = "UPDATE avocado_share.access_control SET description = ? WHERE id = ?";
    public static final int UPDATE_ACCESS_CONTROL_DESCRIPTION_DESCRIPTION_INDEX = 1;
    public static final int UPDATE_ACCESS_CONTROL_DESCRIPTION_ID_INDEX = 2;
    public static final String UPDATE_OWNERSHIP = "UPDATE avocado_share.ownership SET owner_id = ? WHERE object_id = ?";
    public static final int UPDATE_OWNERSHIP_INDEX_OWNER = 1;
    public static final int UPDATE_OWNERSHIP_INDEX_OBJECT = 2;
    public static final String DELETE_OWNERSHIP = "DELETE FROM avocado_share.ownership WHERE object_id = ?";
    public static final int DELETE_OWNERSHIP_INDEX_OBJECT = 1;
    public static final String SELECT_OWNED_IDS = "SELECT object_id FROM avocado_share.ownership WHERE owner_id = ?";
    public static final int SELECT_OWNED_IDS_OWNER_INDEX = 1;


    public final static class Module {
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

        public static final String SELECT_QUERY = "" +
                "SELECT " + SELECT_COLUMNS + " FROM " + table + " AS m " +
                "   JOIN avocado_share.access_control as a " +
                "       ON m.id = a.id " +
                "   LEFT JOIN avocado_share.ownership AS owner " +
                "       ON a.id = owner.object_id " +
                "   WHERE a.id = ? ";

        public static final int SELECT_QUERY_INDEX_ID = 1;

        public static final String UPDATE_QUERY = "UPDATE " + table + " SET name=? WHERE id=?";
        public static final int UPDATE_QUERY_INDEX_ID = 2;
        public static final int UPDATE_QUERY_INDEX_NAME = 1;
    }

    /**
     * Group related queries
     */
        public final static class Group {
        public static final String TABLE = "avocado_share.access_group";
        public static final String INSERT_QUERY = "INSERT INTO "+ TABLE + "(id, name) VALUES (?, ?)";
        public static final int INSERT_QUERY_NAME_INDEX = 2;
        public static final int INSERT_QUERY_ID_INDEX = 1;
        /**
         * Index of the id in get group by id query
         */
        public static final int GET_BY_ID_ID_INDEX = 1;

        /**
         * Index of the id in get group by name query
         */
        public static final int GET_BY_NAME_NAME_INDEX = 1;
        /**
         * Index of the id in a get group result
         */
        public static final int RESULT_ID_INDEX = 1;
        /**
         * Index of the name in a get group result
         */
        public static final int RESULT_NAME_INDEX = 2;
        /**
         * Index of the description in a get group result
         */
        public static final int RESULT_DESCRIPTION_INDEX = 3;
        /**
         * Index of the creation date in a get group result
         */
        public static final int RESULT_CREATION_DATE = 4;
        /**
         * Index of the ownerId in the result
         */
        public static final int RESULT_OWNER_ID = 5;
        private static final String SELECT_COLUMNS = "g.id, name, description, creation_date, W.owner_id";
        private static final String SELECT_WITHOUT_WHERE = "" +
                "SELECT " + SELECT_COLUMNS +
                " FROM " + TABLE + " AS g " +
                "JOIN avocado_share.access_control AS o " +
                " ON g.id = o.id " +
                "LEFT JOIN avocado_share.ownership as W " +
                " ON o.id = W.object_id ";
        public static final String SELECT_BY_NAME_QUERY = SELECT_WITHOUT_WHERE + "WHERE g.name = ?";
        public static final String SELECT_BY_ID_QUERY = SELECT_WITHOUT_WHERE +  "WHERE g.id = ?";

        public static final String UPDATE = "UPDATE " + TABLE + " SET name = ? WHERE id = ?";
        public static final int UPDATE_INDEX_ID = 2;
        public static final int UPDATE_INDEX_NAME = 1;
    }

    /**
     * Category related queries
     */
    public static final class Category {
        public static final String SQL_SELECT_CATEGORY_BY_NAME = "SELECT object_id, name FROM category WHERE name = ?";

        public static final String SQL_SELECT_CATEGORY_BY_NAME_AND_OBJECT_ID = "SELECT object_id, name FROM category WHERE name = ? AND object_id = ?";

        public static final String SQL_SELECT_CATEGORIES_BY_OBJECT_ID = "SELECT name FROM category WHERE object_id = ?";

        public static final String SQL_ADD_CATEGORY = "INSERT INTO category (object_id, name) VALUES (?, ?)";

        public static final String SQL_DELETE_CATEGORY_FROM_OBJECT = "DELETE FROM category WHERE name = ? AND object_id = ?";
    }

    /**
     * Rating related queries
     */
    public static final class Rating{
        public static final String SQL_ADD_RATING = "INSERT INTO rating (object_id, identity_id, rating) VALUES (?, ?)";

        public static final String SQL_DELETE_RATING = "DELETE FROM rating WHERE object_id = ? AND identity_id = ?";

        public static final String SQL_UPDATE_RATING = "UPDATE rating SET rating = ? WHERE object_id = ? AND identity_id = ?";

        public static final String SQL_SELECT_BY_USER_ID_AND_OBJECT_ID = "SELECT rating FROM rating " +
                "WHERE identity_id = ? AND object_id = ?";

        public static final String SQL_SELECT_BY_OBJECT_ID = "SELECT object_id, identity_id, rating FROM rating " +
                "WHERE object_id = ?";
    }

    public static final class File {
        public static final String SELECT_BY_ID_QUERY = "" +
                "SELECT o.id, title, description, last_changed, creation_date, path, module_id, owner.owner_id " +
                "   FROM file AS f " +
                "JOIN access_control AS o " +
                "   ON f.id = o.id " +
                "JOIN avocado_share.uploaded_into AS u " +
                "   ON u.file_id = f.id " +
                "LEFT JOIN avocado_share.ownership AS owner " +
                "   ON owner.object_id = f.id " +
                "WHERE o.id = ?";
        public static final String SELECT_BY_TITLE_QUERY_AND_MODULE = "" +
                "SELECT o.id, title, description, last_changed, creation_date, path, module_id, owner.owner_id " +
                "   FROM file AS f " +
                "JOIN access_control AS o " +
                "   ON f.id = o.id " +
                "JOIN avocado_share.uploaded_into AS u " +
                "   ON u.file_id = f.id " +
                "LEFT JOIN avocado_share.ownership AS owner " +
                "   ON owner.object_id = f.id " +
                "WHERE title = ? AND module_id = ?";
        public static final String INSERT_QUERY = "INSERT INTO avocado_share.file (id, title, last_changed, path) VALUES (?, ?, ?, ?)";
        public static final String UPDATE_QUERY = "UPDATE file SET title=?, last_changed=?, path=? WHERE id = ?";
        public static final String SEARCH_QUERY_START = "SELECT o.id, title, description, last_changed, creation_date, path FROM file AS f JOIN access_control AS o ON f.id = o.id WHERE ";
        public static final String SEARCH_QUERY_LIKE= " title LIKE (?) OR description LIKE (?)";
        public static final String SEARCH_QUERY_LINK = " OR";
        public static final String INSERT_UPLOADED_QUERY = "INSERT INTO avocado_share.uploaded_into (file_id, module_id) VALUES (?, ?)";
        public static final int INSERT_UPLOADED_QUERY_INDEX_FILE = 1;
        public static final int INSERT_UPLOADED_QUERY_INDEX_MODULE = 2;
        public static String UPDATE_UPLOADED = "UPDATE avocado_share.uploaded_into SET module_id = ? WHERE file_id = ?";
        public static final int UPDATE_UPLOADED_INDEX_MODULE = 1;
        public static final int UPDATE_UPLOADED_INDEX_FILE = 2;

    }

    //PERMISSION
    public static final String INSERT_OWNERSHIP = "INSERT INTO avocado_share.ownership(owner_id, object_id) VALUES (?, ?)";
    public static final int INSERT_OWNERSHIP_INDEX_OWNER = 1;
    public static final int INSERT_OWNERSHIP_INDEX_OBJECT = 2;

    public static final String INSERT_RIGHTS_QUERY = "INSERT INTO avocado_share.rights(object_id, owner_id, level) VALUES (?, ?, ?)";
    public static final String SELECT_OWNER_OF_OBJECT = "SELECT owner_id, object_id FROM avocado_share.ownership WHERE object_id=?";
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
            " UNION " + SELECT_ANONYMOUS_ACCESS_LEVEL;

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
            "FROM " + Group.TABLE + " AS g " +
            "JOIN avocado_share.rights AS r  " +
            "  ON r.owner_id = g.id " +
            "NATURAL JOIN avocado_share.access_level as l  " +
            "WHERE " +
            "  r.object_id = ? " +
            "  AND (l.readable <> false OR l.writable <> false OR l.manageable <> false ) " +
            "UNION " +
            "   SELECT o.owner_id, true, true, true, true FROM avocado_share.ownership AS o " +
            "   JOIN " + Group.TABLE + " AS og " +
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
            "   WHERE o.owner_id = ?";

    public static final int SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX = 1;
    public static final int SELECT_TARGETS_WITH_ACCESS_OWNER_ID_INDEX_2 = 3;
    public static final int SELECT_TARGETS_WITH_ACCESS_LEVEL_INDEX = 2;
}
