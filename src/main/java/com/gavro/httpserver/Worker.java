package com.gavro.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gavro.httpserver.config.ServerConfig;
import com.gavro.httpserver.exceptions.BadRequestException;

public class Worker implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Worker.class.getName());
    
    private final Socket socket;

    public Worker(Socket socket) throws SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(ServerConfig.SOCKET_TIMEOUT_MS);
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream outputStream = socket.getOutputStream()) {
            
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.trim().isEmpty()) {
                LOGGER.warning("Received empty request line");
                return;
            }
            
            Map<String, String> headers = parseHeaders(reader);
            
            RequestHandler handler = RequestHandler.fromRequest(requestLine, headers, outputStream);
            handler.handleRequest();
            
        } catch (BadRequestException e) {
            handleBadRequest(e.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error processing request", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error processing request", e);
        } finally {
            closeSocket();
        }
    }
    
    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":\\s*", 2);
            if (parts.length == 2) {
                headers.put(parts[0].toLowerCase(), parts[1]);
            }
        }
        
        return headers;
    }
    
    private void handleBadRequest(String message) {
        try (OutputStream outputStream = socket.getOutputStream()) {
            RequestHandler.handleBadRequest(outputStream, message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending bad request response", e);
        }
    }
    
    private void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing socket", e);
        }
    }
}
