package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
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

import static ch.avocado.share.common.constants.sql.FileConstants.*;

/**
 * File Data handler.
 */
public class FileDataHandler extends DataHandlerBase implements IFileDataHandler {

    private void addFileToModule(File file) throws DataHandlerException {
        long fileId = Long.parseLong(file.getId());
        long moduleId = Long.parseLong(file.getModuleId());
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(INSERT_UPLOADED_QUERY);
            statement.setLong(INSERT_UPLOADED_QUERY_INDEX_FILE, fileId);
            statement.setLong(INSERT_UPLOADED_QUERY_INDEX_MODULE, moduleId);
            statement.execute();
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private boolean changeFileAssociatedModule(File file) throws DataHandlerException {
        long fileId = Long.parseLong(file.getId());
        long moduleId = Long.parseLong(file.getModuleId());
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(UPDATE_UPLOADED);
            statement.setLong(UPDATE_UPLOADED_INDEX_FILE, fileId);
            statement.setLong(UPDATE_UPLOADED_INDEX_MODULE, moduleId);
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
            preparedStatement = connectionHandler.getPreparedStatement(INSERT_QUERY);
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
        if(file.getId() == null) throw new IllegalArgumentException("file.id is null");
        return deleteAccessControlObject(file.getId());
    }

    @Override
    public File getFile(String fileId) throws DataHandlerException {
        if (fileId == null) throw new IllegalArgumentException("fileId is null");
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SELECT_BY_ID_QUERY);
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

            String query = SEARCH_QUERY_START + SEARCH_QUERY_LIKE;

            for (String tmp : searchTerms) {
                // TODO @bergmsas: equals verwenden?
                if (!tmp.equals(searchTerms.get(0))) {
                    query += SEARCH_QUERY_LINK + SEARCH_QUERY_LIKE;
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
            preparedStatement = connectionHandler.getPreparedStatement(SELECT_BY_TITLE_QUERY_AND_MODULE);
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
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnectionHandler().getPreparedStatement(UPDATE_QUERY);
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
        if (!updateFileCategoriesFromDb(file)){
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

    private boolean updateFileCategoriesFromDb(File changedFile) throws DataHandlerException {
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        return categoryHandler.updateAccessObjectCategories(changedFile);
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
                File file = createFileFromResultSet(resultSet);
                assert file != null;
                files.add(file);
            }
            return files;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private File createFileFromResultSet(ResultSet resultSet) throws SQLException, DataHandlerException {
        String id = resultSet.getString(1);
        String title = resultSet.getString(2);
        String description = resultSet.getString(3);
        Date lastChanged = resultSet.getTimestamp(4);
        Date creation = resultSet.getTimestamp(5);
        String path = resultSet.getString(6);
        String moduleId = resultSet.getString(7);
        String ownerId = resultSet.getString(8);
        String extension = resultSet.getString(9);
        String mimeType = resultSet.getString(10);
        List<Category> categories = getFileCategoriesFromDb(id);
        return new File(id, categories, creation, new Rating(), ownerId, description,
                        title, path, lastChanged, extension, moduleId, mimeType);

    }

    private ICategoryDataHandler getCategoryDataHandler() throws DataHandlerException {
        try {
            return ServiceLocator.getService(ICategoryDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
    }
}
