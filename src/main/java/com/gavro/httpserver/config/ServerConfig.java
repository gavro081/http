package com.gavro.httpserver.config;


public final class ServerConfig {
    public static final int DEFAULT_PORT = 8000;
    public static final int DEFAULT_THREAD_POOL_SIZE = 10;
    public static final String DEFAULT_CONTENT_ROOT = "reactapp/dist";
    public static final String DEFAULT_INDEX_FILE = "index.html";
    public static final String SERVER_NAME = "GavroHTTP/1.0";
    public static final int SOCKET_TIMEOUT_MS = 30000;
    
    private ServerConfig() {
        throw new UnsupportedOperationException("Configuration class");
    }
}
