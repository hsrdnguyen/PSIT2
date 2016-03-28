package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IFileDataHandler;

import java.util.List;

import java.sql.SQLException;

/**
 * Created by bergm on 22/03/2016.
 */
public class FileDataHandler implements IFileDataHandler {
    public IDatabaseConnectionHandler databaseConnection;

    @Override
    public String addFile(File file) {
        try {
            //TODO databaseConnection.insertDataSet(String.format(SQLQueryConstants.INSERT_FILE_QUERY, "NULL", file.getTitle(), file.getDescription(), file.getLastChanged()));
            //TODO @kunzlio1: Irgendwie AccesControlObject Id zurück geben...
            //addCategories(file);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
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
    public File getFileByTitle(String fileTitle) {
        //TODO @kunzlio1: noch implementieren dass auch auf Modul geschaut wird, weil titel nur in modul eindeutig
        return null;
    }

    @Override
    public boolean updateFile(File file) {
        return false;
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

    private ICategoryDataHandler getCategoryDataHandler() {
        try {
            return ServiceLocator.getService(ICategoryDataHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }
}
