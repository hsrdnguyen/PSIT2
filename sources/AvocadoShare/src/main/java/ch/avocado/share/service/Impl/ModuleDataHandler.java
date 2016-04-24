package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ModuleDataHandler extends DataHandlerBase implements IModuleDataHandler {
    @Override
    public String addModule(Module module) throws DataHandlerException {
        if(module == null) throw new IllegalArgumentException("module is null");
        if(module.getOwnerId() == null) throw new IllegalArgumentException("module.ownerId is null");
        String id = addAccessControlObject(module);
        module.setId(id);
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Module.INSERT_QUERY);
            statement.setLong(SQLQueryConstants.Module.INSERT_QUERY_ID_INDEX, Long.parseLong(id));
            statement.setString(SQLQueryConstants.Module.INSERT_QUERY_NAME_INDEX, module.getName());
            getConnectionHandler().insertDataSet(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        addCategories(module);
        return module.getId();
    }

    @Override
    public boolean deleteModule(Module module) throws DataHandlerException {
        return deleteAccessControlObject(module.getId());
    }

    private Module getModuleFromResult(ResultSet resultSet) throws DataHandlerException {
        String id, name, description, ownerId;
        Date creationDate;
        try {
            if (!resultSet.next()) return null;
            id = Long.toString(resultSet.getLong(SQLQueryConstants.Module.RESULT_INDEX_ID));
            name = resultSet.getString(SQLQueryConstants.Module.RESULT_INDEX_NAME);
            description = resultSet.getString(SQLQueryConstants.Module.RESULT_INDEX_DESCRIPTION);
            creationDate = resultSet.getDate(SQLQueryConstants.Module.RESULT_INDEX_CREATION_DATE);
            ownerId = Long.toString(resultSet.getLong(SQLQueryConstants.Module.RESULT_INDEX_OWNER));
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return new Module(id, new ArrayList<Category>(), creationDate, 0.0f, ownerId, description, name, new ArrayList<>());
    }

    private void getCategories(Module module) throws DataHandlerException {
        ICategoryDataHandler categoryDataHandler;
        try {
            categoryDataHandler = ServiceLocator.getService(ICategoryDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
        List<Category> categories = categoryDataHandler.getAccessObjectAssignedCategories(module.getId());
        module.setCategories(categories);
    }

    private void updateCategories(Module module) throws DataHandlerException {
        ICategoryDataHandler categoryDataHandler;
        try {
            categoryDataHandler = ServiceLocator.getService(ICategoryDataHandler.class);
        }catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
        Module oldModule = getModule(module.getId());
        if(!categoryDataHandler.updateAccessObjectCategories(oldModule, module)) {
            throw new DataHandlerException("Failed to update categories of an existing module");
        }
    }

    private void addCategories(Module module) throws  DataHandlerException {
        ICategoryDataHandler categoryDataHandler;
        try {
            categoryDataHandler = ServiceLocator.getService(ICategoryDataHandler.class);
        }catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
        categoryDataHandler.addAccessObjectCategories(module);
    }

    @Override
    public Module getModule(String moduleId) throws DataHandlerException {
        // TODO: categoeries, rating and ownerId
        ResultSet resultSet;
        long moduleIdAsLong = Long.parseLong(moduleId);
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Module.SELECT_QUERY);
            statement.setLong(SQLQueryConstants.Module.SELECT_QUERY_INDEX_ID, moduleIdAsLong);
            resultSet = getConnectionHandler().executeQuery(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        Module module = getModuleFromResult(resultSet);
        if(module != null) {
            getCategories(module);
            module.setFileIds(getFileIds(moduleIdAsLong));
        }
        return module;
    }


    private List<String> getFileIds(long moduleId) throws DataHandlerException {
        List<String> fileIds = new LinkedList<>();
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        try {
            PreparedStatement statement = connectionHandler.getPreparedStatement(SQLQueryConstants.Module.SELECT_FILES);
            statement.setLong(SQLQueryConstants.Module.SELECT_FILES_INDEX_MODULE, moduleId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                fileIds.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return fileIds;
    }

    @Override
    public List<Module> getModules(Collection<String> ids) throws DataHandlerException {
        List<Module> modules = new ArrayList<>(ids.size());
        for (String id : ids) {
            if(id == null) {
                continue;
            }
            Module module = getModule(id);
            if (module != null) {
                modules.add(module);
            }
        }
        return modules;
    }

    @Override
    public boolean updateModule(Module module) throws DataHandlerException {
        boolean success;
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Module.UPDATE_QUERY);
            statement.setInt(SQLQueryConstants.Module.UPDATE_QUERY_INDEX_ID, Integer.parseInt(module.getId()));
            statement.setString(SQLQueryConstants.Module.UPDATE_QUERY_INDEX_NAME, module.getName());
            success = getConnectionHandler().updateDataSet(statement);
            if (success) {
                success = updateObject(module);
            }
            if (success) {
                updateCategories(module);
            }
            return success;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }
}
