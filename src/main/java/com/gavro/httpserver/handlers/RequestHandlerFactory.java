package com.gavro.httpserver.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gavro.httpserver.exceptions.BadRequestException;
import com.gavro.httpserver.exceptions.HttpVersionNotSupportedException;
import com.gavro.httpserver.http.HttpRequestParser;

public class RequestHandlerFactory {
    private static final Logger LOGGER = Logger.getLogger(RequestHandlerFactory.class.getName());
    
    private RequestHandlerFactory() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static RequestHandler createHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
            throws IOException, BadRequestException, HttpVersionNotSupportedException {
        LOGGER.log(Level.INFO, "Processing request: {0}", requestLine);
        
        HttpRequestParser.ParsedRequestLine parsed = HttpRequestParser.parseRequestLine(requestLine);
        String target = parsed.target();
        
        if (target.startsWith("/api/")) {
            return new BackendHandler(requestLine, headers, outputStream);
        } else {
            return new FrontendHandler(requestLine, headers, outputStream);
        }
    }
}
