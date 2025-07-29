package com.gavro.httpserver.http;

import com.gavro.httpserver.exceptions.BadRequestException;
import com.gavro.httpserver.exceptions.HttpVersionNotSupportedException;

public final class HttpRequestParser {
    
    private HttpRequestParser() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static ParsedRequestLine parseRequestLine(String requestLine)
            throws BadRequestException, HttpVersionNotSupportedException {
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new BadRequestException("Request line cannot be null or empty");
        }
        
        String[] parts = requestLine.split("\\s+");
        if (parts.length != 3) {
            throw new BadRequestException("Invalid request line format: expected 3 parts, got " + parts.length);
        }
        
        HttpMethod method = parseMethod(parts[0]);
        String target = parts[1];
        String httpVersion = parts[2];
        
        validateHttpVersion(httpVersion);
        
        return new ParsedRequestLine(method, target, httpVersion);
    }
    
    public static HttpMethod parseMethod(String methodString) throws BadRequestException {
        if (methodString == null || methodString.trim().isEmpty()) {
            throw new BadRequestException("HTTP method cannot be null or empty");
        }
        
        try {
            return HttpMethod.valueOf(methodString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unsupported HTTP method: " + methodString);
        }
    }
    
    public static void validateHttpVersion(String httpVersion) throws HttpVersionNotSupportedException {
        if (httpVersion == null || !httpVersion.trim().equals("HTTP/1.1")) {
            throw new HttpVersionNotSupportedException();
        }
    }

    public record ParsedRequestLine(HttpMethod method, String target, String httpVersion) {
    }
}
