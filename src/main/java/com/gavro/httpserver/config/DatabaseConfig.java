package com.gavro.httpserver.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DB_PROPERTIES_FILE = "/db.properties";
    
    private static Properties dbProperties;
    
    static {
        loadProperties();
    }
    
    private DatabaseConfig() {
        throw new UnsupportedOperationException("Configuration class");
    }
    
    private static void loadProperties() {
        dbProperties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + DB_PROPERTIES_FILE + " in classpath");
            }
            dbProperties.load(input);
            LOGGER.info("Database properties loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load database properties", e);
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    
    public static String getUrl() {
        return getProperty("db.url");
    }
    
    public static String getUser() {
        return getProperty("db.user");
    }
    
    public static String getPassword() {
        return getProperty("db.password");
    }
    
    private static String getProperty(String key) {
        String value = dbProperties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Missing required database property: " + key);
        }
        return value.trim();
    }
    
    public static void validateConfiguration() {
        getUrl();
        getUser();
        getPassword();
        LOGGER.info("Database configuration validated successfully");
    }
}
