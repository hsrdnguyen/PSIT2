package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * File Data handler.
 */
public class FileDataHandler extends DataHandlerBase implements IFileDataHandler {

    @Override
    public String addFile(File file) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        try {
            file.setId(addAccessControlObject(file.getDescription()));
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.INSERT_QUERY);
            preparedStatement.setLong(1, Long.parseLong(file.getId()));
            preparedStatement.setString(2, file.getTitle());
            preparedStatement.setString(3, file.getDescription());
            preparedStatement.setString(4, file.getLastChanged().toString());
            connectionHandler.insertDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        addOwnership(Integer.parseInt(file.getOwnerId()), Integer.parseInt(file.getId()));
        return file.getId();
    }

    @Override
    public boolean deleteFile(File file) throws DataHandlerException {
        return deleteAccessControlObject(file.getId());
    }

    @Override
    public File getFile(String fileId) throws DataHandlerException {
        if (fileId == null) throw new IllegalArgumentException("fileId is null");
        //TODO @kunzlio1: noch implementieren dass auch auf Modul geschaut wird, weil titel nur in modul eindeutig
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.SELECT_BY_ID_QUERY);
            preparedStatement.setInt(1, Integer.parseInt(fileId));
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            File file = getFileFromSelectResultSet(resultSet);
            if(file != null) {
                String ownerId = getOwnerId(fileId);
                file.setOwnerId(ownerId);
            }
            return file;


        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<File> getFiles(List<String> ids) throws DataHandlerException {
        List<File> files = new ArrayList<>(ids.size());
        for (String id : ids) {
            File file = getFile(id);
            if(file != null) {
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public List<File> search(List<String> searchTerms) throws DataHandlerException {
        return null;
    }

    @Override
    public File getFileByTitleAndModule(String fileTitle, String moduleId) throws DataHandlerException {
        //TODO @kunzlio1: noch implementieren
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        if (connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.SELECT_BY_TITLE_QUERY);
            preparedStatement.setString(1, fileTitle);
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            return getFileFromSelectResultSet(resultSet);

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateFile(File file) throws DataHandlerException {
        //TODO @kunzlio1: Es gibt File Argumente die gar nicht in der DB gespeichert werden können
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.File.UPDATE_QUERY);
            preparedStatement.setString(1, file.getTitle());
            preparedStatement.setString(2, file.getDescription());
            preparedStatement.setString(3, file.getLastChanged().toString());
            preparedStatement.setString(4, file.getPath());
            preparedStatement.setString(5, file.getId());
            return getConnectionHandler().updateDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean grantAccess(String fileId, String userId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;

        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.SELECT_READING_ACCESS_LEVEL);
            ResultSet rs = connectionHandler.executeQuery(preparedStatement);

            String accessLevel = null;

            if (rs.next()) {
                accessLevel = rs.getString(1);
            } else {
                return false;
            }

            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.INSERT_RIGHTS_QUERY);
            preparedStatement.setInt(1, Integer.parseInt(fileId));
            preparedStatement.setInt(2, Integer.parseInt(userId));
            preparedStatement.setInt(3, Integer.parseInt(accessLevel));

            if (!connectionHandler.updateDataSet(preparedStatement)) return false;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return true;
    }

    private boolean addCategories(File file) {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if(categoryHandler == null) return false;
        if (!categoryHandler.addAccessObjectCategories(file)) return false;

        return true;
    }

    private boolean updateCategories(File oldFile, File changedFile) {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        return categoryHandler != null && categoryHandler.updateAccessObjectCategories(oldFile, changedFile);
    }

    private File getFileFromSelectResultSet(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                File file = FileFactory.getDefaultFile();
                file.setId(resultSet.getString(1));
                file.setTitle(resultSet.getString(2));
                file.setDescription(resultSet.getString(3));
                file.setLastChanged(resultSet.getDate(4));
                file.setCreationDate(resultSet.getDate(5));
                file.setPath(resultSet.getString(6));
                return file;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private ICategoryDataHandler getCategoryDataHandler() {
        try {
            return ServiceLocator.getService(ICategoryDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }

    private IDatabaseConnectionHandler getDatabaseHandler() {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }
}
