package ch.avocado.share.service.Mock;

import ch.avocado.share.service.IDatabaseConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bergm on 15/03/2016.
 */
public class DatabaseConnectionHandlerMock implements IDatabaseConnectionHandler {
    @Override
    public ResultSet executeQuery(String query) {
        return null;
    }

    @Override
    public ResultSet executeQuery(PreparedStatement query) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement getPreparedStatement(String statement) throws SQLException {
        return null;
    }

    @Override
    public boolean insertDataSet(String query) {
        return false;
    }

    @Override
    public boolean deleteDataSet(String query) {
        return false;
    }

    @Override
    public boolean updateDataSet(String query) {
        return false;
    }
}
