package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
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

    public void addFileToModule(File file) throws DataHandlerException {
        long fileId = Long.parseLong(file.getId());
        long moduleId = Long.parseLong(file.getModuleId());
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.INSERT_UPLOADED_QUERY);
            statement.setLong(SQLQueryConstants.File.INSERT_UPLOADED_QUERY_INDEX_FILE, fileId);
            statement.setLong(SQLQueryConstants.File.INSERT_UPLOADED_QUERY_INDEX_MODULE, moduleId);
            statement.execute();
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    public boolean changeFileAssociatedModule(File file) throws DataHandlerException {
        long fileId = Long.parseLong(file.getId());
        long moduleId = Long.parseLong(file.getModuleId());
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.UPDATE_UPLOADED);
            statement.setLong(SQLQueryConstants.File.UPDATE_UPLOADED_INDEX_FILE, fileId);
            statement.setLong(SQLQueryConstants.File.UPDATE_UPLOADED_INDEX_MODULE, moduleId);
            return connectionHandler.updateDataSet(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public String addFile(File file) throws DataHandlerException {
        file.setId(addAccessControlObject(file));
        insertFileData(file);
        addFileToModule(file);
        if(!addFileCategoriesToDb(file)) throw new DataHandlerException("Unable to add categories.");
        return file.getId();
    }

    private void insertFileData(File file) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        long fileId = Long.parseLong(file.getId());
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.INSERT_QUERY);
            preparedStatement.setLong(1, fileId);
            preparedStatement.setString(2, file.getTitle());
            preparedStatement.setTimestamp(3, new Timestamp(file.getLastChanged().getTime()));
            preparedStatement.setString(4, file.getPath());
            connectionHandler.insertDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean deleteFile(File file) throws DataHandlerException {
        if(file == null) throw new IllegalArgumentException("file is null");
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
            preparedStatement.setLong(1, Long.parseLong(fileId));
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            File file = getFileFromSelectResultSet(resultSet);
            if (file != null) {
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
        if(idList == null) throw new IllegalArgumentException("idList is null");
        List<File> files = new ArrayList<>(idList.size());
        for (String id : idList) {
            File file = getFile(id);
            if (file != null) {
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
                if (tmp != searchTerms.get(0)) {
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
        if (fileTitle == null) throw new IllegalArgumentException("fileTitle is null");
        if (moduleId == null) throw new IllegalArgumentException("moduleId is null");
        long parsedModuleId = Long.parseLong(moduleId);
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        if (connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.SELECT_BY_TITLE_QUERY_AND_MODULE);
            preparedStatement.setString(1, fileTitle);
            preparedStatement.setLong(2, parsedModuleId);
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            return getFileFromSelectResultSet(resultSet);

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateFile(File file) throws DataHandlerException {
        if(file == null)throw new IllegalArgumentException("file is null");
        if(file.getId() == null) throw new IllegalArgumentException("file.id is null");
        File oldFileOnDb = getFile(file.getId());
        if (oldFileOnDb == null) throw new IllegalArgumentException("there's no such file on db");
        //TODO @kunzlio1: Es gibt File Argumente die gar nicht in der DB gespeichert werden können
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.File.UPDATE_QUERY);
            preparedStatement.setString(1, file.getTitle());
            preparedStatement.setTimestamp(2, new Timestamp(file.getLastChanged().getTime()));
            preparedStatement.setString(3, file.getPath());
            preparedStatement.setLong(4, Long.parseLong(file.getId()));
            if (!getConnectionHandler().updateDataSet(preparedStatement)) {
                return false;
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        if(!changeFileAssociatedModule(file)) {
            return false;
        }
        if (!updateFileCategoriesFromDb(oldFileOnDb, file)){
            return false;
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

    private List<Category> getFileCategoriesFromDb(String fileId){
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if (fileId == null || fileId.trim().isEmpty() || categoryHandler == null) return null;
        return categoryHandler.getAccessObjectAssignedCategories(fileId);
    }

    private boolean addFileCategoriesToDb(File file) {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if (categoryHandler == null) return false;
        if (!categoryHandler.addAccessObjectCategories(file)) return false;

        return true;
    }

    private boolean updateFileCategoriesFromDb(File oldFile, File changedFile) {
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
        file.setModuleId(resultSet.getString(7));
        file.setCategories(getFileCategoriesFromDb(file.getId()));
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
