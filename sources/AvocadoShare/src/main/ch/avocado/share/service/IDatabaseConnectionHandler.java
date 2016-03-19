package ch.avocado.share.service;

import java.sql.ResultSet;

/**
 * Created by bergm on 15/03/2016.
 */
public interface IDatabaseConnectionHandler {

    /**
     * Executes a select query on the database
     * @param query query to be executed on the database
     * @return java.sql resultset with the requested data
     */
    ResultSet executeQuery(String query);

    /**
     * Executes an insert query on the database
     * @param query query to be executed on the database
     * @return returns true if insert was successful
     */
    boolean insertDataSet(String query);

    /**
     * Executes an delete query on the database
     * @param query query to be executed on the database
     * @return returns true if delete was successful
     */
    boolean deleteDataSet(String query);

    /**
     * Executes an update query on the database
     * @param query query to be executed on the database
     * @return returns true if update was successful
     */
    boolean updateDataSet(String query);
}
