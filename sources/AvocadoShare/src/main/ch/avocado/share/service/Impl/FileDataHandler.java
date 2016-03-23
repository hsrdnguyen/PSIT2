package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IFileDataHandler;

import java.sql.SQLException;

/**
 * Created by bergm on 22/03/2016.
 */
public class FileDataHandler implements IFileDataHandler {
    public IDatabaseConnectionHandler databaseConnection;

    @Override
    public boolean addFile(File file) {
        try {
            databaseConnection.insertDataSet(String.format(SQLQueryConstants.INSERT_FILE_QUERY, "NULL", file.getTitle(), file.getDescription(), file.getLastChanged()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteFile(File file) {
        return false;
    }

    @Override
    public File getFile(String fileId) {
        return null;
    }

    @Override
    public boolean updateFile(File file) {
        return false;
    }
}
