package com.gavro.httpserver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gavro.httpserver.config.ServerConfig;
import com.gavro.httpserver.exceptions.BadRequestException;

abstract public class RequestHandler {
    protected static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());
    protected final String requestLine;
    protected final HttpMethod method;
    protected final Map<String, String> requestHeaders;
    protected final OutputStream outputStream;
    protected final BufferedWriter writer;

    public RequestHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException {
        String[] parts = requestLine.split("\\s+");
        HttpMethod method = parseMethod(parts[0]);
        validateHttpVersion(parts[2]);

        this.requestLine = requestLine;
        this.requestHeaders = headers;
        this.outputStream = outputStream;
        this.method = method;
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public static RequestHandler fromRequest(String requestLine, Map<String, String> headers, OutputStream outputStream)
        throws IOException, BadRequestException {
        LOGGER.info("Processing request: " + requestLine);
        String[] parts = requestLine.split("\\s+");
        if (parts.length != 3) {
            throw new BadRequestException("Invalid request line format");
        }

        String target = parts[1];
        if (target.startsWith("/api/")) {
            return new BackendHandler(requestLine, headers, outputStream);
        } else {
            return new FrontendHandler(requestLine, headers, outputStream);
        }
    }

    abstract public void handleRequest() throws IOException;
    
    protected static HttpMethod parseMethod(String methodString) throws BadRequestException {
        try {
            return HttpMethod.valueOf(methodString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unsupported HTTP method: " + methodString);
        }
    }
    
    protected static void validateHttpVersion(String httpVersion) throws BadRequestException {
        if (!"HTTP/1.1".equals(httpVersion.trim())) {
            throw new BadRequestException("Unsupported HTTP version: " + httpVersion);
        }
    }

    protected String determineContentType(File requestedResource) {
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

    protected static String formatHttpDate() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    protected static void writeHeaders(BufferedWriter writer, int statusCode, int contentLength,
                                   String contentType, Map<String, String> extraHeaders) throws IOException {
        writer.write("HTTP/1.1 " + statusCode + " " + HttpStatus.getMessage(statusCode) + "\r\n");
        writer.write("Content-Type: " + contentType + "\r\n");
        writer.write("Content-Length: " + contentLength + "\r\n");
        writer.write("Server: " + ServerConfig.SERVER_NAME + "\r\n");
        writer.write("Date: " + formatHttpDate() + "\r\n");
//        writer.write("Connection: close\r\n");

        if (extraHeaders != null) {
            for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
                writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
            }
        }

        writer.write("\r\n");
        writer.flush();
    }

    protected void handleUnsupportedMethod(List<String> supportedMethods) throws IOException {
        String contentType = "application/json; charset=utf-8";
        String allowedJsonArray = supportedMethods.stream()
                .map(method -> "\"" + method + "\"")
                .collect(Collectors.joining(", "));

        String responseBody = String.format(
                "{\"error\": \"Method not allowed\", \"allowed\": [%s]}",
                allowedJsonArray
        );
        
        byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
        Map<String, String> extraHeaders = Map.of("Allow", String.join(", ", supportedMethods));
        writeHeaders(writer, 405, body.length, contentType, extraHeaders);
        outputStream.write(body);
        outputStream.flush();
    }

    protected void handleNotFound() throws IOException {
        String contentType = "application/json; charset=utf-8";
        String responseBody = "{\"error\": \"Not found\", \"message\": \"The requested resource was not found\"}";
        byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

        writeHeaders(writer, 404, body.length, contentType, null);
        outputStream.write(body);
        outputStream.flush();
    }

    protected void handleInternalServerError() throws IOException {
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
