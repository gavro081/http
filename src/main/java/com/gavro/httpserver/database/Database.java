package com.gavro.httpserver.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gavro.httpserver.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Database {
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static HikariDataSource dataSource;

    private Database() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static synchronized void init() {
        if (dataSource != null) {
            LOGGER.warning("Database already initialized");
            return;
        }
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.getUrl());
        config.setUsername(DatabaseConfig.getUser());
        config.setPassword(DatabaseConfig.getPassword());
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000); // 5s
        config.setIdleTimeout(60000);
        config.setMaxLifetime(300000);

        dataSource = new HikariDataSource(config);
        LOGGER.info("Database connection pool initialized");
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Database not initialized. Call init() first.");
        }
        return dataSource.getConnection();
    }

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("Database not initialized. Call init() first.");
        }
        return dataSource;
    }

    public static synchronized void shutdown() {
        if (dataSource != null){
            dataSource.close();
            dataSource = null;
            LOGGER.info("Database connection pool shut down");
        }
    }
}
