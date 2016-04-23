package ch.avocado.share.common;

import org.apache.commons.io.FilenameUtils;

/**
 * Created by coffeemakr on 23.04.16.
 */
public class Filename {
    public static String getExtension(String filename) {
        if(filename.charAt(0) == '.') {
            filename = filename.substring(1);
        }
        return FilenameUtils.getExtension(filename);
    }
}
