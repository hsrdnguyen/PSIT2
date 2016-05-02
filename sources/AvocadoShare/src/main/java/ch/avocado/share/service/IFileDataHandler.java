package ch.avocado.share.service;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

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
    boolean deleteFile(File file) throws ObjectNotFoundException, DataHandlerException;

    /**
     * Returns the file from the server with the data from the database
     * @param fileId id of the requested file
     * @return File from server
     */
    File getFile(String fileId) throws ObjectNotFoundException, DataHandlerException;

    /**
     * Returns a list of files
     * @param ids list of id's of files that should be returned
     * @return List of requested files
     * @throws DataHandlerException
     */
    List<File> getFiles(List<String> ids) throws DataHandlerException;

    /**
     * Searches for files in the database that matches the given strings
     * @param searchTerms searchterms the file HAS TO match
     * @return All files that match the search strings
     */
    List<File> search(List<String> searchTerms) throws DataHandlerException;

    /**
     * @param fileTitle its title
     * @param moduleId module id in which it was uploaded
     * @return The file object
     */
    File getFileByTitleAndModule(String fileTitle, String moduleId) throws DataHandlerException, ObjectNotFoundException;

    /**
     * updates a file on the server and database and creates a new version number
     * @param file file with updated data
     * @return {@code true} if the file was found an has been updated.
     */
    boolean updateFile(File file) throws DataHandlerException, ObjectNotFoundException;
}

