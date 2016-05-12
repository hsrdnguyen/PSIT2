package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.Impl.DatabaseConnectionHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection handler which uses the test database.
 */
public class DatabaseConnectionHandlerMock extends DatabaseConnectionHandler implements IDatabaseConnectionHandler {

    // JDBC database URL
    private static final String DB_URL = "jdbc:postgresql://srv-lab-t-944:5432/avocado_test";

    //  Database credentials
    private static final String USER = "avocado_tomcat";
    private static final String PASS = "77eb2c2e52824f26bd47f6d0bc6e1dcb";
    private Connection conn = null;

    @Override
    protected Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return conn;
    }

    public static void use() throws IllegalAccessException {
        IDatabaseConnectionHandler connectionHandler;
        try {
            connectionHandler = ServiceLocator.getService(IDatabaseConnectionHandler.class);
        } catch (ServiceNotFoundException e) {
            connectionHandler = null;
        }
        if (connectionHandler == null || !connectionHandler.getClass().equals(DatabaseConnectionHandlerMock.class)) {
            ServiceLocatorModifier.setService(IDatabaseConnectionHandler.class, new DatabaseConnectionHandlerMock());
        }
    }
}
