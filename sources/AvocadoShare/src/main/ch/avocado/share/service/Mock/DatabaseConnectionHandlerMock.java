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
    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return null;
    }

    @Override
    public ResultSet executeQuery(PreparedStatement statement) throws SQLException {
        return null;
    }

    @Override
    public String insertDataSet(PreparedStatement statement) throws SQLException {
        return null;
    }

    @Override
    public boolean deleteDataSet(PreparedStatement statement) throws SQLException {
        return false;
    }

    @Override
    public boolean updateDataSet(PreparedStatement statement) throws SQLException {
        return false;
    }
}
