package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IFileDataHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import java.sql.SQLException;

/**
 * Created by bergm on 22/03/2016.
 */
public class FileDataHandler implements IFileDataHandler {

    @Override
    public String addFile(File file) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        String fileId = "";
        if(connectionHandler == null) return fileId;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.INSERT_ACCESS_CONTROL_QUERY);
            fileId = connectionHandler.insertDataSet(preparedStatement);
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.INSERT_FILE_QUERY);
            preparedStatement.setString(1, fileId);
            preparedStatement.setString(2, file.getTitle());
            preparedStatement.setString(3, file.getDescription());
            preparedStatement.setString(4, file.getLastChanged().toString());
            connectionHandler.insertDataSet(preparedStatement);
        } catch (SQLException e) {
            return "";
        }

        return fileId;
    }

    @Override
    public boolean deleteFile(File file) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.DELETE_FILE_QUERY);
            preparedStatement.setString(1, file.getId());
            if (!connectionHandler.deleteDataSet(preparedStatement)) return false;
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.DELETE_ACCESS_CONTROL_QUERY);
            preparedStatement.setString(1, file.getId());
            if (!connectionHandler.deleteDataSet(preparedStatement)) return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public File getFileById(String fileId) {
        //TODO @kunzlio1: noch implementieren dass auch auf Modul geschaut wird, weil titel nur in modul eindeutig
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.SELECT_FILE_BY_ID_QUERY);
            preparedStatement.setString(1, fileId);
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            return getFileFromSelectResultSet(resultSet);

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public File getFileByTitle(String fileTitle) {
        //TODO @kunzlio1: noch implementieren dass auch auf Modul geschaut wird, weil titel nur in modul eindeutig
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.SELECT_FILE_BY_TITLE_QUERY);
            preparedStatement.setString(1, fileTitle);
            ResultSet resultSet = connectionHandler.executeQuery(preparedStatement);
            return getFileFromSelectResultSet(resultSet);

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateFile(File file) {
        //TODO @kunzlio1: Es gibt File Argumente die gar nicht in der DB gespeichert werden können
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQLQueryConstants.UPDATE_FILE_QUERY);
            preparedStatement.setString(1, file.getTitle());
            preparedStatement.setString(2, file.getDescription());
            //TODO @kunzlio1: fragen ob last_changed autom. ges. wird?
            preparedStatement.setString(3, file.getLastChanged().toString());
            preparedStatement.setString(4, file.getPath());
            preparedStatement.setString(5, file.getId());
            if (!connectionHandler.updateDataSet(preparedStatement)) return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private boolean addCategories(File file){
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if(categoryHandler == null)
            return false;
        if (!categoryHandler.addAccessObjectCategories(file))
            return false;

        return true;
    }

    private boolean updateCategories(File oldFile, File changedFile){
        ICategoryDataHandler categoryHandler = getCategoryDataHandler();
        if(categoryHandler == null)
            return false;
        if (!categoryHandler.updateAccessObjectCategories(oldFile, changedFile))
            return false;

        return true;
    }

    private File getFileFromSelectResultSet(ResultSet resultSet){
        File file = FileFactory.getDefaultFile();
        try {
            file.setId(resultSet.getString(1));
            file.setTitle(resultSet.getString(2));
            file.setDescription(resultSet.getString(3));
            file.setLastChanged(resultSet.getDate(4));
            file.setCreationDate(resultSet.getDate(5));
            file.setPath(resultSet.getString(6));
        }catch (SQLException e){
            return null;
        }
        return file;
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
