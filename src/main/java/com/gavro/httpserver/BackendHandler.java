package com.gavro.httpserver;

import com.gavro.httpserver.exceptions.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

public class BackendHandler extends RequestHandler{
    BackendHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException {
        super(requestLine, headers, outputStream);
    }

    @Override
    public void handleRequest() throws IOException {
        handleUnsupportedMethod(new ArrayList<>());
//        switch (method) {
//            default -> handleUnsupportedMethod();
//        }
    }
}
