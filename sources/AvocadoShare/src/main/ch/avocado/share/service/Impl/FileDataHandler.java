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

import java.sql.*;

import java.util.*;
import java.util.Date;

/**
 * File Data handler.
 */
public class FileDataHandler extends DataHandlerBase implements IFileDataHandler {

    @Override
    public String addFile(File file) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        try {
            file.setId(addAccessControlObject(file));
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.INSERT_QUERY);
            preparedStatement.setLong(1, Long.parseLong(file.getId()));
            preparedStatement.setString(2, file.getTitle());
            preparedStatement.setTimestamp(3, new Timestamp(file.getLastChanged().getTime()));
            preparedStatement.setString(4, file.getPath());
            connectionHandler.insertDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
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
    public List<File> getFiles(List<String> idList) throws DataHandlerException {
        List<File> files = new ArrayList<>(idList.size());
        for (String id : idList) {
            File file = getFile(id);
            if(file != null) {
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public List<File> search(List<String> searchTerms) throws DataHandlerException {
        try {
            IDatabaseConnectionHandler connectionHandler = getConnectionHandler();

            String query = SQLQueryConstants.File.SEARCH_QUERY_START;
            query += query + SQLQueryConstants.File.SEARCH_QUERY_LIKE;

            for (String tmp : searchTerms) {
                // TODO @bergmsas: equals verwenden?
                if (tmp != searchTerms.get(0))
                {
                    query += SQLQueryConstants.File.SEARCH_QUERY_LINK + SQLQueryConstants.File.SEARCH_QUERY_LIKE;
                }
            }
            PreparedStatement ps = connectionHandler.getPreparedStatement(query);
            int i = 1;
            for (String tmp : searchTerms) {
                ps.setString(i, tmp);
                i++;
                ps.setString(i, tmp);
                i++;
            }
            ResultSet rs = connectionHandler.executeQuery(ps);
            return getMultipleFilesFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File getFileByTitleAndModule(String fileTitle, String moduleId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        if (connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.SELECT_BY_TITLE_QUERY_AND_MODULE);
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
            preparedStatement.setTimestamp(2, new Timestamp(file.getLastChanged().getTime()));
            preparedStatement.setString(3, file.getPath());
            preparedStatement.setLong(4, Long.parseLong(file.getId()));
            if(!getConnectionHandler().updateDataSet(preparedStatement)) {
                return false;
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return updateObject(file);
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
                return createFileFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private List<File> getMultipleFilesFromResultSet(ResultSet resultSet) {
        try {
            List<File> files = new ArrayList<>();
            while (resultSet.next()) {
                files.add(createFileFromResultSet(resultSet));
            }
            return files;
        } catch (SQLException e) {
            return null;
        }
    }

    private File createFileFromResultSet(ResultSet resultSet) throws SQLException {
        File file = FileFactory.getDefaultFile();
        file.setId(resultSet.getString(1));
        file.setTitle(resultSet.getString(2));
        file.setDescription(resultSet.getString(3));
        file.setLastChanged(new Date(resultSet.getTimestamp(4).getTime()));
        file.setCreationDate(new Date(resultSet.getTimestamp(5).getTime()));
        file.setPath(resultSet.getString(6));
        return file;
    }

    private ICategoryDataHandler getCategoryDataHandler() {
        try {
            return ServiceLocator.getService(ICategoryDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }
}
