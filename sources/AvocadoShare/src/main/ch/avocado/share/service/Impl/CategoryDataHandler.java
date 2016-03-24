package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IDatabaseConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kunzlio1 on 23.03.2016.
 */
public class CategoryDataHandler implements ICategoryDataHandler {
    private static final String SQL_SELECT_CATEGORY_BY_ID = "SELECT object_id, name " +
            "FROM category " +
            "WHERE object_id = {}"; //TODO: @kunzlio1: noch in db schauen ob es so auch stimmt...

    private static final String SQL_ADD_CATEGORY = "INSERT INTO category (object_id, name)" +
            "({}, '{}')";

    private static final String SQL_UPDATE_CATEGORY = "UPDATE category " +
            "SET name = '{}' " +
            "WHERE object_id = {}";

    //TODO @kunzlio1: Cyril fragen wieso genau in DB object_id u. name PK?
    //TODO @kunzlio1: Cyril fragen wie man Categorien an mehrere Objekte vergeben kann? Sieht für mich aus, als ob nur 1...
    //TODO @kunzlio1: Fragen wer jetzt genau für welche kontrolle zuständig ist...

    private IDatabaseConnectionHandler getDatabaseHandler() {
        try {
            return ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            return null;
        }
    }

    @Override
    public Category getCategory(String id) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_SELECT_CATEGORY_BY_ID);
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            return null;
        }
        //TODO: @kunzlio1: return result set, sobald an db angebunden...
        return null;
    }

    @Override
    public boolean addCategory(Category category, String accessObjectReferenceId) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_ADD_CATEGORY);
            preparedStatement.setString(1, accessObjectReferenceId);
            preparedStatement.setString(2, category.getName());
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean updateCategory(Category category) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connectionHandler.getPreparedStatement(SQL_UPDATE_CATEGORY);
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getId());
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteCategory(Category category) {
        IDatabaseConnectionHandler connectionHandler = getDatabaseHandler();
        if(connectionHandler == null) return false;
        return false;
    }

    @Override
    public Category getCategoryByName(String name) {
        return new Category("Test");
    }

    @Override
    public File[] getCategoryAssignedFiles(String id) {
        return new File[0];
    }
}
