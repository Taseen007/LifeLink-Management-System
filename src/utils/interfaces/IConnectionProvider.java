package utils.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionProvider {
    Connection getConnection() throws SQLException;
    void closeConnection(Connection connection);
    void shutdown();
}
