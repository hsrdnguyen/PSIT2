package ch.avocado.share.service;

import ch.avocado.share.model.data.File;
import org.apache.commons.fileupload.FileItem;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IFileStorageHandler {

    /**
     * Saves a given file to the server
     * @param dbFIle file to be saved
     * @return Path to the file-location
     */
    String saveFile(FileItem fi, File dbFIle);

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
