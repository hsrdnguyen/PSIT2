package ch.avocado.share.service.Impl;

import ch.avocado.share.service.IDatabaseConnectionHandler;

import java.sql.*;

/**
 * Created by bergm on 19/03/2016.
 */
public class DatabaseConnectionHandler implements IDatabaseConnectionHandler {

    private static Connection conn;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://srv-lab-t-944:5432/avocado_share";

    //  Database credentials
    static final String USER = "avocado_tomcat";
    static final String PASS = "77eb2c2e52824f26bd47f6d0bc6e1dcb";

    public DatabaseConnectionHandler() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        ensureConnection();
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(query);

        conn.close();
        return result;
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
        boolean result = stmt.executeUpdate(query) != 0;
        conn.close();
        return result;
    }

    private void ensureConnection() throws SQLException {
        if (conn == null || conn.isClosed())
        {
            conn =  DriverManager.getConnection(DB_URL,USER,PASS);
            Statement setSchema = conn.createStatement();
            setSchema.execute("SET search_path TO avocado_share;");
        }
    }


}
