package ch.avocado.share.common.constants.sql;

/**
 * Table names.
 */
public class Tables {
    static final String SCHEMA = "avocado_share.";

    static final String FILE_TABLE =  SCHEMA + "file";

    static final String UPLOADED_TABLE = SCHEMA + "uploaded_into";

    static final String RIGHTS_TABLE = SCHEMA + "rights";

    static final String RIGHTS_LEVEL_TABLE = SCHEMA + "access_level";

    static final String DEFAULT_ACCESS_TABLE = SCHEMA + "default_access";

    static final String USER_TABLE = SCHEMA + "identity";

    static final String OWNERSHIP_TABLE = SCHEMA + "ownership";

    static final String BASE_OBJECT_TABLE = SCHEMA + "access_control";

    static final String CATEGORY_TABLE = SCHEMA + "category";

    static final String RATING_TABLE = SCHEMA + "rating";

    static final String GROUP_TABLE = SCHEMA + "access_group";
}
