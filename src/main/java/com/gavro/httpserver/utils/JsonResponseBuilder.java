package com.gavro.httpserver.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.gavro.httpserver.handlers.RequestHandler;
import com.gavro.httpserver.http.HttpConstants;

public final class JsonResponseBuilder {
    
    private JsonResponseBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void sendJsonResponse(BufferedWriter writer, OutputStream outputStream, 
                                      int statusCode, String jsonBody, Map<String, String> extraHeaders) throws IOException {
        byte[] body = jsonBody.getBytes(StandardCharsets.UTF_8);
        RequestHandler.writeHeadersWithBody(writer, statusCode, body.length, HttpConstants.CONTENT_TYPE_JSON, extraHeaders);
        outputStream.write(body);
        outputStream.flush();
    }
    
    public static String buildErrorJson(String error, String message) {
        return String.format("{\"error\": \"%s\", \"message\": \"%s\"}", 
                           escapeJson(error), escapeJson(message));
    }
    
    public static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
