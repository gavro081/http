package com.gavro.httpserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;

import com.gavro.httpserver.config.ServerConfig;
import com.gavro.httpserver.exceptions.BadRequestException;

public class RequestHandler {
    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());
    
    private final HttpMethod method;
    private final File requestedResource;
    private final Map<String, String> requestHeaders;
    private final OutputStream outputStream;
    private final BufferedWriter writer;

    public RequestHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
            throws IOException, BadRequestException {
        LOGGER.info("Processing request: " + requestLine);
        
        this.outputStream = outputStream;
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        this.requestHeaders = headers;

        String[] parts = requestLine.split("\\s+");
        if (parts.length != 3) {
            throw new BadRequestException("Invalid request line format");
        }

        this.method = parseMethod(parts[0]);
        validateHttpVersion(parts[2]);
        this.requestedResource = resolveResource(parts[1]);
    }
    
    private HttpMethod parseMethod(String methodString) throws BadRequestException {
        try {
            return HttpMethod.valueOf(methodString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unsupported HTTP method: " + methodString);
        }
    }
    
    private void validateHttpVersion(String httpVersion) throws BadRequestException {
        if (!"HTTP/1.1".equals(httpVersion.trim())) {
            throw new BadRequestException("Unsupported HTTP version: " + httpVersion);
        }
    }

    private File resolveResource(String requestTarget) throws BadRequestException, IOException {
        try {
            Path basePath = Path.of(ServerConfig.DEFAULT_CONTENT_ROOT).toAbsolutePath().normalize();
            String relativePath = "/".equals(requestTarget) ? ServerConfig.DEFAULT_INDEX_FILE : requestTarget.substring(1);
            Path resolvedPath = basePath.resolve(relativePath).normalize();
            
            if (!resolvedPath.startsWith(basePath)) {
                throw new BadRequestException("Forbidden: Directory traversal attempt");
            }

            File targetFile = resolvedPath.toFile();
            if (!targetFile.exists()) {
                return basePath.resolve(ServerConfig.DEFAULT_INDEX_FILE).toFile();
            }
            
            if (targetFile.isDirectory()) {
                File indexFile = new File(targetFile, ServerConfig.DEFAULT_INDEX_FILE);
                return indexFile.exists() ? indexFile : basePath.resolve(ServerConfig.DEFAULT_INDEX_FILE).toFile();
            }
            
            return targetFile;
        } catch (Exception e) {
            throw new BadRequestException("Invalid request target: " + requestTarget);
        }
    }

    private String determineContentType() {
        String fileName = requestedResource.getName();
        int dotIndex = fileName.lastIndexOf('.');
        
        if (dotIndex == -1) {
            return "application/octet-stream";
        }
        
        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        return switch (extension) {
            case "html", "htm" -> "text/html; charset=utf-8";
            case "css" -> "text/css; charset=utf-8";
            case "js" -> "application/javascript; charset=utf-8";
            case "json" -> "application/json; charset=utf-8";
            case "txt" -> "text/plain; charset=utf-8";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";
            case "pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }

    private static String formatHttpDate() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    private static void writeHeaders(BufferedWriter writer, int statusCode, int contentLength, 
                                   String contentType, Map<String, String> extraHeaders) throws IOException {
        writer.write("HTTP/1.1 " + statusCode + " " + HttpStatus.getMessage(statusCode) + "\r\n");
        writer.write("Content-Type: " + contentType + "\r\n");
        writer.write("Content-Length: " + contentLength + "\r\n");
        writer.write("Server: " + ServerConfig.SERVER_NAME + "\r\n");
        writer.write("Date: " + formatHttpDate() + "\r\n");
//        writer.write("Connection: close\r\n"); todo

        if (extraHeaders != null) {
            for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
                writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
            }
        }

        writer.write("\r\n");
        writer.flush();
    }

    public void handleRequest() throws IOException {
        switch (method) {
            case GET, HEAD -> handleGetOrHead();
            default -> handleUnsupportedMethod();
        }
    }

    private void handleGetOrHead() throws IOException {
        try {
            if (!requestedResource.exists() || !requestedResource.canRead()) {
                handleNotFound();
                return;
            }

            String contentType = determineContentType();
            byte[] body = Files.readAllBytes(requestedResource.toPath());

            writeHeaders(writer, 200, body.length, contentType, null);

            if (method == HttpMethod.GET) {
                outputStream.write(body);
                outputStream.flush();
            }
        } catch (IOException e) {
            LOGGER.severe("Error serving file: " + requestedResource.getPath());
            handleInternalServerError();
        }
    }

    private void handleUnsupportedMethod() throws IOException {
        String contentType = "application/json; charset=utf-8";
        String responseBody = "{\"error\": \"Method not allowed\", \"allowed\": [\"GET\", \"HEAD\"]}";
        byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

        Map<String, String> extraHeaders = Map.of("Allow", "GET, HEAD");
        writeHeaders(writer, 405, body.length, contentType, extraHeaders);
        outputStream.write(body);
        outputStream.flush();
    }

    private void handleNotFound() throws IOException {
        String contentType = "application/json; charset=utf-8";
        String responseBody = "{\"error\": \"Not found\", \"message\": \"The requested resource was not found\"}";
        byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

        writeHeaders(writer, 404, body.length, contentType, null);
        outputStream.write(body);
        outputStream.flush();
    }

    private void handleInternalServerError() throws IOException {
        String contentType = "application/json; charset=utf-8";
        String responseBody = "{\"error\": \"Internal server error\"}";
        byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

        writeHeaders(writer, 500, body.length, contentType, null);
        outputStream.write(body);
        outputStream.flush();
    }

    public static void handleBadRequest(OutputStream outputStream, String message) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            String contentType = "application/json; charset=utf-8";
            String responseBody = "{\"error\": \"Bad request\", \"message\": \"" + 
                                 message.replace("\"", "\\\"") + "\"}";
            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            
            writeHeaders(writer, 400, body.length, contentType, null);
            outputStream.write(body);
            outputStream.flush();
        }
    }
}
