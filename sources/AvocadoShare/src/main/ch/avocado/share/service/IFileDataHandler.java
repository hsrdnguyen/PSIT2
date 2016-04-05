package ch.avocado.share.service;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IFileDataHandler {

    /**
     * adds the given File to the Database and saves it on the server
     * @param file file to be added
     * @return the file id
     */
    String addFile(File file) throws DataHandlerException;

    /**
     * deletes the given file rom the database and the server
     * @param file file to be deleted
     */
    boolean deleteFile(File file) throws DataHandlerException;

    /**
     * Returns the file from the server with the data from the database
     * @param fileId id of the requested file
     * @return File from server
     */
    File getFile(String fileId) throws DataHandlerException;


    List<File> getFiles(List<String> ids) throws DataHandlerException;

    /**
     * @param fileTitle its title
     * @param moduleId module id in which it was uploaded
     * @return The file object
     */
    File getFileByTitleAndModule(String fileTitle, String moduleId) throws DataHandlerException;

    /**
     * updates a file on the server and database and creates a new version number
     * @param file file with updated data
     * @return {@code true} if the file was found an has been updated.
     */
    boolean updateFile(File file) throws DataHandlerException;

    /**
     * Grants the given user reading access on a file with the given id
     * @param fileId file to give access to
     * @param userId user to give access
     * @return {@code true} if the file was found an has been updated.
     */
    boolean grantAccess(String fileId, String userId) throws DataHandlerException;

}

