package ch.avocado.share.service.Impl;

import ch.avocado.share.service.IDatabaseConnectionHandler;

import java.sql.*;

/**
 * Created by bergm on 19/03/2016.
 */
public class DatabaseConnectionHandler implements IDatabaseConnectionHandler {

    private static Connection conn;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/EMP";

    //  Database credentials
    static final String USER = "avocado_tomcat";
    static final String PASS = "77eb2c2e52824f26bd47f6d0bc6e1dcb";

    public DatabaseConnectionHandler() {
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        ensureConnection();
        Statement stmt = conn.createStatement();

        conn.close();
        return stmt.executeQuery(query);
    }

    @Override
    public boolean insertDataSet(String query) throws SQLException {
        return updateDataSet(query);
    }

    @Override
    public boolean deleteDataSet(String query) throws SQLException {
        return updateDataSet(query);
    }

    @Override
    public boolean updateDataSet(String query) throws SQLException {
        ensureConnection();
        Statement stmt = conn.createStatement();

        conn.close();
        return stmt.executeUpdate(query) != 0;
    }

    private void ensureConnection() throws SQLException {
        if (conn == null || conn.isClosed())
        {
            conn =  DriverManager.getConnection(DB_URL,USER,PASS);
        }
    }


}
