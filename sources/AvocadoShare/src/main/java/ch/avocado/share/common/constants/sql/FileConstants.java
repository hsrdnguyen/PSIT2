package ch.avocado.share.common.constants.sql;


public class FileConstants {

    public static final String INSERT_QUERY = "INSERT INTO avocado_share.file (id, title, last_changed, path, extension, mimetype) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE file SET title=?, last_changed=?, path=?, extension=?, mimetype=? WHERE id = ?";
    public static final String SEARCH_QUERY_START = "SELECT o.id, title, description, last_changed, creation_date, path FROM file AS f JOIN access_control AS o ON f.id = o.id WHERE ";
    public static final String SEARCH_QUERY_LIKE= " title LIKE (?) OR description LIKE (?)";
    public static final String SEARCH_QUERY_LINK = " OR";
    public static final String INSERT_UPLOADED_QUERY = "INSERT INTO avocado_share.uploaded_into (file_id, module_id) VALUES (?, ?)";
    public static final int INSERT_UPLOADED_QUERY_INDEX_FILE = 1;
    public static final int INSERT_UPLOADED_QUERY_INDEX_MODULE = 2;
    public static final String UPDATE_UPLOADED = "UPDATE avocado_share.uploaded_into SET module_id = ? WHERE file_id = ?";
    public static final int UPDATE_UPLOADED_INDEX_MODULE = 1;
    public static final int UPDATE_UPLOADED_INDEX_FILE = 2;


    private static final String SELECT_FILES = "" +
            "SELECT o.id, title, description, last_changed, creation_date, path, module_id, owner.owner_id, extension, mimetype " +
            "   FROM file AS f " +
            "JOIN access_control AS o " +
            "   ON f.id = o.id " +
            "JOIN avocado_share.uploaded_into AS u " +
            "   ON u.file_id = f.id " +
            "LEFT JOIN avocado_share.ownership AS owner " +
            "   ON owner.object_id = f.id ";
    public static final String SELECT_BY_TITLE_QUERY_AND_MODULE = SELECT_FILES + "WHERE title = ? AND module_id = ?";
    public static final String SELECT_BY_ID_QUERY = SELECT_FILES + "WHERE o.id = ?";
}
