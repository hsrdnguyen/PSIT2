package ch.avocado.share.service;

import ch.avocado.share.model.data.File;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IFileDataHandler {

    /**
     * Saves a given file to the server
     * @param file file to be saved
     * @return Path to the file-location
     */
    String saveFile(File file);

    /**
     * Gets the file on the given location
     * @param path relative path to the file without filename
     * @param fileName name of the file on the server
     * @return file that is found on the given location
     */
    File getFile(String path, String fileName);

    /**
     * Checks if a file on the given location exists
     * @param path relative path to the file without filename
     * @param fileName name of the file on the server
     * @return true if the file exists
     */
    boolean fileExists(String path, String fileName);
}
