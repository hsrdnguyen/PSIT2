package ch.avocado.share.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IDatabaseConnectionHandler {

    /**
     * Returns a prepared statement to add variables into. Prevents SQL injection
     * @param query query to be executed later
     * @return prepared statement with query inserted
     */
    PreparedStatement getPreparedStatement(String query) throws SQLException;

    /**
     * Executes a select query on the database
     * @param statement statement to be executed on the database
     * @return java.sql resultset with the requested data
     */
    ResultSet executeQuery(PreparedStatement statement) throws SQLException;

    /**
     * Executes an insert query on the database
     * @param statement statement to be executed on the database
     * @return returns the key of the inserted dataset
     * @throws SQLException is the insertion fails
     */
    String insertDataSet(PreparedStatement statement) throws SQLException;

    /**
     * Executes an delete query on the database
     * @param statement statement to be executed on the database
     * @return returns true if delete was successful
     */
    boolean deleteDataSet(PreparedStatement statement) throws SQLException;

    /**
     * Executes an update query on the database
     * @param statement statement to be executed on the database
     * @return returns true if update was successful
     */
    boolean updateDataSet(PreparedStatement statement) throws SQLException;
}
