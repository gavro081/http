package com.gavro.httpserver.http;

import java.util.Map;

public final class HttpStatus {
    private static final Map<Integer, String> STATUS_MESSAGES = Map.ofEntries(
            Map.entry(100, "Continue"),
            Map.entry(200, "OK"),
            Map.entry(201, "Created"),
            Map.entry(304, "Not Modified"),
            Map.entry(400, "Bad Request"),
            Map.entry(401, "Unauthorized"),
            Map.entry(403, "Forbidden"),
            Map.entry(404, "Not Found"),
            Map.entry(405, "Method Not Allowed"),
            Map.entry(409, "Conflict"),
            Map.entry(500, "Internal Server Error"),
            Map.entry(505, "HTTP Version Not Supported")
    );

    private HttpStatus() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getMessage(int statusCode) {
        String message = STATUS_MESSAGES.get(statusCode);
        if (message == null) {
            throw new IllegalArgumentException("Unknown status code: " + statusCode);
        }
        return message;
    }
}
