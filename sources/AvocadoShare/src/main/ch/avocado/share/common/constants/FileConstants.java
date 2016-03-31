package ch.avocado.share.common.constants;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileConstants {
    public static final String FILE_DESTINATION_ON_SERVER = "C:\\avocadoshare\\documents";
    public static final String FILE_TEMP_DESTINATION_ON_SERVER = "C:\\avocadoshare\\TMP";

    /**
     * The maximal size of file in bytes.
     * -1 indicates there is no limit.
     */
    public static final long MAX_FILE_SIZE = 5000L * 1024L * 1024L;
    /**
     * The size of the biggest file stored in memory in bytes.
     */
    public static final int MAX_MEM_SIZE = 5000 * 1024;


}
