package ch.avocado.share.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IDatabaseConnectionHandler {

    /**
     * Executes a select query on the database
     * @param query query to be executed on the database
     * @return java.sql resultset with the requested data
     */
    ResultSet executeQuery(String query) throws SQLException;

    ResultSet executeQuery(PreparedStatement query) throws SQLException;

    PreparedStatement getPreparedStatement(String statement) throws SQLException;

    /**
     * Executes an insert query on the database
     * @param query query to be executed on the database
     * @return returns true if insert was successful
     */
    boolean insertDataSet(String query) throws SQLException;

    /**
     * Executes an delete query on the database
     * @param query query to be executed on the database
     * @return returns true if delete was successful
     */
    boolean deleteDataSet(String query) throws SQLException;

    /**
     * Executes an update query on the database
     * @param query query to be executed on the database
     * @return returns true if update was successful
     */
    boolean updateDataSet(String query) throws SQLException;
}
