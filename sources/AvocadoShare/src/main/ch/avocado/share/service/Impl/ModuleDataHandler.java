package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
            statement.setInt(SQLQueryConstants.Module.INSERT_QUERY_ID_INDEX, Integer.parseInt(id));
            statement.setString(SQLQueryConstants.Module.INSERT_QUERY_NAME_INDEX, module.getName());
            getConnectionHandler().insertDataSet(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return module.getId();
    }

    @Override
    public boolean deleteModule(Module module) throws DataHandlerException {
        return deleteAccessControlObject(module.getId());
    }

    private Module getModuleFromResult(ResultSet resultSet) throws DataHandlerException {
        String id, name, description;
        Date creationDate;
        try {
            if (!resultSet.next()) return null;
            id = Integer.toString(resultSet.getInt(SQLQueryConstants.Module.RESULT_INDEX_ID));
            name = resultSet.getString(SQLQueryConstants.Module.RESULT_INDEX_NAME);
            description = resultSet.getString(SQLQueryConstants.Module.RESULT_INDEX_DESCRIPTION);
            creationDate = resultSet.getDate(SQLQueryConstants.Module.RESULT_INDEX_CREATION_DATE);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return new Module(id, new ArrayList<Category>(), creationDate, 0.0f, "", description, name);
    }

    @Override
    public Module getModule(String moduleId) throws DataHandlerException {
        // TODO: categoeries, rating and ownerId
        ResultSet resultSet;
        try {
            PreparedStatement statement = getConnectionHandler().getPreparedStatement(SQLQueryConstants.Module.SELECT_QUERY);
            statement.setInt(SQLQueryConstants.Module.SELECT_QUERY_INDEX_ID, Integer.parseInt(moduleId));
            resultSet = getConnectionHandler().executeQuery(statement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return getModuleFromResult(resultSet);
    }

    @Override
    public List<Module> getModules(Collection<String> ids) throws DataHandlerException {
        List<Module> modules = new ArrayList<>(ids.size());
        for (String id : ids) {
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
            return success;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }
}
