package utils;

import utils.interfaces.IConnectionProvider;
import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection implements IConnectionProvider, AutoCloseable {
    private static final int MAX_POOL_SIZE = 10;
    private static final int CONNECTION_TIMEOUT = 30; // seconds
    private static volatile int currentConnections = 0;
    private final ConcurrentLinkedQueue<Connection> connectionPool;
    private final ScheduledExecutorService scheduler;
    private final Properties props;
    
    public DatabaseConnection() {
        this.connectionPool = new ConcurrentLinkedQueue<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.props = new Properties();
        
        try {
            // Try to load from config file
            props.load(new FileInputStream("config/database.properties"));
        } catch (IOException e) {
            System.out.println("Using default database configuration");
            // Use default values if config file is not found
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/lifelink_db");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "Taswar923");
        }

        // Initialize database driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }

        // Schedule connection validation every 5 minutes
        scheduler.scheduleAtFixedRate(this::validateConnections, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        Connection conn = connectionPool.poll();
        
        if (conn != null && isConnectionValid(conn)) {
            return conn;
        }

        if (conn != null) {
            closeConnectionQuietly(conn);
            currentConnections--;
        }

        if (currentConnections >= MAX_POOL_SIZE) {
            throw new SQLException("Connection pool limit reached");
        }

        conn = createNewConnection();
        currentConnections++;
        return conn;
    }

    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(CONNECTION_TIMEOUT);
        } catch (SQLException e) {
            return false;
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }

    @Override
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                if (currentConnections < MAX_POOL_SIZE && isConnectionValid(connection)) {
                    connection.setAutoCommit(true);
                    connectionPool.offer(connection);
                } else {
                    closeConnectionQuietly(connection);
                    currentConnections--;
                }
            } catch (SQLException e) {
                closeConnectionQuietly(connection);
                currentConnections--;
            }
        }
    }

    private void closeConnectionQuietly(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private void validateConnections() {
        int initialSize = connectionPool.size();
        for (int i = 0; i < initialSize; i++) {
            Connection conn = connectionPool.poll();
            if (conn != null && isConnectionValid(conn)) {
                connectionPool.offer(conn);
            } else if (conn != null) {
                closeConnectionQuietly(conn);
                currentConnections--;
            }
        }
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while (!connectionPool.isEmpty()) {
            Connection conn = connectionPool.poll();
            closeConnectionQuietly(conn);
            currentConnections--;
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}