package ch.avocado.share.service;

import ch.avocado.share.service.exceptions.FileStorageException;
import org.apache.commons.fileupload.FileItem;

import java.io.InputStream;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IFileStorageHandler {

    /**
     * Saves a given file to the server
     * @param tempUploadedFile the file resource
     * @return Path to the file-location
     */
    String saveFile(FileItem tempUploadedFile) throws FileStorageException;

    /**
     * Gets the file on the given location
     * @param reference reference to the file
     * @return input stream for the file
     */
    InputStream readFile(String reference) throws FileStorageException;

    /**
     * Checks if a file on the given location exists
     * @param reference reference to the file
     * @return true if the file exists
     */
    boolean fileExists(String reference) throws FileStorageException;

    String getContentType(String reference) throws FileStorageException;

    long getFileSize(String reference) throws FileStorageException;
}
