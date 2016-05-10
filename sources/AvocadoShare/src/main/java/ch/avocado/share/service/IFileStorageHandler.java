package ch.avocado.share.service;

import ch.avocado.share.service.exceptions.FileStorageException;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
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
     * Writes the content read by teh input stream to a disk file.
     * @param file The file to store.
     * @return The reference to the file.
     * @throws FileStorageException
     */
    String saveFile(File file) throws FileStorageException;

    /**
     * Gets the file on the given location
     * @param filepath reference to the file
     * @return input stream for the file
     */
    InputStream readFile(String filepath) throws FileStorageException;

    /**
     * Checks if a file on the given location exists
     * @param filepath reference to the file
     * @return true if the file exists
     */
    boolean fileExists(String filepath) throws FileStorageException;

    /**
     *Gets the contentType (type of file: pdf, jpg, docx) of the file
     * @param filepath path to the file
     * @param name
     * @return contentType as string
     * @throws FileStorageException
     */
    String getContentType(String filepath, String name) throws FileStorageException;

    long getFileSize(String filepath) throws FileStorageException;
}
