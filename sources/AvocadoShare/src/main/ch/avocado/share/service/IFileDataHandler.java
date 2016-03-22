package ch.avocado.share.service;

import ch.avocado.share.model.data.File;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IFileDataHandler {

    /**
     * adds the given File to the Database and saves it on the server
     * @param file file to be added
     * @return true if it could be added
     */
    boolean addFile(File file);

    /**
     * deletes the given file rom the database and the server
     * @param file file to be deleted
     * @return true if was successfully deleted
     */
    boolean deleteFile(File file);

    /**
     * Returns the file from the server with the data from the database
     * @param fileId id of the requested file
     * @return File from server
     */
    File getFile(String fileId);

    /**
     * updates a file on the server and database and creates a new version number
     * @param file file with updated data
     * @return true if it was successfully updated
     */
    boolean updateFile(File file);

}

