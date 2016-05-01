package ch.avocado.share.common.constants.sql;

/**
 * Created by coffeemakr on 01.05.16.
 */
public class AccessControlObjectConstants {
    //ACCESS CONTROL DATA OBJECT
    public static final int INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX = 1;
    public static final String INSERT_ACCESS_CONTROL_QUERY = "INSERT INTO avocado_share.access_control(id, creation_date, description) VALUES (DEFAULT, DEFAULT, ?) ";
    public static final String DELETE_ACCESS_CONTROL_QUERY = "DELETE FROM access_control WHERE id = ?";
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
    //PERMISSION
    public static final String INSERT_OWNERSHIP = "INSERT INTO avocado_share.ownership(owner_id, object_id) VALUES (?, ?)";
    public static final int INSERT_OWNERSHIP_INDEX_OWNER = 1;
    public static final int INSERT_OWNERSHIP_INDEX_OBJECT = 2;
    public static final String INSERT_RIGHTS_QUERY = "INSERT INTO avocado_share.rights(object_id, owner_id, level) VALUES (?, ?, ?)";
    public static final String SELECT_OWNER_OF_OBJECT = "SELECT owner_id, object_id FROM avocado_share.ownership WHERE object_id=?";
}
