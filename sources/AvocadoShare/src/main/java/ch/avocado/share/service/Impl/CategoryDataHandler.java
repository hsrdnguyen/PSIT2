package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ch.avocado.share.common.constants.sql.CategoryConstants.*;

/**
 * Handler for categories
 */
public class CategoryDataHandler implements ICategoryDataHandler {
    /**
     * adds all categories from new created AccessControlObject to the database
     * @param accessObject the new created AccessControlObject
     * @return true if added all categories successful
     */
    @Override
    public boolean addAccessObjectCategories(AccessControlObjectBase accessObject) throws DataHandlerException {
        for (Category category : accessObject.getCategoryList()) {
            if (!addCategory(category.getName(), accessObject.getId()))
                return false;
        }
        return true;
    }

    /**
     * updates the categories from a AccessControlObjectBase object,
     * by passing the old Object on the database and the "new"/"changed" Object.
     * @param oldAccessObject       the AccessControlObject on the database
     * @param changedAccessObject   the "new"/"changed" AccessControlObject
     * @return true if updated successfully
     */
    @Override
    public boolean updateAccessObjectCategories(AccessControlObjectBase oldAccessObject,
                                                AccessControlObjectBase changedAccessObject) throws DataHandlerException {
        List<Category> delCategories = new ArrayList<>();
        List<Category> newCategories = new ArrayList<>();

        for (Category changedCategory : changedAccessObject.getCategoryList()) {
            if (!oldAccessObject.getCategoryList().contains(changedCategory)){
                newCategories.add(changedCategory);
            }
        }

        for (Category oldCategory : oldAccessObject.getCategoryList()) {
            if (!changedAccessObject.getCategoryList().contains(oldCategory)){
                delCategories.add(oldCategory);
            }
        }

        for (Category newCategory : newCategories) {
            if (!addCategory(newCategory.getName(), changedAccessObject.getId()))
                return false;
        }

        for (Category delCategory : delCategories) {
            if (!deleteCategoryAssignedObject(delCategory.getName(), oldAccessObject.getId()))
                return false;
        }

        return true;
    }

    /**
     * return the Category by passing the Category name
     * @param name the Category name
     * @return the Category object
     */
    @Override
    public Category getCategoryByName(String name) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SELECT_BY_NAME);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            return createCategoryFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    /**
     * checks if a Category is already added to a AccessControlObject
     * @param name                      the name of the Category
     * @param accessObjectReferenceId   the id of the AccessControlObject
     * @return true if the Category is already added to the AccessControlObject
     */
    @Override
    public boolean hasCategoryAssignedObject(String name, String accessObjectReferenceId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
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

    /**
     * Returns all categories, which are assigned to AccessControlObject.
     * @param accessControlObjectId the accessObjectId, for which the categories should be returned.
     * @return the accessObject assigned categories.
     */
    @Override
    public List<Category> getAccessObjectAssignedCategories(String accessControlObjectId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_SELECT_CATEGORIES_BY_OBJECT_ID);
            preparedStatement.setLong(1, Long.parseLong(accessControlObjectId));
            resultSet = preparedStatement.executeQuery();
            return createAccessObjectAssignedCategories(resultSet);
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    private Category createCategoryFromResultSet(ResultSet resultSet) throws DataHandlerException {
        String name;
        Category category = null;
        try {
            if(resultSet.next()) {
                List<String> objectIds = new LinkedList<>();
                name = resultSet.getString(2);
                do {
                    String id = resultSet.getString(1);
                    assert id != null;
                    objectIds.add(id);
                }while (resultSet.next());
                category = new Category(name, objectIds);
            }
        }catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return category;
    }

    private List<Category> createAccessObjectAssignedCategories(ResultSet resultSet) throws DataHandlerException {
        List<Category> categories = new ArrayList<>();
        try {
            while(resultSet.next()) {
                Category category = getCategoryByName(resultSet.getString(1));
                if(category == null) {
                    return null;
                }
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return categories;
    }

    private boolean addCategory(String name, String accessObjectReferenceId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(hasCategoryAssignedObject(name, accessObjectReferenceId)) return true;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_ADD_CATEGORY);
            preparedStatement.setLong(1, Long.parseLong(accessObjectReferenceId));
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
        return true;
    }

    private boolean deleteCategoryAssignedObject(String name, String accessObjectReferenceId) throws DataHandlerException {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_DELETE_CATEGORY_FROM_OBJECT);
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, Long.parseLong(accessObjectReferenceId));
            return connectionHandler.deleteDataSet(preparedStatement);
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
