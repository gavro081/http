package com.gavro.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gavro.httpserver.config.DatabaseConfig;
import com.gavro.httpserver.config.ServerConfig;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    
    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean running = false;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(ServerConfig.DEFAULT_THREAD_POOL_SIZE);
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server listening on port: " + port);
            
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(new Worker(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        LOGGER.log(Level.WARNING, "Error accepting client connection", e);
                    }
                } catch (Exception e) {
                    if (running) {
                        LOGGER.log(Level.WARNING, "Error creating worker for client", e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server socket error", e);
        } finally {
            shutdown();
        }
    }
    
    public void shutdown() {
        running = false;
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Server shutdown complete");
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : ServerConfig.DEFAULT_PORT;

        try {
            DatabaseConfig.validateConfiguration();

            try (Connection conn = DriverManager.getConnection(
                    DatabaseConfig.getUrl(), 
                    DatabaseConfig.getUser(), 
                    DatabaseConfig.getPassword())) {
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM example;");
                while (rs.next()) {
                    System.out.println("User: " + rs.getString("name"));
                }
                LOGGER.info("Database connection test successful");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Configuration error", e);
        }
        
        // Server server = new Server(port);
        // Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        // server.run();
    }
}
