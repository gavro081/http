package com.gavro.httpserver;

import com.gavro.httpserver.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static HikariDataSource dataSource;

    public static void init() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.getUrl());
        config.setJdbcUrl(DatabaseConfig.getUrl());
        config.setPassword(DatabaseConfig.getPassword());
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000); // 5s
        config.setIdleTimeout(60000);
        config.setMaxLifetime(300000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null){
            dataSource.close();
        }
    }
}
