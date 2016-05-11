package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.common.util.ChangeTrackingSet;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ch.avocado.share.common.constants.sql.CategoryConstants.*;

/**
 * Handler for categories
 */
public class CategoryDataHandler implements ICategoryDataHandler {

    @Override
    public void addAccessObjectCategories(AccessControlObjectBase accessObject) throws DataHandlerException {
        if (accessObject == null) throw new NullPointerException("accessObject is null");
        for (Category category : accessObject.getCategoryList()) {
            addCategory(category.getName(), accessObject.getId());
        }
    }

    @Override
    public void updateAccessObjectCategories(AccessControlObjectBase changedAccessObject) throws DataHandlerException, ObjectNotFoundException {
        if (changedAccessObject == null) throw new NullPointerException("changedAccessObject is null");
        ChangeTrackingSet<Category> categories = changedAccessObject.getCategoryList();
        for (Category newCategory : categories.getNewSet()) {
            addCategory(newCategory.getName(), changedAccessObject.getId());
        }
        for (Category delCategory : categories.getRemovedSet()) {
            deleteCategoryAssignedObject(delCategory.getName(), changedAccessObject.getId());
        }
    }

    @Override
    public Category getCategoryByName(String name) throws DataHandlerException, ObjectNotFoundException {
        if (name == null) throw new NullPointerException("name is null");
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SELECT_BY_NAME);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) throw new ObjectNotFoundException(Category.class, name);
            return createCategoryFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public boolean hasCategoryAssignedObject(String name, String accessObjectReferenceId) throws DataHandlerException {
        if (name == null) throw new NullPointerException("name is null");
        if (accessObjectReferenceId == null) throw new NullPointerException("accessObjectReferenceId is null");
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SELECT_BY_NAME_AND_OBJECT_ID);
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, Long.parseLong(accessObjectReferenceId));
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    @Override
    public List<Category> getAccessObjectAssignedCategories(String accessControlObjectId) throws DataHandlerException {
        ArrayList<Category> categories;
        if (accessControlObjectId == null) throw new NullPointerException("accessControlObjectId is null");
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_SELECT_CATEGORIES_BY_OBJECT_ID);
            preparedStatement.setLong(1, Long.parseLong(accessControlObjectId));
            resultSet = preparedStatement.executeQuery();
            Map<String, List<String>> categoryIdMap = createCategoryIdMapFromResultSet(resultSet);
            categories = new ArrayList<>(categoryIdMap.size());
            for (Map.Entry<String, List<String>> categoryEntry : categoryIdMap.entrySet()) {
                categories.add(new Category(categoryEntry.getKey(), categoryEntry.getValue()));
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return categories;
    }

    /**
     * @param resultSet The sql resultset
     * @return A map containing the category names a keys and a list of all objects ids
     *         assigned to the category as value.
     * @throws DataHandlerException
     * @throws SQLException
     */
    private Map<String, List<String>> createCategoryIdMapFromResultSet(ResultSet resultSet) throws DataHandlerException {
        HashMap<String, List<String>> categoryIdMap = new HashMap<>();
        try {
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                if (categoryIdMap.containsKey(name)) {
                    categoryIdMap.get(name).add(id);
                } else {
                    List<String> idList = new ArrayList<>();
                    idList.add(id);
                    categoryIdMap.put(name, idList);
                }
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return categoryIdMap;
    }

    private Category createCategoryFromResultSet(ResultSet resultSet) throws DataHandlerException {
        String name;
        Category category;
        try {
            List<String> objectIds = new LinkedList<>();
            name = resultSet.getString(2);
            do {
                String id = resultSet.getString(1);
                assert id != null;
                objectIds.add(id);
            } while (resultSet.next());
            category = new Category(name, objectIds);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return category;
    }

    private void addCategory(String name, String accessObjectReferenceId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_ADD_CATEGORY);
            preparedStatement.setLong(1, Long.parseLong(accessObjectReferenceId));
            preparedStatement.setString(2, name);
            connectionHandler.insertDataSet(preparedStatement);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private void deleteCategoryAssignedObject(String name, String accessObjectReferenceId) throws DataHandlerException, ObjectNotFoundException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_DELETE_CATEGORY_FROM_OBJECT);
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, Long.parseLong(accessObjectReferenceId));
            if(!connectionHandler.deleteDataSet(preparedStatement))
                throw new ObjectNotFoundException(Category.class, name + " " + accessObjectReferenceId);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private IDatabaseConnectionHandler getDatabaseHandler() throws DataHandlerException {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new DataHandlerException(e);
        }
    }
}
