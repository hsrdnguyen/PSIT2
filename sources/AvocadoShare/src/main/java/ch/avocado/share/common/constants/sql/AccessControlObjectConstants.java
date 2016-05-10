package ch.avocado.share.common.constants.sql;

import static ch.avocado.share.common.constants.sql.Tables.*;

/**
 * Created by coffeemakr on 01.05.16.
 */
public class AccessControlObjectConstants {
    //ACCESS CONTROL DATA OBJECT
    public static final int INSERT_ACCESS_CONTROL_QUERY_DESCRIPTION_INDEX = 1;
    public static final String INSERT_ACCESS_CONTROL_QUERY = "INSERT INTO " + BASE_OBJECT_TABLE + "(id, creation_date, description) VALUES (DEFAULT, DEFAULT, ?) ";
    public static final String DELETE_ACCESS_CONTROL_QUERY = "DELETE FROM access_control WHERE id = ?";
    public static final String UPDATE_ACCESS_CONTROL_DESCRIPTION = "UPDATE " + BASE_OBJECT_TABLE + " SET description = ? WHERE id = ?";
    public static final int UPDATE_ACCESS_CONTROL_DESCRIPTION_DESCRIPTION_INDEX = 1;
    public static final int UPDATE_ACCESS_CONTROL_DESCRIPTION_ID_INDEX = 2;
    public static final String UPDATE_OWNERSHIP = "UPDATE " + OWNERSHIP_TABLE + " SET owner_id = ? WHERE object_id = ?";
    public static final int UPDATE_OWNERSHIP_INDEX_OWNER = 1;
    public static final int UPDATE_OWNERSHIP_INDEX_OBJECT = 2;
    public static final String DELETE_OWNERSHIP = "DELETE FROM " + OWNERSHIP_TABLE + " WHERE object_id = ?";
    public static final int DELETE_OWNERSHIP_INDEX_OBJECT = 1;
    public static final String SELECT_OWNED_IDS = "SELECT object_id FROM " + OWNERSHIP_TABLE + " WHERE owner_id = ?";
    public static final int SELECT_OWNED_IDS_OWNER_INDEX = 1;
    //PERMISSION
    public static final String INSERT_OWNERSHIP = "INSERT INTO " + OWNERSHIP_TABLE + "(owner_id, object_id) VALUES (?, ?)";
    public static final int INSERT_OWNERSHIP_INDEX_OWNER = 1;
    public static final int INSERT_OWNERSHIP_INDEX_OBJECT = 2;
    public static final String SELECT_OWNER_OF_OBJECT = "SELECT owner_id, object_id FROM " + OWNERSHIP_TABLE + " WHERE object_id=?";
}
