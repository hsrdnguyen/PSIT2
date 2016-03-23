package ch.avocado.share.service.Mock;

import ch.avocado.share.service.IDatabaseConnectionHandler;
import java.sql.ResultSet;

/**
 * Created by bergm on 15/03/2016.
 */
public class DatabaseConnectionHandlerMock implements IDatabaseConnectionHandler {
    @Override
    public ResultSet executeQuery(String query) {
        return null;
    }

    @Override
    public String insertDataSet(String query) {
        return "";
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
