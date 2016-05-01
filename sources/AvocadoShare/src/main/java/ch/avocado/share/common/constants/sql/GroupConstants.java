package ch.avocado.share.common.constants.sql;

/**
 * Group related queries
 */
    public final class GroupConstants {
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
