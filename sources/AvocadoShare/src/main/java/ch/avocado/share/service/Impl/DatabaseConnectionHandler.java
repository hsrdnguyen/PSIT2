package ch.avocado.share.service.Impl;

import ch.avocado.share.service.IDatabaseConnectionHandler;

import java.sql.*;

/**
 * Database connection handler.
 */
public class DatabaseConnectionHandler implements IDatabaseConnectionHandler {

    protected static Connection conn;

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    // JDBC database URL
    // dont modify. Use DatabaseConnectionHandlerMock instead.
    private static final String DB_URL = "jdbc:postgresql://srv-lab-t-944:5432/avocado_share";

    //  Database credentials
    private static final String USER = "avocado_tomcat";
    private static final String PASS = "77eb2c2e52824f26bd47f6d0bc6e1dcb";

    public DatabaseConnectionHandler() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        ensureConnection();
        return conn.prepareStatement(query);
    }

    @Override
    public ResultSet executeQuery(PreparedStatement statement) throws SQLException {
        ensureConnection();
        ResultSet result = statement.executeQuery();

        conn.close();
        return result;
    }

    @Override
    public String insertDataSet(PreparedStatement statement) throws SQLException {
        ensureConnection();

        Statement stmt = conn.createStatement();
        stmt.execute(statement.toString(), Statement.RETURN_GENERATED_KEYS);

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getString(1);
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    @Override
    public boolean deleteDataSet(PreparedStatement statement) throws SQLException {
        return updateDataSet(statement);
    }

    @Override
    public boolean updateDataSet(PreparedStatement statement) throws SQLException {
        ensureConnection();
        boolean result = statement.executeUpdate() != 0;
        conn.close();
        return result;
    }

    protected void ensureConnection() throws SQLException {
        if (conn == null || conn.isClosed())
        {
            conn =  DriverManager.getConnection(DB_URL,USER,PASS);
            Statement setSchema = conn.createStatement();
            setSchema.execute("SET search_path TO avocado_share;");
        }
    }
}
