package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import org.bouncycastle.math.raw.Mod;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static ch.avocado.share.common.constants.sql.ModuleConstants.*;

public class ModuleDataHandler extends DataHandlerBase implements IModuleDataHandler {

    @Override
    public String addModule(Module module) throws DataHandlerException {
        if(module == null) throw new IllegalArgumentException("module is null");
        if(module.getOwnerId() == null) throw new IllegalArgumentException("module.ownerId is null");
        String id = addAccessControlObject(module);
        module.setId(id);
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(INSERT_QUERY);
            statement.setLong(INSERT_QUERY_ID_INDEX, Long.parseLong(id));
            statement.setString(INSERT_QUERY_NAME_INDEX, module.getName());
            getConnectionHandler().insertDataSet(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        addCategories(module);
        return module.getId();
    }

    @Override
    public void deleteModule(Module module) throws DataHandlerException, ObjectNotFoundException {
        deleteAccessControlObject(module);
    }

    private Module getModuleFromResult(ResultSet resultSet) throws DataHandlerException {
        String id, name, description, ownerId;
        Date creationDate;
        try {
            id = Long.toString(resultSet.getLong(RESULT_INDEX_ID));
            name = resultSet.getString(RESULT_INDEX_NAME);
            description = resultSet.getString(RESULT_INDEX_DESCRIPTION);
            creationDate = resultSet.getDate(RESULT_INDEX_CREATION_DATE);
            ownerId = Long.toString(resultSet.getLong(RESULT_INDEX_OWNER));
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

    private void updateCategories(Module module) throws DataHandlerException, ObjectNotFoundException {
        ICategoryDataHandler categoryDataHandler;
        try {
            categoryDataHandler = ServiceLocator.getService(ICategoryDataHandler.class);
        }catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
        categoryDataHandler.updateAccessObjectCategories(module);
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
    public Module getModule(String moduleId) throws DataHandlerException, ObjectNotFoundException {
        // TODO: categoeries, rating and ownerId
        ResultSet resultSet;
        long moduleIdAsLong = Long.parseLong(moduleId);
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SELECT_QUERY);
            statement.setLong(SELECT_QUERY_INDEX_ID, moduleIdAsLong);
            resultSet = getConnectionHandler().executeQuery(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        try {
            if(!resultSet.next()) throw new ObjectNotFoundException(Module.class, moduleId);
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
            PreparedStatement statement = connectionHandler.getPreparedStatement(SELECT_FILES);
            statement.setLong(SELECT_FILES_INDEX_MODULE, moduleId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                fileIds.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return fileIds;
    }

    private List<Module> getMultipleModulesFromResult(ResultSet resultSet) throws DataHandlerException {
        List<Module> modules = new LinkedList<>();
        try {
            while (resultSet.next()) {
                modules.add(getModuleFromResult(resultSet));
            }
        }catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return modules;
    }

    @Override
    public List<Module> getModules(Collection<String> ids) throws DataHandlerException {
        if (ids == null) throw new IllegalArgumentException("ids is null");
        if (ids.isEmpty()) return new ArrayList<>();
        String query = SELECT_BY_ID_LIST + getIdList(ids);
        IDatabaseConnectionHandler connectionHandler = getConnectionHandler();
        ResultSet result;
        try {
            PreparedStatement preparedStatement = connectionHandler.getPreparedStatement(query);
            result = connectionHandler.executeQuery(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return getMultipleModulesFromResult(result);
    }

    @Override
    public void updateModule(Module module) throws DataHandlerException, ObjectNotFoundException {
        if(module == null) throw new IllegalArgumentException("module is null");
        if(module.getId() == null) throw new IllegalArgumentException("module.id is null");
        long moduleId;
        try {
            moduleId = Long.parseLong(module.getId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("module.id is not a number");
        }
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(UPDATE_QUERY);
            statement.setLong(UPDATE_QUERY_INDEX_ID, moduleId);
            statement.setString(UPDATE_QUERY_INDEX_NAME, module.getName());
            if(!getConnectionHandler().updateDataSet(statement)) {
                throw new ObjectNotFoundException(Module.class, moduleId);
            }
            updateObject(module);
            updateCategories(module);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }
}
