package com.gavro.httpserver.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
import com.gavro.httpserver.exceptions.HttpVersionNotSupportedException;
import com.gavro.httpserver.http.HttpConstants;
import com.gavro.httpserver.http.HttpMethod;
import com.gavro.httpserver.http.HttpRequestParser;
import com.gavro.httpserver.http.HttpStatus;
import com.gavro.httpserver.utils.JsonResponseBuilder;

abstract public class RequestHandler {
    protected static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());
    protected final String requestLine;
    protected final HttpMethod method;
    protected final Map<String, String> requestHeaders;
    protected final OutputStream outputStream;
    protected final BufferedWriter writer;

    public RequestHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException, HttpVersionNotSupportedException {
        HttpRequestParser.ParsedRequestLine parsed = HttpRequestParser.parseRequestLine(requestLine);
        
        this.requestLine = requestLine;
        this.requestHeaders = headers;
        this.outputStream = outputStream;
        this.method = parsed.method();
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    abstract public void handleRequest() throws IOException;

    protected String determineContentType(File requestedResource) {
        String fileName = requestedResource.getName();
        int dotIndex = fileName.lastIndexOf('.');
        
        if (dotIndex == -1) {
            return HttpConstants.CONTENT_TYPE_OCTET_STREAM;
        }
        
        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        return switch (extension) {
            case "html", "htm" -> HttpConstants.CONTENT_TYPE_HTML;
            case "css" -> HttpConstants.CONTENT_TYPE_CSS;
            case "js" -> HttpConstants.CONTENT_TYPE_JS;
            case "json" -> HttpConstants.CONTENT_TYPE_JSON;
            case "txt" -> HttpConstants.CONTENT_TYPE_PLAIN;
            case "jpg", "jpeg" -> HttpConstants.CONTENT_TYPE_JPEG;
            case "png" -> HttpConstants.CONTENT_TYPE_PNG;
            case "gif" -> HttpConstants.CONTENT_TYPE_GIF;
            case "svg" -> HttpConstants.CONTENT_TYPE_SVG;
            case "ico" -> HttpConstants.CONTENT_TYPE_X_ICON;
            case "pdf" -> HttpConstants.CONTENT_TYPE_PDF;
            default -> HttpConstants.CONTENT_TYPE_OCTET_STREAM;
        };
    }

    protected static String formatHttpDate() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public static void writeHeadersWithBody(BufferedWriter writer, int statusCode, int contentLength, String contentType,
                                               Map<String, String> extraHeaders) throws IOException {
        writeHeadersNoFlush(writer, statusCode);
        writer.write("Content-Type: " + contentType + "\r\n");
        writer.write("Content-Length: " + contentLength + "\r\n");

        if (extraHeaders != null) {
            for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
                writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
            }
        }

        writer.write("\r\n");
        writer.flush();
    }

    protected static void writeHeadersWithoutBody(BufferedWriter writer, int statusCode, Map<String, String> extraHeaders) throws IOException {
        writeHeadersNoFlush(writer, statusCode);

        if (extraHeaders != null) {
            for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
                writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
            }
        }

        writer.write("\r\n");
        writer.flush();
    }

    private static void writeHeadersNoFlush(BufferedWriter writer, int statusCode) throws IOException {
        writer.write("HTTP/1.1 " + statusCode + " " + HttpStatus.getMessage(statusCode) + "\r\n");
        writer.write("Server: " + ServerConfig.SERVER_NAME + "\r\n");
        writer.write("Date: " + formatHttpDate() + "\r\n");
    }

    protected void handleUnsupportedMethod(List<String> supportedMethods) throws IOException {
        String allowedJsonArray = supportedMethods.stream()
                .map(method_ -> "\"" + method_ + "\"")
                .collect(Collectors.joining(", "));

        String responseBody = String.format(
                "{\"error\": \"Method not allowed\", \"allowed\": [%s]}",
                allowedJsonArray
        );

        Map<String, String> extraHeaders = Map.of(HttpConstants.HEADER_ALLOW, String.join(", ", supportedMethods));
        JsonResponseBuilder.sendJsonResponse(writer, outputStream, 405, responseBody, extraHeaders);
    }

    protected void handleNotFound() throws IOException {
        int statusCode = 404;
        JsonResponseBuilder.sendErrorResponse(writer, outputStream, statusCode,
                HttpStatus.getMessage(statusCode), "The requested resource was not found");
    }

    protected void handleInternalServerError() throws IOException {
        int statusCode = 500;
        JsonResponseBuilder.sendErrorResponse(writer, outputStream, statusCode, HttpStatus.getMessage(statusCode));
    }

    public static void handleBadRequest(OutputStream outputStream, String message) throws IOException {
        int statusCode = 400;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            JsonResponseBuilder.sendErrorResponse(writer, outputStream, statusCode,
                    HttpStatus.getMessage(statusCode), message);
        }
    }

    public static void handleHttpNotSupported(OutputStream outputStream, String message) throws IOException {
        int statusCode = 505;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            JsonResponseBuilder.sendErrorResponse(writer, outputStream, statusCode,
                    HttpStatus.getMessage(statusCode), message);
        }
    }
}
