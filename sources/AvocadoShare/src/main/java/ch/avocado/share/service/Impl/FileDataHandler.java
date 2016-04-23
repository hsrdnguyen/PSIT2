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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            preparedStatement.setString(5, file.getExtension());
            preparedStatement.setString(6, file.getMimeType());
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
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.File.SELECT_BY_ID_QUERY);
            preparedStatement.setLong(1, Long.parseLong(fileId));
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            return getFileFromSelectResultSet(resultSet);
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

            String query = SQLQueryConstants.File.SEARCH_QUERY_START + SQLQueryConstants.File.SEARCH_QUERY_LIKE;

            for (String tmp : searchTerms) {
                // TODO @bergmsas: equals verwenden?
                if (!tmp.equals(searchTerms.get(0))) {
                    query += SQLQueryConstants.File.SEARCH_QUERY_LINK + SQLQueryConstants.File.SEARCH_QUERY_LIKE;
                }
            }
            PreparedStatement ps = connectionHandler.getPreparedStatement(query);
            int i = 1;
            for (String tmp : searchTerms) {
                ps.setString(i, "%"+tmp+"%");
                i++;
                ps.setString(i, "%"+tmp+"%");
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
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean updateFile(File file) throws DataHandlerException {
        if(file == null)throw new IllegalArgumentException("file is null");
        if(file.getId() == null) throw new IllegalArgumentException("file.id is null");
        File oldFileOnDb = getFile(file.getId());
        if (oldFileOnDb == null) throw new IllegalArgumentException("there's no such file on db");
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.File.UPDATE_QUERY);
            preparedStatement.setString(1, file.getTitle());
            preparedStatement.setTimestamp(2, new Timestamp(file.getLastChanged().getTime()));
            preparedStatement.setString(3, file.getPath());
            preparedStatement.setString(4, file.getExtension());
            preparedStatement.setString(5, file.getMimeType());
            preparedStatement.setLong(6, Long.parseLong(file.getId()));

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

    private List<Category> getFileCategoriesFromDb(String fileId) throws DataHandlerException {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if (fileId == null || fileId.trim().isEmpty()) return null;
        return categoryHandler.getAccessObjectAssignedCategories(fileId);
    }

    private boolean addFileCategoriesToDb(File file) throws DataHandlerException {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if (!categoryHandler.addAccessObjectCategories(file)) return false;
        return true;
    }

    private boolean updateFileCategoriesFromDb(File oldFile, File changedFile) throws DataHandlerException {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        return categoryHandler.updateAccessObjectCategories(oldFile, changedFile);
    }

    private File getFileFromSelectResultSet(ResultSet resultSet) throws DataHandlerException {
        try {
            if (resultSet.next()) {
                return createFileFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return null;
    }

    private List<File> getMultipleFilesFromResultSet(ResultSet resultSet) throws DataHandlerException {
        try {
            List<File> files = new ArrayList<>();
            while (resultSet.next()) {
                File file = FileFactory.getDefaultFile();
                file.setId(resultSet.getString(1));
                file.setTitle(resultSet.getString(2));
                file.setDescription(resultSet.getString(3));
                file.setLastChanged(new Date(resultSet.getTimestamp(4).getTime()));
                file.setCreationDate(new Date(resultSet.getTimestamp(5).getTime()));
                file.setPath(resultSet.getString(6));
                files.add(file);
            }
            return files;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private File createFileFromResultSet(ResultSet resultSet) throws SQLException, DataHandlerException {
        File file = FileFactory.getDefaultFile();
        file.setId(resultSet.getString(1));
        file.setTitle(resultSet.getString(2));
        file.setDescription(resultSet.getString(3));
        file.setLastChanged(new Date(resultSet.getTimestamp(4).getTime()));
        file.setCreationDate(new Date(resultSet.getTimestamp(5).getTime()));
        file.setPath(resultSet.getString(6));
        file.setModuleId(resultSet.getString(7));
        file.setOwnerId(resultSet.getString(8));
        file.setExtension(resultSet.getString(9));
        file.setMimeType(resultSet.getString(10));
        file.setCategories(getFileCategoriesFromDb(file.getId()));
        return file;
    }

    private ICategoryDataHandler getCategoryDataHandler() throws DataHandlerException {
        try {
            return ServiceLocator.getService(ICategoryDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
    }
}
